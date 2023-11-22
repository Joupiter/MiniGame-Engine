package fr.joupi.api.duelgame.phase;

import fr.joupi.api.Spigot;
import fr.joupi.api.duelgame.DuelGame;
import fr.joupi.api.duelgame.DuelGamePlayer;
import fr.joupi.api.game.GameState;
import fr.joupi.api.game.phase.AbstractGamePhase;
import fr.joupi.api.threading.MultiThreading;
import lombok.Getter;
import org.bukkit.GameMode;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Getter
public class VictoryPhase extends AbstractGamePhase<DuelGame> {

    private final Spigot spigot;

    public VictoryPhase(DuelGame game, Spigot spigot) {
        super(game);
        this.spigot = spigot;
    }

    @Override
    public void onStart() {
        getGame().setState(GameState.END);

        Optional.ofNullable(getGame().getAlivePlayers().get(0)).ifPresent(gamePlayer ->
                getGame().broadcast("&7&m-----------------------",
                        "",
                        "&b" + gamePlayer.getPlayer().getName() + " &egagne la partie !",
                        "&eavec &b" + gamePlayer.getKills() + " &ekills et &b" + gamePlayer.getDeaths() + " &emorts !",
                        "              &9&lGG WP",
                        "&7&m-----------------------"));

        getGame().getAlivePlayers().forEach(gamePlayer -> {
            gamePlayer.setSpectator(true);
            gamePlayer.getPlayer().setGameMode(GameMode.SPECTATOR);
        });

        MultiThreading.schedule(this::endPhase, 10, TimeUnit.SECONDS);
    }

    @Override
    public void onEnd() {
        getGame().endGame(getSpigot().getGameManager());
    }

}
