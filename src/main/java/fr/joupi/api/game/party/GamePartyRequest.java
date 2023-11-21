package fr.joupi.api.game.party;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GamePartyRequest {

    private final UUID sender, target;

    public Player getSenderPlayer() {
        return Bukkit.getPlayer(getSender());
    }

    public Player getTargetPlayer() {
        return Bukkit.getPlayer(getSender());
    }

}
