package fr.joupi.api.visual.sb;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Getter
public class EScoreboard {

    private final JavaPlugin plugin;
    private final AbstractBoard board;
    private final ConcurrentMap<UUID, List<String>> playersScoreboard;

    public EScoreboard(JavaPlugin plugin, AbstractBoard board) {
        this.plugin = plugin;
        this.board = board;
        this.playersScoreboard = Maps.newConcurrentMap();

        Bukkit.getScheduler().runTaskTimer(plugin,
                () -> getPlayersScoreboard().keySet().stream().map(Bukkit::getPlayer).forEach(this::createScoreboard), 0, 20);
    }

    public void createScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(coloredText(getBoard().getTitle()), "dummy");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        updateLines(player);

        List<String> list = getPlayersScoreboard().get(player.getUniqueId()).stream().map(this::coloredText).collect(Collectors.toList());
        Collections.reverse(list);

        list.forEach(s -> {
            Score score = objective.getScore(s);
            score.setScore(list.indexOf(s));
        });

        player.setScoreboard(scoreboard);
    }

    public void updateLines(Player player) {
        getPlayersScoreboard().put(player.getUniqueId(), getBoard().getLines(player));
    }

    public void onJoin(Player player) {
        createScoreboard(player);
        getPlayersScoreboard().put(player.getUniqueId(), getBoard().getLines(player));
    }

    public void onLeave(Player player) {
        getPlayersScoreboard().remove(player.getUniqueId());
    }

    private String coloredText(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
