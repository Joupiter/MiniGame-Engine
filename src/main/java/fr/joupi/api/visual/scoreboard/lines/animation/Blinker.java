package fr.joupi.api.visual.scoreboard.lines.animation;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Blinker implements LineAnimation {

    private final int delay;
    @Setter private int counter = 0;
    private final String[] lines;

    public Blinker(int delay, String... lines) {
        this.delay = delay;
        this.lines = lines;
    }

    @Override
    public int id() {
        return 1;
    }

    @Override
    public String next() {
        if (counter >= lines.length) counter = 0;
        return lines[counter++];
    }

    @Override
    public int getDelay() {
        return getCounter();
    }

}
