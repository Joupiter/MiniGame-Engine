package fr.joupi.api.listener;

import fr.joupi.api.AListener;
import fr.joupi.api.Spigot;
import fr.joupi.api.duelgame.DuelGame;
import fr.joupi.api.game.utils.GameSizeTemplate;
import fr.joupi.api.particle.ParticleTest;
import fr.joupi.api.threading.MultiThreading;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;

public class TestListener extends AListener<Spigot> {

    public TestListener(Spigot plugin) {
        super(plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        /*
         *  TEST
         */

        if (event.getMessage().equalsIgnoreCase("!particle")) {
            MultiThreading.schedule(() -> ParticleTest.spawnParticle(player, player.getLocation(), EnumParticle.HEART), 10, 10, TimeUnit.MILLISECONDS);
            event.setCancelled(true);
        }

        if (event.getMessage().equals("!addgame")) {
            getPlugin().getGameManager().addGame("duel", new DuelGame(getPlugin(), GameSizeTemplate.SIZE_1V1.getGameSize().clone()));
            event.setCancelled(true);
        }

        if (event.getMessage().equals("!test")) {
            event.setCancelled(true);
        }

        if (event.getMessage().equals("!addhost")) {
            getPlugin().getGameManager().addGame("duel", new DuelGame(getPlugin(), player, GameSizeTemplate.SIZE_1V1.getGameSize().clone()));
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        getPlugin().getGameManager().leaveGame(player);
        getPlugin().getGameManager().getPartyManager().onLeave(player);
        getPlugin().getDuelManager().onLeave(player);
    }

}
