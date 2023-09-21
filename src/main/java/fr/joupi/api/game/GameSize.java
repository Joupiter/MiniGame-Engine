package fr.joupi.api.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameSize {

    SIZE_1V1 ("1vs1", 1, 2, 2, 1),
    SIZE_2V2 ("2vs2", 2, 4, 2, 2),
    SIZE_5V5 ("5vs5", 5, 10,2, 5),
    SIZE_10V10 ("10vs10", 10, 20, 2, 10);

    private final String name;
    private final int minPlayer, maxPlayer, teamNeeded, teamMaxPlayer;

}