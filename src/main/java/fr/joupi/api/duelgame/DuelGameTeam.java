package fr.joupi.api.duelgame;

import fr.joupi.api.game.team.GameTeam;
import fr.joupi.api.game.team.GameTeamColor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DuelGameTeam extends GameTeam {

    private int score;

    public DuelGameTeam(GameTeamColor color) {
        super(color);
        this.score = 0;
    }

    public void addScore() {
        setScore(getScore() + 1);
    }

}
