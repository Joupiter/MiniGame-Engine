package fr.joupi.api.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GamePlayer {

    private final UUID uuid;
    @Setter private boolean spectator;

    public void sendMessage(String... messages) {
        Arrays.asList(messages)
                .forEach(message -> getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getUuid());
    }

}
