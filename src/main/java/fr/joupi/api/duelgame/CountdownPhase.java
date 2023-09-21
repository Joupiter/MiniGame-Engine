package fr.joupi.api.duelgame;

import fr.joupi.api.CountdownTimer;
import fr.joupi.api.game.Game;
import fr.joupi.api.game.GamePlayer;
import fr.joupi.api.game.phase.AbstractGamePhase;
import lombok.Getter;

@Getter
public class CountdownPhase extends AbstractGamePhase {

    private final CountdownTimer countdownTimer;

    public CountdownPhase(Game<?> game) {
        super(game);
        this.countdownTimer = new CountdownTimer(getGame().getPlugin(), 15);
    }

    @Override
    protected void startPhase() {
        getCountdownTimer().setBeforeTimer(() -> getGame().broadcast("&eLa partie va se lancer !"));
        getCountdownTimer().setAfterTimer(this::end);
        getCountdownTimer().setEverySecond(timer -> getGame().getPlayers().values().stream().map(GamePlayer::getPlayer).forEach(player -> player.getPlayer().setLevel(timer.getSecondsLeft())));

        getCountdownTimer().scheduleTimer();
    }

    @Override
    protected void endPhase() {
        getCountdownTimer().cancelTimer();
    }

}
