package fr.joupi.api.listener;

import fr.joupi.api.Spigot;
import fr.joupi.api.duelgame.DuelGame;
import fr.joupi.api.game.GameSize;
import fr.joupi.api.game.event.GamePlayerJoinEvent;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import fr.joupi.api.shop.ShopGui;
import fr.joupi.api.skyly.PlayerGameListGui;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@Getter
public class GameListener implements Listener {

    private final Spigot plugin;

    public GameListener(Spigot plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onGamePlayerJoin(GamePlayerJoinEvent event) {
        /*Game<?> game = event.getGame();
        GamePlayer gamePlayer = event.getGamePlayer();

        game.checkGameState(GameState.WAIT, () -> {
            setTimer(getNewCountdownTimer(game));

            if (game.getSettings().getSize().getMinPlayer() == game.getSize())
                getTimer().scheduleTimer();

            event.sendJoinMessage();
            System.out.println("CHECK GAME STATE = WAIT");
        });

        game.checkGameState(GameState.IN_GAME, () -> {
            gamePlayer.getPlayer().setGameMode(GameMode.SPECTATOR);
            gamePlayer.sendMessage("&aLa partie est déjà commencer !");
            System.out.println("CHECK GAME STATE = IG");
        });

        game.checkGameState(GameState.END, () -> {
            gamePlayer.getPlayer().setGameMode(GameMode.SPECTATOR);
            gamePlayer.sendMessage("&aLa partie est déjà terminée !");
            System.out.println("CHECK GAME STATE = END");
        });*/
    }

    @EventHandler
    public void onGamePlayerLeave(GamePlayerLeaveEvent event) {
        /*Game<?> game = event.getGame();

        game.checkGameState(GameState.WAIT, () -> {
            if (game.getSize() < game.getSettings().getSize().getMinPlayer()) {
                getTimer().cancelTimer();
                System.out.println("CANCEL TIMER BCS HAVING NO MUCH PLAYER FOR START!");
            }
        });

        event.sendLeaveMessage();*/
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        /**
         *  TEST
         */

        if (event.getMessage().equals("!addgame")) {
            getPlugin().getGameManager().addGame("duel", new DuelGame(getPlugin(), GameSize.SIZE_1V1));
            event.setCancelled(true);
        }

        if (event.getMessage().equals("!gui")) {
            new PlayerGameListGui(getPlugin(), player).onOpen(player);
            event.setCancelled(true);
        }

        if (event.getMessage().equals("!test")) {
            new ShopGui(getPlugin(), player).onOpen(player);
            event.setCancelled(true);
        }

    }

}
