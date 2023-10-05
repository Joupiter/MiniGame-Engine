package fr.joupi.api;

import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.function.Consumer;

@UtilityClass
public class Utils {

    public <T> void ifPresentOrElse(Optional<T> optional, Consumer<T> consumer, Runnable runnable) {
        if (optional.isPresent()) consumer.accept(optional.get());
        else runnable.run();
    }

}
