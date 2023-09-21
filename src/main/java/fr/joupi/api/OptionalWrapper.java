package fr.joupi.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public class OptionalWrapper<T> {

    private final Optional<T> optional;

    public static <T> OptionalWrapper<T> of(final T value) {
        return new OptionalWrapper<>(Optional.of(value));
    }

    public static <T> OptionalWrapper<T> of(final Optional<T> optional) {
        return new OptionalWrapper<>(optional);
    }

    public static <T> OptionalWrapper<T> ofNullable(final T value) {
        return new OptionalWrapper<>(Optional.ofNullable(value));
    }

    public static <T> OptionalWrapper<T> empty() {
        return new OptionalWrapper<>(Optional.empty());
    }

    private final BiFunction<Consumer<T>, Runnable, Void> ifPresent = (present, notPresent) -> {
        if (getOptional().isPresent())
            present.accept(getOptional().get());
        else
            notPresent.run();
        return null;
    };

    private final BiFunction<Runnable, Consumer<T>, Void> ifNotPresent = (notPresent, present) -> {
        if (!getOptional().isPresent())
            notPresent.run();
        else
            present.accept(getOptional().get());
        return null;
    };

    public IOptionalFunction<Consumer<T>, IOptionalFunction<Runnable, Void>> ifPresent() {
        return OptionalWrapper.curry(ifPresent);
    }

    public IOptionalFunction<Runnable, IOptionalFunction<Consumer<T>, Void>> ifNotPresent() {
        return OptionalWrapper.curry(ifNotPresent);
    }

    private static <X, Y, Z> IOptionalFunction<X, IOptionalFunction<Y, Z>> curry(final BiFunction<X, Y, Z> function) {
        return (final X x) -> (final Y y) -> function.apply(x, y);
    }

    @FunctionalInterface
    public interface IOptionalFunction<T, R> extends Function<T, R> {

        default R elseApply(final T t) {
            return this.apply(t);
        }

    }

}