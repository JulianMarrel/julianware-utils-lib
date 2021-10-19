package org.julianware.utilslib.functional;


/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 24/06/2021
 */
@FunctionalInterface
public interface TriableRunnable {

    void giveItARun() throws Throwable;
}
