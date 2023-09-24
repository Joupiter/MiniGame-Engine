package fr.joupi.api.duelgame.phase;

import fr.joupi.api.game.Game;
import fr.joupi.api.game.GameState;
import fr.joupi.api.game.event.GamePlayerJoinEvent;
import fr.joupi.api.game.phase.AbstractGamePhase;
import org.bukkit.GameMode;

public class WaitingPhase extends AbstractGamePhase {

    public WaitingPhase(Game game) {
        super(game);
    }

    @Override
    public void onStart() {
        registerEvent(GamePlayerJoinEvent.class, event -> {
            if (canTriggerEvent(event.getPlayer().getUniqueId()))
                getGame().checkGameState(GameState.WAIT, () -> {
                    event.getPlayer().setGameMode(GameMode.ADVENTURE);
                    canEnd();
                });
        });
    }

    private void canEnd() {
        if (getGame().getSettings().getSize().getMinPlayer() == getGame().getSize())
            endPhase();
    }

    @Override
    public void onEnd() { }

}
