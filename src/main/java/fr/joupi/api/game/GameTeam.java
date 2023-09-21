package fr.joupi.api.game;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

@Getter
public class GameTeam {

    private final String name;
    private final GameTeamColor color;
    private final List<GamePlayer> members;

    public GameTeam(GameTeamColor color) {
        this.name = color.getName();
        this.color = color;
        this.members = Lists.newLinkedList();
    }

    public void addMember(GamePlayer gamePlayer) {
        getMembers().add(gamePlayer);
    }

    public void removeMember(GamePlayer gamePlayer) {
        getMembers().remove(gamePlayer);
    }

    public boolean isMember(GamePlayer gamePlayer) {
        return getMembers().contains(gamePlayer);
    }

    public int getSize() {
        return getMembers().size();
    }

    public String getColoredName() {
        return getColor().getChatColor() + getName();
    }

}
