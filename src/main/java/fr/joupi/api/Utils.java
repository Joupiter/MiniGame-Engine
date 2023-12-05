package fr.joupi.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.function.Consumer;

@UtilityClass
public class Utils {

    public <T> void ifPresentOrElse(Optional<T> optional, Consumer<T> consumer, Runnable runnable) {
        if (optional.isPresent()) consumer.accept(optional.get());
        else runnable.run();
    }

    public void debug(String message, Object ... arguments) {
        System.out.println("[GameEngine] " + MessageFormat.format(message, arguments));
    }

    public Gson getGson() {
        return new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping().create();
    }

    @Getter
    @AllArgsConstructor
    public class Value<T> {

        private final T value;

        public static <T> Value<T> of(T value) {
            return new Value<>(value);
        }

        public void consume(Consumer<T> consumer) {
            consumer.accept(getValue());
        }

    }

}
