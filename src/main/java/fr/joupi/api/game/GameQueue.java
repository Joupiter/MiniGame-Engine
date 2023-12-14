package fr.joupi.api.game;

import fr.joupi.api.threading.MultiThreading;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
public class GameQueue<G extends Game<?, ?, ?>> {

    private final G game;

    private final List<UUID> queue;

    public GameQueue(G game) {
        this.game = game;
        this.queue = new LinkedList<>();
        MultiThreading.runnablePool.scheduleAtFixedRate(this::update, 1, 1, TimeUnit.SECONDS);
    }

    private void update() {
        getFirstPlayer().map(Bukkit::getPlayer).ifPresent(player -> {
            removePlayer(player);
            getGame().joinGame(player);
        });
    }

    public Optional<UUID> getFirstPlayer() {
        return getQueue().stream().findFirst();
    }

    public Optional<UUID> getPlayer(UUID uuid) {
        return getQueue().stream().filter(uuid::equals).findFirst();
    }

    public void addPlayer(Player player) {
        getQueue().add(player.getUniqueId());
        getGame().debug("Queue - Added {0} to {1} queue", player.getName(), getGame().getFullName());
    }

    public void removePlayer(Player player) {
        getQueue().remove(player.getUniqueId());
        getGame().debug("Queue - Remove {0} from {1} queue", player.getPlayer().getName(), getGame().getFullName());
    }

    public boolean contains(UUID uuid) {
        return getQueue().contains(uuid);
    }

    public int getSize() {
        return getQueue().size();
    }

    public int getPosition(UUID uuid) {
        return getQueue().indexOf(uuid) + 1;
    }

    public boolean isEmpty() {
        return getQueue().isEmpty();
    }

}
