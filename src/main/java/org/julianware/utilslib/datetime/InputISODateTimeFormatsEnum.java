package org.julianware.utilslib.datetime;

import org.julianware.utilslib.datetime.exceptions.MalformedISODateTimeStringException;


/**
 * @author Julian Marrel <julian.marrel@smile.fr>
 * @created 11/05/2021
 */
public enum InputISODateTimeFormatsEnum {
    YEAR,
    MONTH,
    DAY,
    HOUR,
    MINUTE,
    SECOND;

    public static InputISODateTimeFormatsEnum of(final String ISODateTimeAsString) throws MalformedISODateTimeStringException {
        final int ISODateTimeAsStringRefSize = ISODateTimeAsString.trim().length() - "yyyy".length();
        if (ISODateTimeAsStringRefSize % 3 != 0) {
            throw new MalformedISODateTimeStringException("Unexpected ISO datetime String: '" + ISODateTimeAsString + "'.");
        }
        final int formatIndex = ISODateTimeAsStringRefSize / 3;
        return InputISODateTimeFormatsEnum.values()[formatIndex];
    }
}
