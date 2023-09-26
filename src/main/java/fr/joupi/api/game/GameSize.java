package fr.joupi.api.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GameSize {

    private String name;
    private int minPlayer, maxPlayer, teamNeeded, teamMaxPlayer;

}