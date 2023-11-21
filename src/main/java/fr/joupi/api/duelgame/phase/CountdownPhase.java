package fr.joupi.api.duelgame.phase;

import fr.joupi.api.game.utils.CountdownTimer;
import fr.joupi.api.duelgame.DuelGame;
import fr.joupi.api.game.GamePlayer;
import fr.joupi.api.game.GameState;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import fr.joupi.api.game.phase.AbstractGamePhase;
import lombok.Getter;

public class CountdownPhase extends AbstractGamePhase<DuelGame> {

    @Getter private final CountdownTimer countdownTimer;

    public CountdownPhase(DuelGame game) {
        super(game);
        this.countdownTimer = new CountdownTimer(getGame().getPlugin(), 10);
    }

    @Override
    public void onStart() {
        getCountdownTimer().setBeforeTimer(() -> getGame().broadcast("&eLa partie va se lancer !"));
        getCountdownTimer().setEverySecond(timer -> getGame().getPlayers().values().stream().map(GamePlayer::getPlayer).forEach(player -> player.setLevel(timer.getSecondsLeft())));
        getCountdownTimer().setAfterTimer(this::endPhase);

        registerEvent(GamePlayerLeaveEvent.class, GamePlayerLeaveEvent::getPlayer, event -> getGame().checkGameState(GameState.WAIT, this::checkCanCancelPhase));

        getCountdownTimer().setSecondsLeft(10);
        getCountdownTimer().scheduleTimer();
    }

    private void checkCanCancelPhase() {
        if (getGame().getSize() - 1  < getGame().getSettings().getGameSize().getMinPlayer()) {
            getCountdownTimer().cancelTimer();
            cancelPhase();
        }
    }

    @Override
    public void onCancel() {
        getCountdownTimer().cancelTimer();
    }

    @Override
    public void onEnd() {
        getGame().broadcast("&eBonne chance !");
        getCountdownTimer().cancelTimer();
    }

}
