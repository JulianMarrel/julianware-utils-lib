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

    public static String snakeToCamel(final String snakeString) {
        String camelString = snakeString.substring(0, 1).toUpperCase() + snakeString.substring(1);

        while (camelString.contains("_")) {
            camelString = camelString.replaceFirst(
                    "_[a-z]",
                    String.valueOf(
                            Character.toUpperCase(
                                    camelString.charAt(
                                            camelString.indexOf("_") + 1
                                    )
                            )
                    )
            );
        }
        return camelString;
    }
}
