package fr.joupi.api;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Getter
public class GameRunnable implements Runnable {

    private BukkitTask task;
    private final Consumer<BukkitTask> consumer;

    public GameRunnable(Consumer<BukkitTask> consumer) {
        this.consumer = consumer;
    }

    public synchronized void cancel() throws IllegalStateException {
        Bukkit.getScheduler().cancelTask(this.getTaskId());
    }

    @NotNull
    public synchronized BukkitTask runTask(@NotNull Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(Bukkit.getScheduler().runTask(plugin, this));
    }

    @NotNull
    public synchronized BukkitTask runTaskAsynchronously(@NotNull Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return setupTask(Bukkit.getScheduler().runTaskAsynchronously(plugin, this));
    }

    @NotNull
    public synchronized BukkitTask runTaskLater(@NotNull Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return this.setupTask(Bukkit.getScheduler().runTaskLater(plugin, this, delay));
    }

    @NotNull
    public synchronized BukkitTask runTaskLaterAsynchronously(@NotNull Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return this.setupTask(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this, delay));
    }

    @NotNull
    public synchronized BukkitTask runTaskTimer(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return this.setupTask(Bukkit.getScheduler().runTaskTimer(plugin, this, delay, period));
    }

    @NotNull
    public synchronized BukkitTask runTaskTimerAsynchronously(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        checkNotYetScheduled();
        return this.setupTask(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, delay, period));
    }

    public synchronized int getTaskId() throws IllegalStateException {
        checkScheduled();
        return this.task.getTaskId();
    }

    private void checkScheduled() {
        if (this.task == null)
            throw new IllegalStateException("Not scheduled yet");
    }

    private void checkNotYetScheduled() {
        if (this.task != null)
            throw new IllegalStateException("Already scheduled as " + this.task.getTaskId());
    }

    @Override
    public void run() {
        getConsumer().accept(task);
    }

    @NotNull
    private BukkitTask setupTask(@NotNull BukkitTask task) {
        this.task = task;
        return task;
    }

}