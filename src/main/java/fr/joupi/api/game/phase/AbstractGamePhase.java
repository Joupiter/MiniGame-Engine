package fr.joupi.api.game.phase;

import fr.joupi.api.Utils;
import fr.joupi.api.game.utils.GameRunnable;
import fr.joupi.api.game.listener.EventListenerWrapper;
import fr.joupi.api.game.Game;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.*;

@Getter
public abstract class AbstractGamePhase<G extends Game<?, ?, ?>> implements GamePhase {

    private final G game;

    private final List<Listener> events;
    private final List<BukkitTask> tasks;

    public AbstractGamePhase(G game) {
        this.game = game;
        this.events = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    public void startPhase() {
        onStart();
    }

    public void endPhase() {
        onEnd();
        unregister();
        getGame().getPhaseManager().tryAdvance(this);
    }

    public void cancelPhase() {
        onCancel();
        unregister();
        getGame().getPhaseManager().tryRetreat(this);
    }

    public void unregister() {
        getEvents().forEach(HandlerList::unregisterAll);
        getTasks().forEach(BukkitTask::cancel);

        getTasks().clear();
        getEvents().clear();
    }

    public <EventType extends Event> void registerEvent(Class<EventType> eventClass, Consumer<EventType> consumer) {
        registerEvent(eventClass, null, consumer);
    }

    public <EventType extends Event> void registerEvent(Class<EventType> eventClass, Function<EventType, Player> function, Consumer<EventType> consumer) {
        EventListenerWrapper<EventType> wrapper = new EventListenerWrapper<>(consumer);

        Bukkit.getPluginManager().registerEvent(eventClass, wrapper, EventPriority.NORMAL, eventExecutor(function, consumer), getGame().getPlugin());
        getEvents().add(wrapper);
    }

    private <EventType extends Event> EventExecutor eventExecutor(Function<EventType, Player> function, Consumer<EventType> consumer) {
        return (listener, event) ->
                Utils.ifPresentOrElse(Optional.ofNullable(function.apply((EventType) event)).filter(this::canTriggerEvent),
                        uuid -> consumer.accept((EventType) event),
                        () -> consumer.accept((EventType) event));
    }

    public boolean canTriggerEvent(UUID uuid) {
        return getGame().containsPlayer(uuid);
    }

    public boolean canTriggerEvent(Player player) {
        return getGame().containsPlayer(player.getUniqueId());
    }

    public void scheduleSyncTask(Consumer<BukkitTask> task, long delay) {
        getTasks().add(new GameRunnable(task).runTaskLater(getGame(), delay));
    }

    public void scheduleAsyncTask(Consumer<BukkitTask> task, long delay) {
        getTasks().add(new GameRunnable(task).runTaskLaterAsynchronously(getGame(), delay));
    }

    public void scheduleRepeatingTask(Consumer<BukkitTask> task, long delay, long period) {
        getTasks().add(new GameRunnable(task).runTaskTimer(getGame(), delay, period));
    }

}
