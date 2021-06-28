package org.julianware.utilslib.functional;

import java.util.Objects;

/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 24/06/2021
 */
public class FailableRunnableWrapper
extends FailableActionWrapper
implements Runnable {

    private TriableRunnable action;

    public FailableRunnableWrapper wrap(final TriableRunnable action) {
        Objects.requireNonNull(action);
        this.action = action;
        return this;
    }

    @Override
    public void run() {
        try {
            this.action.giveItARun();
        } catch (final Throwable thrownException) {
            this.setThrownException(thrownException);
        }
    }
}
