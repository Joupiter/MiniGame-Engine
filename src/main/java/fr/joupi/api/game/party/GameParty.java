package fr.joupi.api.game.party;

import fr.joupi.api.game.GamePlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@Setter
public class GameParty {

    private UUID leader;
    private String name;
    private int maxMembers;

    private final List<UUID> members;

    public GameParty(UUID leader) {
        this.leader = leader;
        this.maxMembers = 5;
        this.name = "Partie de " + Bukkit.getPlayer(leader).getName();
        this.members = new ArrayList<>(Collections.singletonList(leader));
    }

    public void addMember(UUID uuid) {
        getMembers().add(uuid);
    }

    public void removeMember(UUID uuid) {
        getMembers().remove(uuid);
    }

    public boolean isMember(UUID uuid) {
        return getMembers().contains(uuid);
    }

    public void canSetNewRandomLeader(Player player) {
        if (isLeader(player.getUniqueId()))
            getMembers().stream().filter(((Predicate<? super UUID>) this::isLeader).negate()).findAny().ifPresent(this::setLeader);
    }

    public void kickAll() {
        getMembers().stream().filter(((Predicate<? super UUID>) this::isLeader).negate()).forEach(this::removeMember);
    }

    public List<Player> getPlayers() {
        return getMembers().stream().map(Bukkit::getPlayer).collect(Collectors.toList());
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getLeader());
    }

    public boolean isComplete() {
        return getMembers().size() == getMaxMembers();
    }

    public boolean isEmpty() {
        return getMembers().isEmpty();
    }

    public boolean isLeader(UUID uuid) {
        return getLeader().equals(uuid);
    }

    public int getSize() {
        return getMembers().size();
    }

    public void sendDebug(Player player) {
        player.sendMessage("-----------------------------");
        player.sendMessage("Leader: " + getPlayer().getName());
        player.sendMessage("Name: " + getName());
        player.sendMessage("Max Player: " + getMaxMembers());
        player.sendMessage("Size: " + getSize());
        player.sendMessage("Members: " + getPlayers().stream().map(Player::getName).collect(Collectors.joining(", ")));
        player.sendMessage("-----------------------------");
    }

}
