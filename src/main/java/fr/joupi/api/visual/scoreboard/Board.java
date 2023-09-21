package fr.joupi.api.visual.scoreboard;

import com.google.common.collect.Maps;
import fr.joupi.api.visual.scoreboard.lines.Line;
import fr.joupi.api.visual.scoreboard.lines.LineParser;
import fr.joupi.api.visual.scoreboard.lines.TextLine;
import fr.joupi.api.visual.scoreboard.lines.animation.LineAnimation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

@Getter
public class Board<P extends JavaPlugin> {

    private final P plugin;

    private final Player player;
    private long updateSpeed;
    private BukkitTask activeTask;

    private final Scoreboard scoreboard;
    private final Objective objective;

    private final ConcurrentMap<Integer, BoardEntry> entries;
    @Setter private Line title;
    @Setter private int titleAnimationIndex;

    @Setter private String teamEntries;

    public Board(P plugin, Player player, Line title) {
        this.plugin = plugin;
        this.player = player;
        this.title = title;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("scoreboard", "dummy");
        this.updateSpeed = 10L;
        this.titleAnimationIndex = 0;
        this.teamEntries = "abcdefghijklmnopqrstuvwxyz0123456789";
        this.entries = Maps.newConcurrentMap();

        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', title.next()));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void enable() {
        getPlayer().setScoreboard(scoreboard);
        update();
    }

    public void disable() {
        getActiveTask().cancel();
        getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public void setUpdateSpeed(int updateSpeed) {
        this.updateSpeed = updateSpeed;
        getActiveTask().cancel();
        update();
    }

    private void update() {
        activeTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateBoard();
            }
        }.runTaskTimerAsynchronously(getPlugin(), 0, getUpdateSpeed());
    }

    private void updateBoard() {
        if (!getPlayer().isOnline()) {
            disable();
            return;
        }

        getEntries().forEach((key, value) -> {
            boolean execute = true;

            if (value.getLine() instanceof LineAnimation)
                if (!value.executeNextAnimationFrame((LineAnimation) value.getLine()))
                    execute = false;

            if (execute) {
                String[] parsedLine = LineParser.parseText(value.getLine().next());

                value.getTeam().setPrefix(parsedLine[0]);
                value.getTeam().setSuffix(parsedLine[1]);

                objective.getScore(value.getEntry()).setScore(key);
            }
        });

        if (getTitle() instanceof LineAnimation) {
            setTitleAnimationIndex((getTitleAnimationIndex() + 1) % ((LineAnimation) getTitle()).getDelay());
            if (getTitleAnimationIndex() == 0)
                getObjective().setDisplayName(getTitle().next());
        }
    }

    public void setLineText(String... text) {
        Arrays.asList(text)
                .forEach(line -> setLineText(Arrays.asList(text).indexOf(line), line));
    }

    public void setLineText(int score, String text) {
        setLineText(score, new TextLine(text));
    }

    public void setLineText(int score, Line line) {
        Team team = getScoreboard().registerNewTeam(UUID.randomUUID().toString().substring(0, 15));
        String teamEntry = "§" + getTeamEntries().charAt(entries.size()) + "§r";
        team.addEntry(teamEntry);

        BoardEntry entry = new BoardEntry(team, teamEntry, line);
        entries.put(score, entry);
    }

}
