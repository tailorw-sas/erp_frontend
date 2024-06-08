package com.kynsof.share.utils;

public class SplitNames {
    public static String[] splitNombresApellidos(String nombreCompleto) {
        String[] nombresApellidos = new String[3];

        String[] palabras = nombreCompleto.split(" ");

        // Los apellidos son las últimas dos palabras
        nombresApellidos[1] = palabras[palabras.length - 2] + " " + palabras[palabras.length - 1];

        // Los nombres son el resto de las palabras
        StringBuilder nombres = new StringBuilder();
        for (int i = 0; i < palabras.length - 2; i++) {
            nombres.append(palabras[i]);
            if (i != palabras.length - 3) {
                nombres.append(" ");
            }
        }
        nombresApellidos[0] = nombres.toString();

        return nombresApellidos;
    }
}
