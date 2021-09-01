package org.julianware.utilslib.functional;


/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 25/06/2021
 */
@FunctionalInterface
public interface TriableSupplier<T, V extends Throwable> {

    T giveMeSome() throws V;
}
