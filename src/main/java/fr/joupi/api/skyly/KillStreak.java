package fr.joupi.api.skyly;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.joupi.api.PowerRarity;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;

@Getter
public class KillStreak {

    private final JavaPlugin plugin;

    private final Gson gson;
    private ConcurrentMap<Integer, PowerRarity> killStreaksMap;

    public KillStreak(JavaPlugin plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.createFile();
    }

    public void loadKillStreak() {
        CompletableFuture.runAsync(() -> {
            try {
                this.killStreaksMap = getGson().fromJson(new FileReader(getKillStreakFile()), new TypeToken<Map<Integer, PowerRarity>>() {}.getType());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    private void createFile() {
        CompletableFuture.runAsync(() ->
                Optional.ofNullable(getKillStreakFile())
                        .stream()
                        .filter(((Predicate<? super File>) File::exists).negate())
                        .findFirst()
                        .ifPresent(file -> {
                            try {
                                System.out.println("created");
                                getKillStreakFile().getParentFile().mkdirs();
                                getKillStreakFile().createNewFile();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })).whenComplete((unused, throwable) -> loadKillStreak());
    }

    public void saveKillStreak() {
        CompletableFuture.runAsync(() -> {
            try (Writer writer = new FileWriter(getKillStreakFile(), false)) {
                getGson().toJson(getKillStreaksMap(), writer);
                writer.flush();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    public File getKillStreakFile() {
        return new File(getPlugin().getDataFolder(), "killstreak.json");
    }

}