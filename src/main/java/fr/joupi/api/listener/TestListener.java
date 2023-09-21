package fr.joupi.api.listener;

import fr.joupi.api.AListener;
import fr.joupi.api.CountdownTimer;
import fr.joupi.api.Spigot;
import fr.joupi.api.User;
import fr.joupi.api.game.Game;
import fr.joupi.api.game.GamePlayer;
import fr.joupi.api.game.GameTeam;
import fr.joupi.api.shop.ShopGui;
import io.reactivex.rxjava3.core.Observable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.stream.Collectors;

public class TestListener extends AListener<Spigot> {

    public TestListener(Spigot plugin) {
        super(plugin);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Game<Spigot> game = getPlugin().getDuelGame();

        if (event.getMessage().equals("!test")) {
            CountdownTimer timer = new CountdownTimer(getPlugin(), 10,
                    () -> player.sendMessage(">> before timer"),
                    () -> player.sendMessage(">> after timer"),
                    seconds -> player.sendMessage(">> " + seconds.getSecondsLeft()));

            timer.scheduleTimer();
            event.setCancelled(true);
        }

        if (event.getMessage().equals("!join")) {
            game.joinGame(new GamePlayer(player.getUniqueId(), 0, 0, false));
            event.setCancelled(true);
        }

        if (event.getMessage().equals("!leave")) {
            game.leaveGame(player.getUniqueId());
            event.setCancelled(true);
        }

        if (event.getMessage().equals("!info")) {
            game.getTeams().forEach(gameTeam -> player.sendMessage(gameTeam.getName() + ": " + gameTeam.getMembers().stream().map(GamePlayer::getUuid).map(UUID::toString).collect(Collectors.joining(", "))));
            event.setCancelled(true);
        }

        if (event.getMessage().equals("!team")) {
            game.fillTeam();
            event.setCancelled(true);
        }

        if (event.getMessage().equals("!test")) {
            new ShopGui(getPlugin(), player).onOpen(player);
            event.setCancelled(true);
        }

        if (event.getMessage().equals("!rx")) {
            Observable<GameTeam> teamObservable = Observable.fromIterable(game.getTeams());

            teamObservable.subscribe(
                    gameTeam -> Bukkit.broadcastMessage(gameTeam.getName() + ":" + gameTeam.getMembers().stream().map(GamePlayer::getPlayer).map(Player::getName).collect(Collectors.joining(","))),
                    throwable -> Bukkit.broadcastMessage("Error: " + throwable.getMessage()),
                    () -> Bukkit.broadcastMessage("Completed!")
            );

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        getPlugin().getUsers().add(new User(player.getUniqueId(), 1));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        getPlugin().getDuelGame().leaveGame(event.getPlayer().getUniqueId());
        getPlugin().getUsers().remove(getPlugin().getUser(player.getUniqueId()));
    }

}
