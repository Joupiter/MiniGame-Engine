package fr.joupi.api.visual.scoreboard.lines;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TextLine implements Line {

    private String text;

    @Override
    public String next() {
        return text;
    }

}
