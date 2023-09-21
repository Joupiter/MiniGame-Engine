package fr.joupi.api.visual.scoreboard.lines.animation;

import fr.joupi.api.visual.scoreboard.lines.Line;

public interface LineAnimation extends Line {

    int id();

    String next();

    int getDelay();

}
