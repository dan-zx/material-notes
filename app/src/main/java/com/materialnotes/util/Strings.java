package com.materialnotes.util;

/**
 * Clase con metodos y constantes miscelaneos de String.
 *
 * @author Daniel Pedraza Arcega
 */
public final class Strings {

    /** Cadena vacia "". */
    public static final String EMPTY = "";

    /** Constructor. No debe ser invocado. */
    private Strings() {
        throw new IllegalAccessError("This class cannot be instantiated nor extended");
    }

    /**
     * Revisa si el string dado es {@code null} o vacio.
     *
     * @param str el string a revisar.
     * @return {@code true} si es {@code null} o vacio; sino {@code false}.
     */
    public static boolean isNullOrBlank(String str) {
        return str == null || str.trim().length() == 0;
    }
}