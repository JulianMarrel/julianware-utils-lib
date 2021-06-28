package org.julianware.utilslib.datetime.exceptions;


/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 19/05/2019
 */
public class DateTimeUtilsException
extends Exception {

    public DateTimeUtilsException(final String message) {
        super(message);
    }

    public DateTimeUtilsException(final String message, final Throwable t) {
        super(message, t);
    }
}
