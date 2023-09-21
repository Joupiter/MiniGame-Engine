package fr.joupi.api.visual.scoreboard;

import fr.joupi.api.visual.scoreboard.lines.Line;
import fr.joupi.api.visual.scoreboard.lines.animation.LineAnimation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.scoreboard.Team;

@Getter
@Setter
public class BoardEntry {

    private final Team team;
    private final String entry;
    private final Line line;
    private int animationIndex = 0;

    public BoardEntry(Team team, String entry, Line line) {
        this.team = team;
        this.entry = entry;
        this.line = line;
    }

    public Team getTeam() {
        return team;
    }

    public String getEntry() {
        return entry;
    }

    public Line getLine() {
        return line;
    }

    public boolean executeNextAnimationFrame(LineAnimation animation) {
        setAnimationIndex(StrictMath.floorMod(getAnimationIndex() + 1, animation.getDelay()));
        return getAnimationIndex() == 0;
    }

}