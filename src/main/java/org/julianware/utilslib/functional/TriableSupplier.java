package org.julianware.utilslib.functional;


import java.io.IOException;

/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 25/06/2021
 */
@FunctionalInterface
public interface TriableSupplier<T> {

    T giveMeSome() throws IOException;
}
