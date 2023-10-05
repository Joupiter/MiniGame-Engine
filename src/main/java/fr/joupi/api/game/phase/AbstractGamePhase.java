package fr.joupi.api.game.phase;

import fr.joupi.api.game.GameRunnable;
import fr.joupi.api.game.EventListenerWrapper;
import fr.joupi.api.game.Game;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
public abstract class AbstractGamePhase<G extends Game<?, ?>> {

    private final G game;

    private final List<Listener> events;
    private final List<BukkitTask> tasks;

    public AbstractGamePhase(G game) {
        this.game = game;
        this.events = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    public abstract void onStart();
    public abstract void onEnd();

    public void startPhase() {
        onStart();
    }

    public void endPhase() {
        onEnd();
        unregister();
        getGame().getPhaseManager().tryAdvance(this);
    }

    public void cancelPhase() {
        unregister();
        getGame().getPhaseManager().tryRetreat(this);
    }

    public void unregister() {
        getEvents().forEach(HandlerList::unregisterAll);
        getTasks().forEach(BukkitTask::cancel);

        getTasks().clear();
        getEvents().clear();
    }

    public <EventType extends Event> void registerEvent(Class<EventType> eventClass, Consumer<EventType> handler) {
        EventListenerWrapper<EventType> wrapper = new EventListenerWrapper<>(handler);

        Bukkit.getPluginManager().registerEvent(eventClass, wrapper, EventPriority.NORMAL, (listener, event) ->  handler.accept((EventType) event), getGame().getPlugin());
        getEvents().add(wrapper);
    }

    public boolean canTriggerEvent(G game, UUID uuid) {
        return game.containsPlayer(uuid);
    }

    public boolean canTriggerEvent(UUID uuid) {
        return canTriggerEvent(getGame(), uuid);
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
