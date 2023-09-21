package fr.joupi.api.duelgame;

import fr.joupi.api.game.Game;
import fr.joupi.api.game.GamePlayer;
import fr.joupi.api.game.GameState;
import fr.joupi.api.game.phase.AbstractGamePhase;
import fr.joupi.api.threading.MultiThreading;

import java.util.concurrent.TimeUnit;

public class VictoryPhase extends AbstractGamePhase {

    public VictoryPhase(Game<?> game) {
        super(game);
    }

    @Override
    protected void startPhase() {
        getGame().setState(GameState.END);
        GamePlayer winner = getGame().getAlivePlayers().get(0);

        getGame().broadcast("&7&m-----------------------",
                "",
                "&b" + winner.getPlayer().getName() + " &egagne la partie !",
                "&eavec &b" + winner.getKills() + " &ekills et &b" + winner.getDeaths() + " &emorts !",
                "",
                "&7&m-----------------------");

        MultiThreading.schedule(() -> getGame().getPlayers().values().stream().map(GamePlayer::getUuid).forEach(getGame()::leaveGame), 10, TimeUnit.SECONDS);
    }

    @Override
    protected void endPhase() {

    }

}
