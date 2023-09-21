package fr.joupi.api.listener;

import fr.joupi.api.CountdownTimer;
import fr.joupi.api.Spigot;
import fr.joupi.api.game.Game;
import fr.joupi.api.game.GamePlayer;
import fr.joupi.api.game.GameState;
import fr.joupi.api.game.GameTeam;
import fr.joupi.api.game.event.GamePlayerJoinEvent;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import fr.joupi.api.game.event.GameStartEvent;
import fr.joupi.api.game.event.GameStopEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

@Getter
public class GameListener implements Listener {

    private final Spigot plugin;
    @Setter private CountdownTimer timer;

    public GameListener(Spigot plugin) {
        this.plugin = plugin;
    }

    private CountdownTimer getNewCountdownTimer(Game<?> game) {
        return new CountdownTimer(getPlugin(), 25,
                () -> game.broadcast("&eLa partie va se lancer !"),
                () -> Bukkit.getServer().getPluginManager().callEvent(new GameStartEvent(game)),
                countdown -> game.getPlayers().values().stream().map(GamePlayer::getPlayer).forEach(player -> player.setLevel(countdown.getSecondsLeft())));
    }

    @EventHandler
    public void onGamePlayerJoin(GamePlayerJoinEvent event) {
        Game<?> game = event.getGame();
        GamePlayer gamePlayer = event.getGamePlayer();

        game.checkGameState(GameState.WAIT, () -> {
            setTimer(getNewCountdownTimer(game));

            if (game.getSettings().getSize().getMinPlayer() == game.getSize())
                getTimer().scheduleTimer();

            event.sendJoinMessage();
        });

        game.checkGameState(GameState.IN_GAME, () -> {
            gamePlayer.setSpectator();
            gamePlayer.sendMessage("&aLa partie est déjà commencer !");
        });

        game.checkGameState(GameState.END, () -> {
            gamePlayer.setSpectator();
            gamePlayer.sendMessage("&aLa partie est déjà terminée !");
        });
    }

    @EventHandler
    public void onGamePlayerLeave(GamePlayerLeaveEvent event) {
        Game<?> game = event.getGame();

        game.checkGameState(GameState.WAIT, () -> {
            if (game.getSize() < game.getSettings().getSize().getMinPlayer())
                getTimer().cancelTimer();
        });

        event.sendLeaveMessage();
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        Game<?> game = event.getGame();

        game.setState(GameState.IN_GAME);
        game.broadcast("&eDémarage de la partie !!");
    }

    @EventHandler
    public void onGameEnd(GameStopEvent event) {
        Game<?> game = event.getGame();
        game.setState(GameState.END);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Game<Spigot> game = getPlugin().getDuelGame();

        if (event.getMessage().equals("!timerstop")) {
            Optional.ofNullable(getTimer()).ifPresentOrElse(CountdownTimer::cancelTimer, () -> player.sendMessage("timer is null"));
            event.setCancelled(true);
        }

        game.getPlayer(player.getUniqueId())
                .ifPresent(gamePlayer -> event.setFormat(ChatColor.translateAlternateColorCodes('&', game.getTeam(gamePlayer).map(GameTeam::getColoredName).orElse("&fAucune") + " &f%1$s &7: &f%2$s")));
    }

}
