package fr.joupi.api.listener;

import fr.joupi.api.AListener;
import fr.joupi.api.Spigot;
import fr.joupi.api.User;
import fr.joupi.api.duelgame.DuelGame;
import fr.joupi.api.game.utils.GameSizeTemplate;
import fr.joupi.api.shop.ShopGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        /**
         *  TEST
         */

        if (event.getMessage().equals("!addgame")) {
            getPlugin().getGameManager().addGame("duel", new DuelGame(getPlugin(), GameSizeTemplate.SIZE_1V1.getGameSize().clone()));
            event.setCancelled(true);
        }

        if (event.getMessage().equals("!addhost")) {
            getPlugin().getGameManager().addGame("duel", new DuelGame(getPlugin(), player, GameSizeTemplate.SIZE_1V1.getGameSize().clone()));
            event.setCancelled(true);
        }

        if (event.getMessage().equals("!newpagegui")) {
            getPlugin().getGuiManager().open(player, new fdg8yfht15(getPlugin()));
            event.setCancelled(true);
        }

        if (event.getMessage().equals("!newgui")) {
            getPlugin().getGuiManager().open(player, new TestGui(getPlugin()));
            event.setCancelled(true);
        }

        if (event.getMessage().equals("!spec")) {
            event.setCancelled(true);
        }

        if (event.getMessage().equals("!test")) {
            new ShopGui(getPlugin(), player).onOpen(player);
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        getPlugin().getGameManager().getPartyManager().onLeave(player);
        getPlugin().getGameManager().leaveGame(player);
        getPlugin().getGameManager().getGameHost(player).ifPresent(game -> game.endGame(getPlugin().getGameManager()));
        getPlugin().getUsers().remove(getPlugin().getUser(player.getUniqueId()));
    }

}
