package fr.joupi.api.game.party;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GamePartyRequest {

    private final UUID sender, target;

}
