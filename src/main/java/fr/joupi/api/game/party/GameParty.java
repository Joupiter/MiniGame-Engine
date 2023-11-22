package fr.joupi.api.game.party;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    private boolean opened;
    
    private final List<UUID> members;

    public GameParty(UUID leader) {
        this.leader = leader;
        this.name = "Partie de " + Bukkit.getPlayer(leader).getName();
        this.maxMembers = 5;
        this.opened = false;
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
            getMembers().stream().filter(isLeader().negate()).findAny().ifPresent(this::setLeader);
    }

    public void kickAll() {
        getMembers().stream().filter(isLeader().negate()).forEach(this::removeMember);
    }

    public List<Player> getPlayers() {
        return getMembers().stream().map(Bukkit::getPlayer).collect(Collectors.toList());
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getLeader());
    }

    public void sendMessages(String... messages) {
        Arrays.asList(messages)
                .forEach(message -> getPlayers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', message))));
    }

    private Predicate<UUID> isLeader() {
        return this::isLeader;
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
        player.sendMessage(String.format("Leader: %s", getPlayer().getName()));
        player.sendMessage(String.format("Name: %s", getName()));
        player.sendMessage(String.format("Max Player: %d", getMaxMembers()));
        player.sendMessage(String.format("Status public: %s", isOpened() ? "OUVERT" : "FERMER"));
        player.sendMessage(String.format("Size: %d", getSize()));
        player.sendMessage(String.format("Members: %s", getPlayers().stream().map(Player::getName).collect(Collectors.joining(", "))));
        player.sendMessage("-----------------------------");
    }

}
