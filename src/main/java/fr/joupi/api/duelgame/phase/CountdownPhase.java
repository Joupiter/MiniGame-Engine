package fr.joupi.api.duelgame.phase;

import fr.joupi.api.CountdownTimer;
import fr.joupi.api.game.Game;
import fr.joupi.api.game.GamePlayer;
import fr.joupi.api.game.GameState;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import fr.joupi.api.game.phase.AbstractGamePhase;
import lombok.Getter;

@Getter
public class CountdownPhase extends AbstractGamePhase {

    private final CountdownTimer countdownTimer;

    public CountdownPhase(Game game) {
        super(game);
        this.countdownTimer = new CountdownTimer(getGame().getPlugin(), 10);
    }

    @Override
    public void onStart() {
        getCountdownTimer().setBeforeTimer(() -> getGame().broadcast("&eLa partie va se lancer !"));
        getCountdownTimer().setEverySecond(timer -> getGame().getPlayers().values().stream().map(GamePlayer::getPlayer).forEach(player -> player.getPlayer().setLevel(timer.getSecondsLeft())));
        getCountdownTimer().setAfterTimer(this::endPhase);

        registerEvent(GamePlayerLeaveEvent.class, event -> {
            if (canTriggerEvent(event.getPlayer().getUniqueId()))
                getGame().checkGameState(GameState.WAIT, this::checkCanCancelPhase);
        });

        getCountdownTimer().setSecondsLeft(10);
        getCountdownTimer().scheduleTimer();
    }

    private void checkCanCancelPhase() {
        if (getGame().getSize() - 1  < getGame().getSettings().getSize().getMinPlayer()) {
            getCountdownTimer().cancelTimer();
            cancelPhase();
        }
    }

    @Override
    public void onEnd() {
        getGame().broadcast("&eBonne chance !");
        getCountdownTimer().cancelTimer();
    }

}
