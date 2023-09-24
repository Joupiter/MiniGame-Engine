package fr.joupi.api.listener;

import com.google.common.collect.ImmutableList;
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
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        getPlugin().getUsers().add(new User(player.getUniqueId(), 1));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        getPlugin().getGameManager().getGame(player, game -> game.leaveGame(player.getUniqueId()));
        getPlugin().getUsers().remove(getPlugin().getUser(player.getUniqueId()));
    }

}
