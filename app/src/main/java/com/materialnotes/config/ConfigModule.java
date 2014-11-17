package com.materialnotes.config;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import com.materialnotes.data.source.sqlite.NotesDatabaseHelper;

/**
 * Clase para cablear dependencias de la aplicación
 *
 * @author Daniel Pedraza Arcega
 */
public class ConfigModule extends AbstractModule {

    private final Application context;

    public ConfigModule(Application context) {
        this.context = context;
    }

    /** Cablea las implementaciones. */
    @Override
    protected void configure() {
        bind(SQLiteOpenHelper.class)
                .annotatedWith(Names.named("NotesDbHelper"))
                .toInstance(new NotesDatabaseHelper(context));
    }
}