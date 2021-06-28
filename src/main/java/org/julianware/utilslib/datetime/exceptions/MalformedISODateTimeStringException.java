package org.julianware.utilslib.datetime.exceptions;


/**
 * @author Julian Marrel <julian.marrel@smile.fr>
 * @created 12/05/2019
 */
public class MalformedISODateTimeStringException
        extends Exception {

    public MalformedISODateTimeStringException(final String message) {
        super(message);
    }
}
