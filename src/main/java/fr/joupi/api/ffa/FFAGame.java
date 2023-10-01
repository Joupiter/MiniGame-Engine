package fr.joupi.api.ffa;

import fr.joupi.api.ItemBuilder;
import fr.joupi.api.game.Game;
import fr.joupi.api.game.GameSettings;
import fr.joupi.api.game.GameSize;
import fr.joupi.api.game.GameState;
import fr.joupi.api.game.event.GamePlayerJoinEvent;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.UUID;

public class FFAGame extends Game<FFAGamePlayer, GameSettings> {

    public FFAGame(JavaPlugin plugin) {
        super(plugin, "FFA", new GameSettings(new GameSize("ffa", 0, 999, 0, 0), Bukkit.getWorld("world")));
        setState(GameState.IN_GAME);

        getSettings().addLocations("lobby", new Location(getSettings().getWorld(), -171, 75, 57, -176, 2));
        getSettings().addLocations("random", new Location(getSettings().getWorld(), -179, 67, 74), new Location(getSettings().getWorld(), -189, 67, 74));
    }

    @Override
    public FFAGamePlayer defaultGamePlayer(UUID uuid) {
        /*
            Requete SQL ici pour recup stats du joueur
         */
        return new FFAGamePlayer(uuid, 0, 0, 0, false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGamePlayerJoin(GamePlayerJoinEvent<FFAGamePlayer> event) {
        if (containsPlayer(event.getPlayer().getUniqueId())) {
            Player player = event.getPlayer();
            FFAGamePlayer gamePlayer = event.getGamePlayer();

            checkGameState(GameState.IN_GAME, () -> {
                event.sendJoinMessage();
                player.setGameMode(GameMode.ADVENTURE);
                player.getInventory().clear();
                player.setHealth(player.getMaxHealth());
                player.teleport(getSettings().getLocation("lobby"));
                player.getInventory().setItem(0, new ItemBuilder(Material.GOLD_AXE).setName("&bJouer").build());
                gamePlayer.sendStats();
            });
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (containsPlayer(event.getPlayer().getUniqueId()))
            event.setFormat(ChatColor.translateAlternateColorCodes('&', "&7[&b1&7] &7%1$s &7: &f%2$s"));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() == null) return;
        if (!event.getItem().getType().equals(Material.GOLD_AXE)) return;

        if (containsPlayer(player.getUniqueId())) {
            Optional<FFAGamePlayer> gamePlayer = getPlayer(player.getUniqueId());

            getSettings().getRandomLocation("random").ifPresent(player::teleport);
            gamePlayer.ifPresent(FFAGamePlayer::giveKit);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGamePlayerLeave(GamePlayerLeaveEvent<FFAGamePlayer> event) {
        if (containsPlayer(event.getPlayer().getUniqueId())) {
            getPlayers().remove(event.getPlayer().getUniqueId());
            event.sendLeaveMessage();
        }
    }

}
