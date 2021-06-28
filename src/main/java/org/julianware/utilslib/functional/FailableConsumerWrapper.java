package org.julianware.utilslib.functional;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 24/06/2021
 */
public class FailableConsumerWrapper<T>
extends FailableActionWrapper
implements Consumer<T> {

    private TriableConsumer<T> consumer;

    public FailableConsumerWrapper<T> wrap(final TriableConsumer<T> consumer) {
        Objects.requireNonNull(consumer);
        this.consumer = consumer;
        return this;
    }

    @Override
    public void accept(final T t) {
        try {
            this.consumer.eatThis(t);
        } catch (final Throwable thrownException) {
            this.setThrownException(thrownException);
        }
    }
}
