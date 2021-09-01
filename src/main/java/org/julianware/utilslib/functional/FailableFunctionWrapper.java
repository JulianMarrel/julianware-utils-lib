package org.julianware.utilslib.functional;

import java.util.Objects;
import java.util.function.Function;


/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 24/06/2021
 */
final class FailableFunctionWrapper<T, U, RethrownExceptionType extends Throwable>
extends FailableActionWrapper<RethrownExceptionType>
implements Function<T, U> {

    private TriableFunction<T, U> function;

    public FailableFunctionWrapper<T, U, RethrownExceptionType> wrap(final TriableFunction<T, U> function) {
        Objects.requireNonNull(function);
        this.function = function;
        return this;
    }

    @Override
    public U apply(final T t) {
        try {
            return this.function.giveATry(t);
        } catch (final Throwable thrownException) {
            this.setThrownException((RethrownExceptionType) thrownException);
            return null;
        }
    }
}
