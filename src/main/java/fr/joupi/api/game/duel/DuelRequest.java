package fr.joupi.api.game.duel;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public class DuelRequest {

    private final UUID id, sender, target;
    private final String selectedMap, selectedKit, selectedKnockBack;

    public DuelRequest(UUID sender, UUID target, String selectedMap, String selectedKit, String selectedKnockBack) {
        this.id = UUID.randomUUID();
        this.sender = sender;
        this.target = target;
        this.selectedMap = selectedMap;
        this.selectedKit = selectedKit;
        this.selectedKnockBack = selectedKnockBack;
    }

    public Optional<Player> getSenderPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(getSender()));
    }

    public Optional<Player> getTargetPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(getTarget()));
    }

    public List<Player> getPlayers() {
        return List.of(Bukkit.getPlayer(getSender()), Bukkit.getPlayer(getTarget()));
    }

}
