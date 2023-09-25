package fr.joupi.api.listener;

import fr.joupi.api.AListener;
import fr.joupi.api.Spigot;
import fr.joupi.api.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
