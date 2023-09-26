package fr.joupi.api.duelgame.phase;

import fr.joupi.api.duelgame.DuelGame;
import fr.joupi.api.game.GameState;
import fr.joupi.api.game.event.GamePlayerJoinEvent;
import fr.joupi.api.game.phase.AbstractGamePhase;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class WaitingPhase extends AbstractGamePhase<DuelGame> {

    public WaitingPhase(DuelGame game) {
        super(game);
    }

    @Override
    public void onStart() {
        registerEvent(GamePlayerJoinEvent.class, event -> {
            Player player = event.getPlayer();
            if (canTriggerEvent(player.getUniqueId()))
                getGame().checkGameState(GameState.WAIT, () -> {
                    player.setGameMode(GameMode.ADVENTURE);
                    player.teleport(getGame().getSettings().getLocation("waiting"));
                    canEnd();
                });
        });
    }

    private void canEnd() {
        if (getGame().getSettings().getGameSize().getMinPlayer() == getGame().getSize())
            endPhase();
    }

    @Override
    public void onEnd() { }

}
