package org.julianware.utilslib.functional;

import java.util.Objects;
import java.util.function.Consumer;


/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 24/06/2021
 */
public final class FailableConsumerWrapper<T, U extends Throwable>
extends FailableActionWrapper<U>
implements Consumer<T> {

    private TriableConsumer<T, U> consumer;

    public FailableConsumerWrapper<T, U> wrap(final TriableConsumer<T, U> consumer) {
        Objects.requireNonNull(consumer);
        this.consumer = consumer;
        return this;
    }

    @Override
    public void accept(final T t) {
        try {
            this.consumer.eatThis(t);
        } catch (final Throwable thrownException) {
            this.setThrownException((U) thrownException);
        }
    }
}
