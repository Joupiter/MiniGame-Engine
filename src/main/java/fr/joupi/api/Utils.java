package fr.joupi.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.joupi.api.game.utils.LocationAdapter;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;

import java.util.Optional;
import java.util.function.Consumer;

@UtilityClass
public class Utils {

    public <T> void ifPresentOrElse(Optional<T> optional, Consumer<T> consumer, Runnable runnable) {
        if (optional.isPresent()) consumer.accept(optional.get());
        else runnable.run();
    }

    public Gson getGson() {
        return new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().registerTypeAdapter(Location .class, new LocationAdapter()).disableHtmlEscaping().create();
    }

}
