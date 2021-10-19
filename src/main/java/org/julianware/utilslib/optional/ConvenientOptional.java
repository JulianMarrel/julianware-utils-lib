package org.julianware.utilslib.optional;


import org.julianware.utilslib.functional.*;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;


/**
 * @author Julian Marrel <julian.marrel@smile.eu>
 * @created 24/06/2021
 */
public class ConvenientOptional<OptionalObjectType> {

    private final Optional<OptionalObjectType> optionalT;

    private ConvenientOptional(final Optional<OptionalObjectType> optionalT) {
        this.optionalT = optionalT;
    }

    private ConvenientOptional(final OptionalObjectType t, final boolean isNullable) {
        this.optionalT = isNullable ? Optional.ofNullable(t) : Optional.of(t);
    }

    public static <ObjectType> ConvenientOptional<ObjectType> of(final ObjectType b) {
        return new ConvenientOptional<>(b, false);
    }

    public static <ObjectType> ConvenientOptional<ObjectType> ofNullable(final ObjectType b) {
        return new ConvenientOptional<>(b, true);
    }

    public <MappingReturnType, OriginalExceptionType extends Throwable, RethrownExceptionType extends Throwable>
    ConvenientOptional<MappingReturnType> map(
            final TriableFunction<OptionalObjectType, MappingReturnType> mapper,
            final Function<OriginalExceptionType, RethrownExceptionType> exceptionSupplier
    ) throws RethrownExceptionType {
        final FailableFunctionWrapper<OptionalObjectType, MappingReturnType, OriginalExceptionType> failableMapperWrapper = new FailableFunctionWrapper<>();
        final Optional<MappingReturnType> optionalU = this.optionalT.map(failableMapperWrapper.wrap(mapper));
        failableMapperWrapper.rethrowIfFailed(exceptionSupplier);
        return new ConvenientOptional<>(optionalU);
    }

    public Stream<OptionalObjectType> stream() {
        return this.optionalT.stream();
    }

    public boolean isPresent() {
        return this.optionalT.isPresent();
    }

    public boolean isEmpty() {
        return this.optionalT.isEmpty();
    }

    public <OriginalExceptionType extends Throwable> void ifPresent(final TriableConsumer<OptionalObjectType, OriginalExceptionType> action) throws OriginalExceptionType {
        final FailableConsumerWrapper<OptionalObjectType, OriginalExceptionType> failableConsumerWrapper = new FailableConsumerWrapper<>();
        this.optionalT.ifPresent(failableConsumerWrapper.wrap(action));
        failableConsumerWrapper.rethrowIfFailed(exception -> exception);
    }

    public <OriginalExceptionType extends Throwable, RethrownExceptionType extends Throwable> void ifPresent(final TriableConsumer<OptionalObjectType, OriginalExceptionType> action, final Function<OriginalExceptionType, RethrownExceptionType> exceptionSupplier) throws RethrownExceptionType {
        final FailableConsumerWrapper<OptionalObjectType, OriginalExceptionType> failableConsumerWrapper = new FailableConsumerWrapper<>();
        this.optionalT.ifPresent(failableConsumerWrapper.wrap(action));
        failableConsumerWrapper.rethrowIfFailed(exceptionSupplier);
    }

    public <OriginalExceptionType extends Throwable> void ifPresentOrElse(final TriableConsumer<OptionalObjectType, OriginalExceptionType> action, final TriableRunnable emptyAction) throws OriginalExceptionType {
        final FailableConsumerWrapper<OptionalObjectType, OriginalExceptionType> failableConsumerWrapper = new FailableConsumerWrapper<>();
        final FailableRunnableWrapper<OriginalExceptionType> failableRunnableWrapper = new FailableRunnableWrapper<>();
        this.optionalT.ifPresentOrElse(
                failableConsumerWrapper.wrap(action),
                failableRunnableWrapper.wrap(emptyAction)
        );
        failableConsumerWrapper.rethrowIfFailed(exception -> exception);
        failableRunnableWrapper.rethrowIfFailed(exception -> exception);
    }

    public <OriginalExceptionType extends Throwable, RethrownExceptionType extends Throwable> void ifPresentOrElse(final TriableConsumer<OptionalObjectType, OriginalExceptionType> action, final TriableRunnable emptyAction, final Function<OriginalExceptionType, RethrownExceptionType> exceptionSupplier) throws RethrownExceptionType {
        final FailableConsumerWrapper<OptionalObjectType, OriginalExceptionType> failableConsumerWrapper = new FailableConsumerWrapper<>();
        final FailableRunnableWrapper<OriginalExceptionType> failableRunnableWrapper = new FailableRunnableWrapper<>();
        this.optionalT.ifPresentOrElse(
                failableConsumerWrapper.wrap(action),
                failableRunnableWrapper.wrap(emptyAction)
        );
        failableConsumerWrapper.rethrowIfFailed(exceptionSupplier);
        failableRunnableWrapper.rethrowIfFailed(exceptionSupplier);
    }

    public Optional<OptionalObjectType> filter(final Predicate<? super OptionalObjectType> predicate) {
        return this.optionalT.filter(predicate);
    }

    public OptionalObjectType orElse(final OptionalObjectType other) {
        return this.optionalT.orElse(other);
    }

    public <V extends Throwable> OptionalObjectType orElseGet(final TriableSupplier<OptionalObjectType, V> supplier) throws V {
        final FailableSupplierWrapper<OptionalObjectType, V> failableSupplierWrapper = new FailableSupplierWrapper<>();
        final OptionalObjectType suppliedValue = this.optionalT.orElseGet(failableSupplierWrapper.wrap(supplier));
        failableSupplierWrapper.rethrowIfFailed(exception -> exception);
        return suppliedValue;
    }

    public <U extends Throwable, V extends Throwable> OptionalObjectType orElseGet(final TriableSupplier<OptionalObjectType, U> supplier, final Function<U, V> exceptionSupplier) throws V {
        final FailableSupplierWrapper<OptionalObjectType, U> failableSupplierWrapper = new FailableSupplierWrapper<>();
        final OptionalObjectType suppliedValue = this.optionalT.orElseGet(failableSupplierWrapper.wrap(supplier));
        failableSupplierWrapper.rethrowIfFailed(exceptionSupplier);
        return suppliedValue;
    }

    public OptionalObjectType orElseThrow() {
        return this.optionalT.orElseThrow();
    }

    public <X extends Throwable> OptionalObjectType orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X {
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
