package org.julianware.utilslib.objects;

import java.util.Collection;
import java.util.Objects;


/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 03/09/2021
 */
public class JulianwareObjects {

    public static <T> T requireNonEmpty(final T object, final String message) {
        final T nonNullObject = Objects.requireNonNull(object, message);
        if (nonNullObject instanceof String) {
            if (((String) nonNullObject).isBlank()) {
                throw new IllegalStateException(message);
            }
        } else if (nonNullObject instanceof Collection) {
            if (((Collection<?>) nonNullObject).isEmpty()) {
                throw new IllegalStateException(message);
            }
        }
        return nonNullObject;
    }
}
