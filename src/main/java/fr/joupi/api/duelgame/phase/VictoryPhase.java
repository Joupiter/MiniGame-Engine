package fr.joupi.api.duelgame.phase;

import fr.joupi.api.game.Game;
import fr.joupi.api.game.GamePlayer;
import fr.joupi.api.game.GameState;
import fr.joupi.api.game.GameTeam;
import fr.joupi.api.game.event.GamePlayerJoinEvent;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import fr.joupi.api.game.phase.AbstractGamePhase;
import fr.joupi.api.threading.MultiThreading;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.TimeUnit;

public class VictoryPhase extends AbstractGamePhase {

    public VictoryPhase(Game<?> game) {
        super(game);
    }

    @Override
    public void onStart() {
        getGame().setState(GameState.END);
        GamePlayer winner = getGame().getAlivePlayers().get(0);

        registerEvent(AsyncPlayerChatEvent.class, event -> {
            getGame().getPlayer(event.getPlayer().getUniqueId())
                    .ifPresent(gamePlayer -> event.setFormat(ChatColor.translateAlternateColorCodes('&', getGame().getTeam(gamePlayer).map(GameTeam::getColoredName).orElse("&fAucune") + " &f%1$s &7: &f%2$s")));
        });

        registerEvent(GamePlayerJoinEvent.class, event -> {
            getGame().checkGameState(GameState.END, () -> {
                event.getPlayer().setGameMode(GameMode.SPECTATOR);
                event.getGamePlayer().sendMessage("&aLa partie est déjà terminée !");
                System.out.println("CHECK GAME STATE = END");
            });

            event.sendJoinMessage();
        });

        registerEvent(GamePlayerLeaveEvent.class, GamePlayerLeaveEvent::sendLeaveMessage);

        getGame().broadcast("&7&m-----------------------",
                "",
                "&b" + winner.getPlayer().getName() + " &egagne la partie !",
                "&eavec &b" + winner.getKills() + " &ekills et &b" + winner.getDeaths() + " &emorts !",
                "",
                "&7&m-----------------------");

        MultiThreading.schedule(this::endPhase, 10, TimeUnit.SECONDS);
        //scheduleAsyncTask(task -> endPhase(), 10 * 20L);
    }

    @Override
    public void onEnd() {
        // REMOVE THE GAME IN THE MAP OF GamesManager class
        getGame().getPlayers().values().stream().map(GamePlayer::getUuid).forEach(getGame()::leaveGame);
        System.out.println("END OF GAME : " + getGame().getFullName());
        // SEND TO HUB ect ...
    }

}
