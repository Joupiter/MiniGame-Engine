package fr.joupi.api.duelgame.phase;

import fr.joupi.api.duelgame.DuelGame;
import fr.joupi.api.game.GameState;
import fr.joupi.api.game.event.GamePlayerJoinEvent;
import fr.joupi.api.game.phase.AbstractGamePhase;
import org.bukkit.entity.Player;

public class WaitingPhase extends AbstractGamePhase<DuelGame> {

    public WaitingPhase(DuelGame game) {
        super(game);
    }

    @Override
    public void onStart() {
        registerEvent(GamePlayerJoinEvent.class, event -> {
            if (canTriggerEvent(event.getPlayer()))
                getGame().checkGameState(GameState.WAIT, this::canEnd);
        });
    }

    private void canEnd() {
        if (getGame().canStart())
            endPhase();
    }

    @Override
    public void onEnd() { }

}
