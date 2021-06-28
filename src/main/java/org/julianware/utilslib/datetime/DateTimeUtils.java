package org.julianware.utilslib.datetime;


import org.julianware.utilslib.datetime.exceptions.DateTimeUtilsException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;


/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 19/05/2019
 */
public class DateTimeUtils {

    private static final String REFERENCE_START_DATE_AS_STRING = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.of(LocalDate.now().getYear(), Month.JANUARY, 1, 0, 0, 0));

    private static final String REFERENCE_END_DATE_AS_STRING = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.of(LocalDate.now().getYear(), Month.DECEMBER, 31, 23, 59, 59));

    public static void assertISOCompliance(final String dateTime) throws DateTimeUtilsException {
        parseISODateTime(dateTime, true);
    }

    public static LocalDateTime parseISODateTime(final String dateTime, final boolean isStartDateTime) throws DateTimeUtilsException {
        try {
            final String fixedDate = dateTime.trim();
            final String ISODateString = fixedDate + (isStartDateTime ? REFERENCE_START_DATE_AS_STRING : REFERENCE_END_DATE_AS_STRING).substring(fixedDate.length());
            return LocalDateTime.parse(ISODateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (final Throwable t) {
            throw new DateTimeUtilsException(String.format("'%s' is not an ISO-compliant datetime string.", dateTime), t);
        }
    }
}
