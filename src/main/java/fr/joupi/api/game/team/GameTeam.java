package fr.joupi.api.game.team;

import fr.joupi.api.Utils;
import fr.joupi.api.game.GamePlayer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public abstract class GameTeam {

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
        Utils.debug("Team - {0} added to {1} team", gamePlayer.getPlayer().getName(), getName());
    }

    public void removeMember(GamePlayer gamePlayer) {
        getMembers().remove(gamePlayer);
        Utils.debug("Team - {0} removed to {1} team", gamePlayer.getPlayer().getName(), getName());
    }

    public boolean isMember(GamePlayer gamePlayer) {
        return getMembers().contains(gamePlayer);
    }

    public boolean isMember(UUID uuid) {
        return getMembers().stream().anyMatch(gamePlayer -> gamePlayer.getUuid().equals(uuid));
    }

    public List<GamePlayer> getAlivePlayers() {
        return getMembers().stream().filter(isSpectatorPredicate().negate()).collect(Collectors.toList());
    }

    private Predicate<GamePlayer> isSpectatorPredicate() {
        return GamePlayer::isSpectator;
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
