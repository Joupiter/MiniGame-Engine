package fr.joupi.api;

import fr.joupi.api.game.Game;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public class GameRunnable implements Runnable {

    private BukkitTask task;
    private final Consumer<BukkitTask> consumer;

    public synchronized void cancel() throws IllegalStateException {
        Bukkit.getScheduler().cancelTask(this.getTaskId());
    }

    public synchronized BukkitTask runTask(Game game) {
        checkNotYetScheduled();
        return setupTask(Bukkit.getScheduler().runTask(game.getPlugin(), this));
    }

    public synchronized BukkitTask runTaskAsynchronously(Game game) {
        checkNotYetScheduled();
        return setupTask(Bukkit.getScheduler().runTaskAsynchronously(game.getPlugin(), this));
    }

    public synchronized BukkitTask runTaskLater(Game game, long delay) {
        checkNotYetScheduled();
        return setupTask(Bukkit.getScheduler().runTaskLater(game.getPlugin(), this, delay));
    }

    public synchronized BukkitTask runTaskLaterAsynchronously(Game game, long delay) {
        checkNotYetScheduled();
        return setupTask(Bukkit.getScheduler().runTaskLaterAsynchronously(game.getPlugin(), this, delay));
    }

    public synchronized BukkitTask runTaskTimer(Game game, long delay, long period) {
        checkNotYetScheduled();
        return setupTask(Bukkit.getScheduler().runTaskTimer(game.getPlugin(), this, delay, period));
    }

    public synchronized BukkitTask runTaskTimerAsynchronously(Game game, long delay, long period) {
        checkNotYetScheduled();
        return setupTask(Bukkit.getScheduler().runTaskTimerAsynchronously(game.getPlugin(), this, delay, period));
    }

    public synchronized int getTaskId() {
        checkScheduled();
        return getTask().getTaskId();
    }

    private void checkScheduled() {
        if (getTask() == null)
            throw new IllegalStateException("Not scheduled yet");
    }

    private void checkNotYetScheduled() {
        if (getTask() != null)
            throw new IllegalStateException("Already scheduled as " + getTask().getTaskId());
    }

    @Override
    public void run() {
        getConsumer().accept(getTask());
    }

    private BukkitTask setupTask(BukkitTask task) {
        this.task = task;
        return task;
    }

}