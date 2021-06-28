package org.julianware.utilslib.functional;

import java.util.Objects;
import java.util.function.Function;


/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 24/06/2021
 */
public class FailableFunctionWrapper<T, U>
extends FailableActionWrapper
implements Function<T, U> {

    private TriableFunction<T, U> function;

    public FailableFunctionWrapper<T, U> wrap(final TriableFunction<T, U> function) {
        Objects.requireNonNull(function);
        this.function = function;
        return this;
    }

    @Override
    public U apply(final T t) {
        try {
            return this.function.giveATry(t);
        } catch (final Throwable thrownException) {
            this.setThrownException(thrownException);
            return null;
        }
    }
}
