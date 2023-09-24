package fr.joupi.api.duelgame.phase;

import fr.joupi.api.Spigot;
import fr.joupi.api.game.*;
import fr.joupi.api.game.event.GamePlayerJoinEvent;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import fr.joupi.api.game.phase.AbstractGamePhase;
import fr.joupi.api.threading.MultiThreading;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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

        registerEvent(AsyncPlayerChatEvent.class, event -> {
            getGame().getPlayer(event.getPlayer().getUniqueId())
                    .ifPresent(gamePlayer -> event.setFormat(ChatColor.translateAlternateColorCodes('&', getGame().getTeam(gamePlayer).map(GameTeam::getColoredName).orElse("&fAucune") + " &f%1$s &7: &f%2$s")));
        });

        registerEvent(GamePlayerJoinEvent.class, event -> {
            if (canTriggerEvent(event.getPlayer().getUniqueId())) {
                getGame().checkGameState(GameState.END, () -> {
                    event.getPlayer().setGameMode(GameMode.SPECTATOR);
                    event.getGamePlayer().sendMessage("&aLa partie est déjà terminée !");
                    System.out.println("CHECK GAME STATE = END");
                });

                event.sendJoinMessage();
            }
        });

        registerEvent(GamePlayerLeaveEvent.class, event -> {
            if (canTriggerEvent(event.getPlayer().getUniqueId())) {
                event.getGame().getPlayers().remove(event.getPlayer().getUniqueId());
                event.sendLeaveMessage();
            }
        });

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
