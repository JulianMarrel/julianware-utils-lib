package org.julianware.utilslib.functional;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 24/06/2021
 */
abstract class FailableActionWrapper<OriginalExceptionType extends Throwable> {

    private OriginalExceptionType thrownException;

    public <RethrownExceptionType extends Throwable> void rethrowIfFailed(final Function<OriginalExceptionType, RethrownExceptionType> exceptionFunction) throws RethrownExceptionType {
        final Optional<OriginalExceptionType> optionalException = Optional.ofNullable(this.thrownException);
        if (optionalException.isPresent()) {
            throw exceptionFunction.apply(optionalException.get());
        }
    }

    protected void setThrownException(final OriginalExceptionType thrownException) {
        this.thrownException = thrownException;
    }
}
