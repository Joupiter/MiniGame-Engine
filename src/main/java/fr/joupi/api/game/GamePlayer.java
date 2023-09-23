package fr.joupi.api.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Function;

@Getter
@Setter
@AllArgsConstructor
public class GamePlayer {

    private final UUID uuid;
    private int kills, deaths;
    private boolean spectator;

    public void addKill() {
        addKill(1);
    }

    public void addDeath() {
        addDeath(1);
    }

    public void addKill(int kills) {
        addKill(integer -> integer + kills);
    }

    public void addDeath(int deaths) {
        addDeath(integer -> integer + deaths);
    }

    private void addKill(Function<Integer, Integer> function) {
       setKills(function.apply(kills));
    }

    private void addDeath(Function<Integer, Integer> function) {
        setDeaths(function.apply(deaths));
    }

    public void sendMessage(String... messages) {
        Arrays.asList(messages)
                .forEach(message -> getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getUuid());
    }

}
