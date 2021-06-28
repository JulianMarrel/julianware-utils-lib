package org.julianware.utilslib.functional;

import java.util.Objects;
import java.util.function.Supplier;


/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 25/06/2021
 */
public class FailableSupplierWrapper<T>
extends FailableActionWrapper
implements Supplier<T> {

    private TriableSupplier<T> supplier;

    public FailableSupplierWrapper<T> wrap(final TriableSupplier<T> supplier) {
        Objects.requireNonNull(supplier);
        this.supplier = supplier;
        return this;
    }

    @Override
    public T get() {
        try {
            return this.supplier.giveMeSome();
        } catch (final Throwable thrownException) {
            this.setThrownException(thrownException);
            return null;
        }
    }
}
