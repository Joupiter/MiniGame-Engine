package fr.joupi.api.game.phase;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fr.joupi.api.GameRunnable;
import fr.joupi.api.game.EventListenerWrapper;
import fr.joupi.api.game.Game;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Getter
public abstract class AbstractGamePhase implements GamePhase {

    private final Game<?> game;

    private final List<Listener> events;
    private final List<BukkitTask> tasks;
    private final Set<AbstractGamePhase> components;

    private Runnable onStart, onEnd, onCancel;

    public AbstractGamePhase(Game<?> game) {
        this.game = game;
        this.events = Lists.newLinkedList();
        this.tasks = Lists.newLinkedList();
        this.components = Sets.newConcurrentHashSet();
    }

    public <EventType extends Event> void registerEvent(Class<EventType> eventClass, Consumer<EventType> handler) {
        EventListenerWrapper<EventType> wrapper = new EventListenerWrapper<>(handler);

        Bukkit.getPluginManager().registerEvent(eventClass, wrapper, EventPriority.NORMAL, (listener, event) -> handler.accept((EventType) event), getGame().getPlugin());
        getEvents().add(wrapper);
    }

    public void scheduleSyncTask(Consumer<BukkitTask> task, long delay) {
        getTasks().add(new GameRunnable(task).runTaskLater(getGame().getPlugin(), delay));
    }

    public void scheduleAsyncTask(Consumer<BukkitTask> task, long delay) {
        getTasks().add(new GameRunnable(task).runTaskLaterAsynchronously(getGame().getPlugin(), delay));
    }

    public void scheduleRepeatingTask(Consumer<BukkitTask> task, long delay, long period) {
        getTasks().add(new GameRunnable(task).runTaskTimer(getGame().getPlugin(), delay, period));
    }

    public AbstractGamePhase addComponent(AbstractGamePhase component) {
        this.components.add(component);
        return this;
    }

    public void setOnStart(Runnable task) {
        this.onStart = task;
    }

    public void setOnCancel(Runnable task) {
        this.onCancel = task;
    }

    public void setOnEnd(Runnable task) {
        this.onEnd = task;
    }

    public void dispose() {
        getEvents().forEach(HandlerList::unregisterAll);
        getTasks().forEach(BukkitTask::cancel);
        getComponents().forEach(AbstractGamePhase::dispose);

        getTasks().clear();
        getEvents().clear();
    }

    protected abstract void startPhase();
    protected abstract void endPhase();

    @Override
    public void start() {
        startPhase();
        getComponents().forEach(AbstractGamePhase::start);
        Optional.ofNullable(getOnStart()).ifPresent(Runnable::run);
    }

    @Override
    public void end() {
        endPhase();
        dispose();
        getComponents().forEach(AbstractGamePhase::end);
        Optional.ofNullable(getOnEnd()).ifPresent(Runnable::run);
    }

    @Override
    public void cancel() {
        dispose();
        getComponents().forEach(AbstractGamePhase::cancel);
        Optional.ofNullable(getOnCancel()).ifPresent(Runnable::run);
    }

}