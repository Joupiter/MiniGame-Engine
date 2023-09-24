package fr.joupi.api.duelgame.phase;

import fr.joupi.api.Spigot;
import fr.joupi.api.game.Game;
import fr.joupi.api.game.GamePlayer;
import fr.joupi.api.game.GameState;
import fr.joupi.api.game.phase.AbstractGamePhase;
import fr.joupi.api.threading.MultiThreading;
import lombok.Getter;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class VictoryPhase extends AbstractGamePhase {

    @Getter private final Spigot spigot;

    public VictoryPhase(Game game, Spigot spigot) {
        super(game);
        this.spigot = spigot;
    }

    @Override
    public void onStart() {
        getGame().setState(GameState.END);
        Optional<GamePlayer> winner = Optional.ofNullable(getGame().getAlivePlayers().get(0));

        winner.ifPresent(gamePlayer ->
                getGame().broadcast("&7&m-----------------------",
                        "",
                        "&b" + gamePlayer.getPlayer().getName() + " &egagne la partie !",
                        "&eavec &b" + gamePlayer.getKills() + " &ekills et &b" + gamePlayer.getDeaths() + " &emorts !",
                        "              &9&lGG WP",
                        "&7&m-----------------------"));

        MultiThreading.schedule(this::endPhase, 10, TimeUnit.SECONDS);
    }

    @Override
    public void onEnd() {
        getGame().getPlayers().values().stream().map(GamePlayer::getUuid).forEach(getGame()::leaveGame);
        getSpigot().getGameManager().removeGame(getGame());
        System.out.println("END OF GAME : " + getGame().getFullName());
    }

}
