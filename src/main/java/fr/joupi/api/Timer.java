package fr.joupi.api;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Getter
public class Timer<T> {

    public static List<Timer<?>> TIMERS = new ArrayList<>();

    protected T key;
    private final String name;
    private long duration;

    private String displayName;
    private int tickInterval = -1;
    private BukkitTask task;
    private long startTime = 0;
    private Consumer<Timer<T>> onStartConsumer, tickConsumer, onEndConsumer;

    public Timer(String name, long duration, T key) {
        this.name = name;
        this.duration = duration;
        this.key = key;
    }

    public long getRemainingTime() {
        return (this.startTime + this.duration) - System.currentTimeMillis();
    }

    public boolean isActive() {
        return this.getRemainingTime() > 0;
    }

    public Timer<T> displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public Timer<T> start(Consumer<Timer<T>> timerConsumer) {
        this.onStartConsumer = timerConsumer;
        return this;
    }

    public Timer<T> tick(int tickInterval, Consumer<Timer<T>> timerConsumer) {
        this.tickInterval = tickInterval;
        this.tickConsumer = timerConsumer;
        return this;
    }

    public Timer<T> end(Consumer<Timer<T>> timerConsumer) {
        this.onEndConsumer = timerConsumer;
        return this;
    }

    public void stop() {
        if(this.onEndConsumer != null) this.onEndConsumer.accept(this);
        if(this.task != null) this.task.cancel();
        TIMERS.remove(this);
    }

    public void extend(JavaPlugin plugin, long toAdd) {
        this.duration += toAdd;

        if(this.task != null) {
            this.task.cancel();
            this.task = null;
        }

        this.runTask(plugin);
    }

    private void runTask(JavaPlugin plugin)  {
        TIMERS.add(this);
        this.startTime = System.currentTimeMillis();

        if(this.tickInterval == -1 || this.tickConsumer == null) {
            this.task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(this.onEndConsumer != null) this.onEndConsumer.accept(this);
                if(this.task != null) this.task.cancel();
                TIMERS.remove(this);
            }, this.duration / 50);
        } else {
            this.task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                this.tickConsumer.accept(this);
                if(!isActive()) {
                    if(this.onEndConsumer != null) this.onEndConsumer.accept(this);
                    if(this.task != null) this.task.cancel();
                    TIMERS.remove(this);
                }

            }, this.tickInterval, this.tickInterval);
        }
    }

    public void run(JavaPlugin plugin) {
        if(this.onStartConsumer != null) {
            this.onStartConsumer.accept(this);
        }

        this.runTask(plugin);
    }

    public static Optional<Timer<?>> find(String key) {
        return TIMERS.stream().filter(timer -> timer.getName().equals(key)).findAny();
    }
}