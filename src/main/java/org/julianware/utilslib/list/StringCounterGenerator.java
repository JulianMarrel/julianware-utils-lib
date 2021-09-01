package org.julianware.utilslib.list;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.StreamSupport;


/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 23/07/2021
 */
public final class StringCounterGenerator
implements Iterator<String> {

    private int count;

    private final int maxValue;

    private final int formatLength;

    StringCounterGenerator(final int minValue, final int maxValue) {
        this.maxValue = maxValue;
        this.formatLength = Integer.toString(maxValue).length();
        this.count = minValue;
    }

    @Override
    public boolean hasNext() {
        return this.count <= this.maxValue;
    }

    @Override
    public String next() {
        final String pendingCounterAsString = Integer.toString(this.count);
        final char[] chars = new char[this.formatLength];
        Arrays.fill(chars, '0');
        final StringBuilder nextString = new StringBuilder(new String(chars));
        nextString.replace((this.formatLength - pendingCounterAsString.length()), this.formatLength, pendingCounterAsString);
        this.count ++;
        return nextString.toString();
    }

    public static String[] stringSequenceOf(final int minValue, final int maxValue) {
        final Iterable<String> iterable = () -> new StringCounterGenerator(minValue, maxValue);
        return StreamSupport.stream(iterable.spliterator(), false).toArray(String[]::new);
    }
}
