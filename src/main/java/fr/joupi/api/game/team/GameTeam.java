package fr.joupi.api.game.team;

import fr.joupi.api.game.GamePlayer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class GameTeam {

    private final String name;
    private final GameTeamColor color;
    private final List<GamePlayer> members;

    public GameTeam(GameTeamColor color) {
        this.name = color.getName();
        this.color = color;
        this.members = new ArrayList<>();
    }

    public void addMember(GamePlayer gamePlayer) {
        getMembers().add(gamePlayer);
        System.out.println("[Team] " + gamePlayer.getPlayer().getName() + " added to " + getName() + " team");
    }

    public void removeMember(GamePlayer gamePlayer) {
        getMembers().remove(gamePlayer);
        System.out.println("[Team] " + gamePlayer.getPlayer().getName() + " removed to " + getName() + " team");
    }

    public boolean isMember(GamePlayer gamePlayer) {
        return getMembers().contains(gamePlayer);
    }

    public List<GamePlayer> getAlivePlayers() {
        return getMembers().stream().filter(((Predicate<? super GamePlayer>) GamePlayer::isSpectator).negate()).collect(Collectors.toList());
    }

    public boolean isNoPlayersAlive() {
        return getAlivePlayers().isEmpty();
    }

    public int getSize() {
        return getMembers().size();
    }

    public String getColoredName() {
        return getColor().getChatColor() + getName();
    }

}