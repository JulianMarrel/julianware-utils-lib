package org.julianware.utilslib.string;


/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 15/10/2021
 */
public class StringUtils {

    public static String camelToSnake(final String camelString) {
        String ret = camelString.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2").replaceAll("([a-z])([A-Z])", "$1_$2");
        return ret.toLowerCase();
    }
}
