package com.materialnotes.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.materialnotes.R;
import com.materialnotes.data.Note;
import com.materialnotes.data.dao.NoteDAO;
import com.materialnotes.view.ShowHideOnScroll;
import com.materialnotes.widget.AboutNoticeDialog;
import com.materialnotes.widget.NotesAdapter;

import com.shamanland.fab.FloatingActionButton;

import java.util.ArrayList;

import javax.inject.Inject;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Actividad principal que presenta una lista de notas.
 *
 * @author Daniel Pedraza Arcega
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActionBarActivity {

    private static final int NEW_NOTE_RESULT_CODE = 4;
    private static final int EDIT_NOTE_RESULT_CODE = 5;

    @InjectView(android.R.id.empty)   private TextView emptyListTextView;
    @InjectView(android.R.id.list)    private ListView listView;
    @InjectView(R.id.add_note_button) private FloatingActionButton addNoteButton;

    @Inject private NoteDAO noteDAO;

    private ArrayList<Integer> selectedPositions;
    private ArrayList<NotesAdapter.NoteViewWrapper> notesData;
    private NotesAdapter listAdapter;
    private ActionMode.Callback actionModeCallback;
    private ActionMode actionMode;

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializa los componentes //////////////////////////////////////////////////////////////
        listView.setOnTouchListener(new ShowHideOnScroll(addNoteButton, getSupportActionBar())); // Esconde o muesta el FAB y la Action Bar
        addNoteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Crear una nota nueva
                startActivityForResult(EditNoteActivity.buildIntent(MainActivity.this), NEW_NOTE_RESULT_CODE);
            }
        });
        selectedPositions = new ArrayList<>();
        setupNotesAdapter();
        setupActionModeCallback();
        setListOnItemClickListenersWhenNoActionMode();
        updateView();
    }

    /** {@inheritDoc} */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about_info:
                new AboutNoticeDialog()
                        .show(getSupportFragmentManager(), "dialog_about_notice");
                return true;
            case R.id.action_licenses_info:
                WebView webView = new WebView(this);
                webView.loadUrl("file:///android_asset/licenses.html");
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_licenses_notice_title)
                        .setView(webView)
                        .setCancelable(true)
                        .show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_NOTE_RESULT_CODE) {
            if (resultCode == RESULT_OK) addNote(data);
        }
        if (requestCode == EDIT_NOTE_RESULT_CODE) {
            if (resultCode == RESULT_OK) updateNote(data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /** Crea la llamada al modo contextual. */
    private void setupActionModeCallback() {
        actionModeCallback = new ActionMode.Callback() {

            /** {@inheritDoc} */
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                setListOnItemClickListenersWhenActionMode();
                // inflar menu contextual
                mode.getMenuInflater().inflate(R.menu.context_note, menu);
                return true;
            }

            /** {@inheritDoc} */
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Nada
                return false;
            }

            /** {@inheritDoc} */
            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    // borrar notas solo si hay notas a borrar; sino se acaba el modo contextual.
                    case R.id.action_delete:
                        if (!selectedPositions.isEmpty()) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setMessage(getString(R.string.delete_notes_alert, selectedPositions.size()))
                                    .setNegativeButton(android.R.string.no, null)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteNotes(selectedPositions);
                                            mode.finish();
                                        }
                                    })
                                    .show();
                        } else mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            /** {@inheritDoc} */
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Regresar al modo normal
                setListOnItemClickListenersWhenNoActionMode();
                resetSelectedListItems();
            }
        };
    }

    /** Inicializa el adaptador de notas. */
    private void setupNotesAdapter() {
        notesData = new ArrayList<>();
        for (Note note : noteDAO.fetchAll()) { // Convertir a wrapper
            NotesAdapter.NoteViewWrapper noteViewWrapper = new NotesAdapter.NoteViewWrapper(note);
            notesData.add(noteViewWrapper);
        }
        listAdapter = new NotesAdapter(notesData);
        listView.setAdapter(listAdapter);
    }

    /** Actualiza la vista de esta actividad cuando hay notas o no hay notas. */
    private void updateView() {
        if (notesData.isEmpty()) { // Mostrar mensaje
            listView.setVisibility(View.GONE);
            emptyListTextView.setVisibility(View.VISIBLE);
        } else { // Mostrar lista
            listView.setVisibility(View.VISIBLE);
            emptyListTextView.setVisibility(View.GONE);
        }
    }

    /**
     * Agrega una nota a lista y la fuente de datos.
     *
     * @param data los datos de la actividad de edición de notas.
     */
    private void addNote(Intent data) {
        Note note = EditNoteActivity.getExtraNote(data);
        noteDAO.insert(note);
        NotesAdapter.NoteViewWrapper noteViewWrapper = new NotesAdapter.NoteViewWrapper(note);
        notesData.add(noteViewWrapper);
        updateView();
        listAdapter.notifyDataSetChanged();
    }

    /**
     * Borra notas de la lista y de la fuente de datos.
     *
     * @param selectedPositions las posiciones de las notas en la lista.
     */
    private void deleteNotes(ArrayList<Integer> selectedPositions) {
        ArrayList<NotesAdapter.NoteViewWrapper> toRemoveList = new ArrayList<>(selectedPositions.size());
        // Primero borra de la base de datos
        for (int position : selectedPositions) {
            NotesAdapter.NoteViewWrapper noteViewWrapper = notesData.get(position);
            toRemoveList.add(noteViewWrapper);
            noteDAO.delete(noteViewWrapper.getNote());
        }
        // Y luego de la vista (no al mismo tiempo porque pierdo las posiciones que hay que borrar)
        for (NotesAdapter.NoteViewWrapper noteToRemove : toRemoveList) notesData.remove(noteToRemove);
        updateView();
        listAdapter.notifyDataSetChanged();
    }

    /**
     * Actualiza una nota en la lista y la fuente de datos.
     *
     * @param data los datos de la actividad de edición de notas.
     */
    private void updateNote(Intent data) {
        Note updatedNote = ViewNoteActivity.getExtraUpdatedNote(data);
        noteDAO.update(updatedNote);
        for (NotesAdapter.NoteViewWrapper noteViewWrapper : notesData) {
            // Buscar la nota vieja para actulizarla en la vista
            if (noteViewWrapper.getNote().getId().equals(updatedNote.getId())) {
                noteViewWrapper.getNote().setTitle(updatedNote.getTitle());
                noteViewWrapper.getNote().setContent(updatedNote.getContent());
                noteViewWrapper.getNote().setUpdatedAt(updatedNote.getUpdatedAt());
            }
        }
        listAdapter.notifyDataSetChanged();
    }

    /** Reinicia las notas seleccionadas a no seleccionadas y limpia la lista de seleccionados. */
    private void resetSelectedListItems() {
        for (NotesAdapter.NoteViewWrapper noteViewWrapper : notesData) noteViewWrapper.setSelected(false);
        selectedPositions.clear();
        listAdapter.notifyDataSetChanged();
    }

    /**
     * Inicializa las acciones de la lista al hacer click en sus items cuando NO esta activo el
     * modo contextual.
     */
    private void setListOnItemClickListenersWhenNoActionMode() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Ver la nota al hacer click
                startActivityForResult(ViewNoteActivity.buildIntent(MainActivity.this, notesData.get(position).getNote()), EDIT_NOTE_RESULT_CODE);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Iniciar modo contextual para selección de items
                notesData.get(position).setSelected(true);
                listAdapter.notifyDataSetChanged();
                selectedPositions.add(position);
                actionMode = startSupportActionMode(actionModeCallback);
                actionMode.setTitle(String.valueOf(selectedPositions.size()));
                return true;
            }
        });
    }

    /**
     * Inicializa las acciones de la lista al hacer click en sus items cuando esta activo el menu
     * contextual.
     */
    private void setListOnItemClickListenersWhenActionMode() {
        listView.setOnItemLongClickListener(null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Agregar items a la lista de seleccionados y cambiarles el fondo.
                // Si se deseleccionan todos los items, se acaba el modo contextual
                if (selectedPositions.contains(position)) {
                    selectedPositions.remove((Object)position); // no quiero el índice sino el objeto
                    if (selectedPositions.isEmpty()) actionMode.finish();
                    else {
                        actionMode.setTitle(String.valueOf(selectedPositions.size()));
                        notesData.get(position).setSelected(false);
                        listAdapter.notifyDataSetChanged();
                    }
                } else {
                    notesData.get(position).setSelected(true);
                    listAdapter.notifyDataSetChanged();
                    selectedPositions.add(position);
                    actionMode.setTitle(String.valueOf(selectedPositions.size()));
                }
            }
        });
    }
}