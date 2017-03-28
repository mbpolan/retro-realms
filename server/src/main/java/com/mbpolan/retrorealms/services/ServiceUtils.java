package com.mbpolan.retrorealms.services;

/**
 * Collection of various utility methods.
 *
 * @author mbpolan
 */
public final class ServiceUtils {

    public static String getBasename(String path) {
        return tokenizeFileName(path)[0];
    }

    public static String getExtension(String path) {
        String[] tokens = tokenizeFileName(path);
        return tokens[tokens.length - 1];
    }

    private static String[] tokenizeFileName(String file) {
        return file.split("\\.(?=[^\\.]+$)");
    }

    private ServiceUtils() {
    }
}
