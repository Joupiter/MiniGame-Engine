package fr.joupi.api.file.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.joupi.api.threading.MultiThreading;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Collection;

@Getter
@AllArgsConstructor
public class JsonRepository<P extends JavaPlugin, ID, T extends Identifiable<ID>> implements AdvancedRepository<ID, T> {

    private final P plugin;

    private final Gson gson;
    private final Class<T> type;
    private final String path;

    public JsonRepository(P plugin, Class<T> type, String path) {
        this(plugin, new GsonBuilder().setPrettyPrinting().create(), type, path);
    }

    public File getFile(ID identifier) {
        final File file = new File(getDataFolder(), identifier.toString() + ".json");

        MultiThreading.runAsync(() -> file.getParentFile().mkdirs());
        return file;
    }

    public File getFile(T object) {
        return getFile(object.getIdentifier());
    }

    public boolean fileExists(ID identifier) {
        return new File(getDataFolder(), identifier.toString() + ".json").exists();
    }

    @Override
    public T find(ID identifier) {
        try {
            return getGson().fromJson(new FileReader(getFile(identifier)), getType());
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public Collection<T> findAll() {
        throw new NotImplementedException();
    }

    @Override
    public boolean delete(T object) {
        return getFile(object).delete();
    }

    @Override
    public boolean delete(ID identifier) {
        return getFile(identifier).delete();
    }

    @Override
    public T save(T object) {
        try (Writer writer = new FileWriter(getFile(object))) {
            MultiThreading.runAsync(() -> getGson().toJson(object, writer));
        } catch (IOException exception) {
            return null;
        }
        return object;
    }

    @Override
    public void saveAll(Collection<T> objects) {
        objects.forEach(this::save);
    }

    public File getDataFolder() {
        return new File(getPlugin().getDataFolder() + File.separator + getPath());
    }

}
