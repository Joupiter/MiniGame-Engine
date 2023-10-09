package fr.joupi.api.game;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Getter
@Setter
public class GameSettings {

    private final GameSize gameSize;
    private World world;

    private final ConcurrentMap<String, List<Location>> locations;

    public GameSettings(GameSize gameSize, World world) {
        this.gameSize = gameSize;
        this.world = world;
        this.locations = new ConcurrentHashMap<>();
    }

    public void loadWorld() {

    }

    public void addLocation(String name, Location location) {
        getLocations().computeIfAbsent(name, k -> Lists.newArrayList()).add(location);
    }

    public void addLocations(String name, Location... locations) {
        Arrays.asList(locations)
                .forEach(location -> addLocation(name, location));
    }

    public Location getLocation(String name) {
        return getLocations(name).get(0);
    }

    public Optional<Location> getRandomLocation(String name) {
        return getLocations(name).stream().skip(getLocations(name).isEmpty() ? 0 : new Random().nextInt(getLocations(name).size())).findFirst();
    }

    public List<Location> getLocations(String name) {
        return getLocations().get(name);
    }

}
