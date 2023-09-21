package fr.joupi.api.duelgame;

import fr.joupi.api.game.Game;
import fr.joupi.api.game.event.GamePlayerJoinEvent;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import fr.joupi.api.game.phase.AbstractGamePhase;
import org.bukkit.GameMode;

public class WaitingPhase extends AbstractGamePhase {

    public WaitingPhase(Game<?> game) {
        super(game);
    }

    @Override
    protected void startPhase() {
        registerEvent(GamePlayerJoinEvent.class, event -> {
            canEnd();
            event.sendJoinMessage();
            scheduleAsyncTask(task -> event.getPlayer().setGameMode(GameMode.ADVENTURE), 1L);
        });

        registerEvent(GamePlayerLeaveEvent.class, GamePlayerLeaveEvent::sendLeaveMessage);
        canEnd();
    }

    private void canEnd() {
        if (getGame().getPlayers().size() >= getGame().getSettings().getSize().getMinPlayer())
            end();
    }

    @Override
    protected void endPhase() {

    }

}
