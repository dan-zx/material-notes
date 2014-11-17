package com.materialnotes.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.materialnotes.R;
import com.materialnotes.data.Note;

import com.materialnotes.view.ShowHideOnScroll;
import com.shamanland.fab.FloatingActionButton;

import java.text.DateFormat;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Actividad para visualizar una nota. Adicionalmente, se puede editar la nota en otra actividad.
 *
 * @author Daniel Pedraza Arcega
 */
@ContentView(R.layout.activity_view_note)
public class ViewNoteActivity extends RoboActionBarActivity {

    private static final int EDIT_NOTE_RESULT_CODE = 8;
    private static final String EXTRA_NOTE = "EXTRA_NOTE";
    private static final String EXTRA_UPDATED_NOTE = "EXTRA_UPDATED_NOTE";
    private static final DateFormat DATETIME_FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

    @InjectView(R.id.scroll_view)          private ScrollView scrollView;
    @InjectView(R.id.edit_note_button)     private FloatingActionButton editNoteButton;
    @InjectView(R.id.note_title)           private TextView noteTitleText;
    @InjectView(R.id.note_content)         private TextView noteContentText;
    @InjectView(R.id.note_created_at_date) private TextView noteCreatedAtDateText;
    @InjectView(R.id.note_updated_at_date) private TextView noteUpdatedAtDateText;

    private Note note;

    /**
     * Construye el Intent para llamar a esta actividad.
     *
     * @param context el contexto que la llama.
     * @param note la nota a visualizar.
     * @return un Intent.
     */
    public static Intent buildIntent(Context context, Note note) {
        Intent intent = new Intent(context, ViewNoteActivity.class);
        intent.putExtra(EXTRA_NOTE, note);
        return intent;
    }

    /**
     * Recupera la nota actualizada en la actividad de edición de notas.
     *
     * @param intent el Intent que vine en onActivityResult
     * @return la nota actualizada
     */
    public static Note getExtraUpdatedNote(Intent intent) {
        return (Note) intent.getExtras().get(EXTRA_UPDATED_NOTE);
    }

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializa los componentes //////////////////////////////////////////////////////////////
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Muestra la flecha hacia atrás
        scrollView.setOnTouchListener(new ShowHideOnScroll(editNoteButton, getSupportActionBar())); // Esconde o muesta el FAB y la Action Bar
        editNoteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Ir a la actividad de edición de notas
                startActivityForResult(EditNoteActivity.buildIntent(ViewNoteActivity.this, note), EDIT_NOTE_RESULT_CODE);
            }
        });
        note = (Note) getIntent().getSerializableExtra(EXTRA_NOTE); // Recuperar la nota del Intent
        // Mostrar la información de la nota en el layout
        noteTitleText.setText(note.getTitle());
        noteContentText.setText(note.getContent());
        noteCreatedAtDateText.setText(DATETIME_FORMAT.format(note.getCreatedAt()));
        noteUpdatedAtDateText.setText(DATETIME_FORMAT.format(note.getUpdatedAt()));
    }

    /** {@inheritDoc} */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed(); // Cerrar esta actividad
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_NOTE_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                // La nota fue editada correctamente y debe finalizar esta actividad con un resultado
                Intent resultIntent = new Intent();
                Note note = EditNoteActivity.getExtraNote(data);
                resultIntent.putExtra(EXTRA_UPDATED_NOTE, note);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else if (resultCode == RESULT_CANCELED) onBackPressed();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /** {@inheritDoc} */
    @Override
    public void onBackPressed() {
        // No se edito la nota
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }
}