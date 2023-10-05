package fr.joupi.api.listener;

import fr.joupi.api.AListener;
import fr.joupi.api.Spigot;
import fr.joupi.api.User;
import fr.joupi.api.duelgame.DuelGame;
import fr.joupi.api.game.GameSizeTemplate;
import fr.joupi.api.game.phase.AbstractGamePhase;
import fr.joupi.api.shop.ShopGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        /**
         *  TEST
         */

        if (event.getMessage().equals("!addgame")) {
            getPlugin().getGameManager().addGame("duel", new DuelGame(getPlugin(), GameSizeTemplate.SIZE_1V1.getGameSize()));
            event.setCancelled(true);
        }

        if (event.getMessage().equals("!addhost")) {
            getPlugin().getGameManager().addGame("duelhost", new DuelGame(getPlugin(), player, GameSizeTemplate.SIZE_1V1.getGameSize()));
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

        if (event.getMessage().equals("!list")) {

            Arrays.stream(getPlugin().getServer().getPluginManager().getPlugins())
                    .filter(plugin -> plugin.getName().equals(getPlugin().getName()))
                    .map(HandlerList::getRegisteredListeners)
                    .forEach(rls ->
                            rls.forEach(registeredListener ->
                                    Arrays.stream(registeredListener.getListener().getClass().getDeclaredMethods())
                                            .filter(method -> method.isAnnotationPresent(EventHandler.class))
                                            .forEach(method -> System.out.println(registeredListener.getListener().getClass().getSimpleName() + " = " + method.getName())))
                    );

            event.setCancelled(true);
        }

        if (event.getMessage().equals("!test")) {
            new ShopGui(getPlugin(), player).onOpen(player);
            event.setCancelled(true);
        }

        if (event.getMessage().equals("!npc")) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        getPlugin().getGameManager().leave(player);
        getPlugin().getUsers().remove(getPlugin().getUser(player.getUniqueId()));
    }

}
