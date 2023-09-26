package fr.joupi.api.duelgame;

import fr.joupi.api.Spigot;
import fr.joupi.api.duelgame.phase.CountdownPhase;
import fr.joupi.api.duelgame.phase.DuelPhase;
import fr.joupi.api.duelgame.phase.VictoryPhase;
import fr.joupi.api.duelgame.phase.WaitingPhase;
import fr.joupi.api.game.*;
import fr.joupi.api.game.event.GamePlayerJoinEvent;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class DuelGame extends Game<DuelGamePlayer> {

    public DuelGame(Spigot plugin, GameSize gameSize) {
        super(plugin, "Duel", new GameSettings(gameSize, Bukkit.getWorld("world")));

        getSettings().addLocation("waiting", new Location(getSettings().getWorld(), -171, 75, 57, -176, 2));
        getSettings().addLocation("red", new Location(getSettings().getWorld(), -179, 67, 74));
        getSettings().addLocation("blue", new Location(getSettings().getWorld(), -189, 67, 74));

        /*
            Les phases doivent être dans l'ordre !
         */
        getPhaseManager().addPhase(
                new WaitingPhase(this),
                new CountdownPhase(this),
                new DuelPhase(this),
                new VictoryPhase(this, plugin)
        );

        getPhaseManager().start();
    }

    @Override
    public DuelGamePlayer defaultGamePlayer(UUID uuid) {
        return new DuelGamePlayer(uuid, 0, 0, 0, !getState().equals(GameState.WAIT));
    }

    /*
        Listeners
     */

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGamePlayerJoin(GamePlayerJoinEvent<DuelGamePlayer> event) {
        GamePlayer gamePlayer = event.getGamePlayer();

        if (containsPlayer(event.getPlayer().getUniqueId())) {
            checkGameState(GameState.IN_GAME, () -> {
                gamePlayer.setSpectator(true);
                event.getPlayer().setGameMode(GameMode.SPECTATOR);
                gamePlayer.sendMessage("&aLa partie est déjà commencer !");
            });

            checkGameState(GameState.END, () -> {
                gamePlayer.setSpectator(true);
                event.getPlayer().setGameMode(GameMode.SPECTATOR);
                gamePlayer.sendMessage("&aLa partie est déjà terminée !");
            });

            event.sendJoinMessage();
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (containsPlayer(event.getPlayer().getUniqueId()))
            getPlayer(event.getPlayer().getUniqueId())
                    .ifPresent(gamePlayer -> event.setFormat(ChatColor.translateAlternateColorCodes('&', getTeam(gamePlayer).map(GameTeam::getColoredName).orElse("&fAucune") + " &f%1$s &7: &f%2$s")));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGamePlayerLeave(GamePlayerLeaveEvent<DuelGamePlayer> event) {
        if (containsPlayer(event.getPlayer().getUniqueId())) {
            getPlayers().remove(event.getPlayer().getUniqueId());
            event.sendLeaveMessage();
        }
    }

}
