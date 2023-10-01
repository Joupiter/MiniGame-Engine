package fr.joupi.api.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

@Getter
@Setter
@AllArgsConstructor
public class CountdownTimer implements Runnable {

    private final JavaPlugin plugin;

    private Integer assignedTaskId;

    private final int seconds;
    private int secondsLeft;

    private Consumer<CountdownTimer> everySecond;
    private Runnable beforeTimer, afterTimer;

    public CountdownTimer(JavaPlugin plugin, int seconds) {
        this(plugin, seconds, () -> {}, () -> {}, timer -> {});
    }

    public CountdownTimer(JavaPlugin plugin, int seconds, Runnable beforeTimer, Runnable afterTimer, Consumer<CountdownTimer> everySecond) {
        this.plugin = plugin;
        this.seconds = seconds;
        this.secondsLeft = seconds;
        this.beforeTimer = beforeTimer;
        this.afterTimer = afterTimer;
        this.everySecond = everySecond;
    }

    @Override
    public void run() {
        if (getSecondsLeft() < 1) {
            getAfterTimer().run();

            if (getAssignedTaskId() != null)
                Bukkit.getScheduler().cancelTask(getAssignedTaskId());

            return;
        }

        if (getSecondsLeft() == getSeconds())
            getBeforeTimer().run();

        getEverySecond().accept(this);

        secondsLeft--;
    }

    public void cancelTimer() {
        Bukkit.getScheduler().cancelTask(getAssignedTaskId());
    }

    public void scheduleTimer() {
        setAssignedTaskId(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 20L));
    }

}