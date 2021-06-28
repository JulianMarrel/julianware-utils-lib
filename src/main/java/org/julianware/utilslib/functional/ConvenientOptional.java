package org.julianware.utilslib.functional;


import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;


/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 24/06/2021
 */
public class ConvenientOptional<T> {

    private final Optional<T> optionalT;

    private ConvenientOptional(final Optional<T> optionalT) {
        this.optionalT = optionalT;
    }

    private ConvenientOptional(final T t, final boolean isNullable) {
        this.optionalT = isNullable ? Optional.ofNullable(t) : Optional.of(t);
    }

    public static <B> ConvenientOptional<B> of(final B b) {
        return new ConvenientOptional<>(b, false);
    }

    public static <B> ConvenientOptional<B> ofNullable(final B b) {
        return new ConvenientOptional<>(b, true);
    }

    public <U, V extends Throwable> ConvenientOptional<U> map(final TriableFunction<T, U> mapper, final Function<Throwable, V> exceptionSupplier) throws V {
        final FailableFunctionWrapper<T, U> failableMapperWrapper = new FailableFunctionWrapper<>();
        final Optional<U> optionalU = this.optionalT.map(failableMapperWrapper.wrap(mapper));
        failableMapperWrapper.rethrowIfFailed(exceptionSupplier);
        return new ConvenientOptional<>(optionalU);
    }

    public Stream<T> stream() {
        return this.optionalT.stream();
    }

    public boolean isPresent() {
        return this.optionalT.isPresent();
    }

    public boolean isEmpty() {
        return this.optionalT.isEmpty();
    }

    public <V extends Throwable> void ifPresent(final TriableConsumer<T> action, final Function<Throwable, V> exceptionSupplier) throws V {
        final FailableConsumerWrapper<T> failableConsumerWrapper = new FailableConsumerWrapper<>();
        this.optionalT.ifPresent(failableConsumerWrapper.wrap(action));
        failableConsumerWrapper.rethrowIfFailed(exceptionSupplier);
    }

    public <V extends Throwable> void ifPresentOrElse(final TriableConsumer<T> action, final TriableRunnable emptyAction, final Function<Throwable, V> exceptionSupplier) throws V {
        final FailableConsumerWrapper<T> failableConsumerWrapper = new FailableConsumerWrapper<>();
        final FailableRunnableWrapper failableRunnableWrapper = new FailableRunnableWrapper();
        this.optionalT.ifPresentOrElse(
                failableConsumerWrapper.wrap(action),
                failableRunnableWrapper.wrap(emptyAction)
        );
        failableConsumerWrapper.rethrowIfFailed(exceptionSupplier);
        failableRunnableWrapper.rethrowIfFailed(exceptionSupplier);
    }

    public Optional<T> filter(final Predicate<? super T> predicate) {
        return this.optionalT.filter(predicate);
    }

    public T orElse(final T other) {
        return this.optionalT.orElse(other);
    }

    public <V extends Throwable> T orElseGet(final TriableSupplier<T> supplier, final Function<Throwable, V> exceptionSupplier) throws V {
        final FailableSupplierWrapper<T> failableSupplierWrapper = new FailableSupplierWrapper<>();
        final T suppliedValue = this.optionalT.orElseGet(failableSupplierWrapper.wrap(supplier));
        failableSupplierWrapper.rethrowIfFailed(exceptionSupplier);
        return suppliedValue;
    }

    public T orElseThrow() {
        return this.optionalT.orElseThrow();
    }

    public <X extends Throwable> T orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X {
        return this.optionalT.orElseThrow(exceptionSupplier);
    }

    @Override
    public boolean equals(Object obj) {
        return this.optionalT.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.optionalT.hashCode();
    }

    @Override
    public String toString() {
        return this.optionalT.toString();
    }
}
