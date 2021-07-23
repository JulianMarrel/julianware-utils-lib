package org.julianware.utilslib.functional;

import java.util.Objects;
import java.util.function.Supplier;


/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 25/06/2021
 */
final class FailableSupplierWrapper<T, V extends Throwable>
extends FailableActionWrapper<V>
implements Supplier<T> {

    private TriableSupplier<T, V> supplier;

    public FailableSupplierWrapper<T, V> wrap(final TriableSupplier<T, V> supplier) {
        Objects.requireNonNull(supplier);
        this.supplier = supplier;
        return this;
    }

    @Override
    public T get() {
        try {
            return this.supplier.giveMeSome();
        } catch (final Throwable thrownException) {
            this.setThrownException((V) thrownException);
            return null;
        }
    }
}
