package org.julianware.utilslib.functional;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 24/06/2021
 */
public abstract class FailableActionWrapper {

    private Throwable thrownException;

    public <Z extends Throwable> void rethrowIfFailed(final Function<Throwable, Z> exceptionFunction) throws Z {
        final Optional<Throwable> optionalException = Optional.ofNullable(this.thrownException);
        if (optionalException.isPresent()) {
            throw exceptionFunction.apply(optionalException.get());
        }
    }

    protected void setThrownException(final Throwable thrownException) {
        this.thrownException = thrownException;
    }
}
