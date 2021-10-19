package org.julianware.utilslib.functional;

import java.util.Objects;


/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 24/06/2021
 */
public final class FailableRunnableWrapper<U extends Throwable>
extends FailableActionWrapper<U>
implements Runnable {

    private TriableRunnable action;

    public FailableRunnableWrapper<U> wrap(final TriableRunnable action) {
        Objects.requireNonNull(action);
        this.action = action;
        return this;
    }

    @Override
    public void run() {
        try {
            this.action.giveItARun();
        } catch (final Throwable thrownException) {
            this.setThrownException((U) thrownException);
        }
    }
}
