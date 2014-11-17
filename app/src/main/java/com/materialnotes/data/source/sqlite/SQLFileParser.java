package com.materialnotes.data.source.sqlite;

import android.util.Log;

import com.materialnotes.util.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * Clase para parsear archivos *.sql
 *
 * @author Daniel Pedraza Arcega
 */
class SQLFileParser {

    private static final String TAG = SQLFileParser.class.getSimpleName();
    private static final Pattern COMMENT_PATTERN = Pattern.compile("(?:/\\*[^;]*?\\*/)|(?:--[^;]*?$)", Pattern.DOTALL | Pattern.MULTILINE);

    /**
     * Regresa todas las sentencias SQL contenidas en un archivo *.sql
     *
     * @param stream el stream del archivo *.sql
     * @return las sentencias SQL.
     */
    static String[] getSQLStatements(InputStream stream) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(stream));
            int r;
            StringBuilder sb = new StringBuilder();
            while ((r = reader.read()) != -1) sb.append((char) r);
            return COMMENT_PATTERN.matcher(sb).replaceAll(Strings.EMPTY).split(";");
        } catch (IOException ex) {
            Log.e(TAG, "Unable to parse SQL Statements", ex);
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Log.e(TAG, "Unable to close stream", ex);
                }
            }
        }
    }
}