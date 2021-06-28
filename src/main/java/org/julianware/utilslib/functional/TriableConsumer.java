package org.julianware.utilslib.functional;


/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 24/06/2021
 */
@FunctionalInterface
public interface TriableConsumer<T> {

    void eatThis(final T t) throws Throwable;
}