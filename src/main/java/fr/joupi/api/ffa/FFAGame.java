package fr.joupi.api.ffa;

import fr.joupi.api.game.Game;
import fr.joupi.api.game.GameSettings;
import fr.joupi.api.game.GameSize;
import fr.joupi.api.game.GameState;
import fr.joupi.api.game.event.GamePlayerJoinEvent;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import fr.joupi.api.game.team.GameTeamColor;
import fr.joupi.api.game.utils.DefaultGameSettings;
import fr.joupi.api.game.utils.DefaultGameTeam;
import fr.joupi.api.game.utils.GameSizeTemplate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class FFAGame extends Game<FFAGamePlayer, DefaultGameTeam, GameSettings> {

    public FFAGame(JavaPlugin plugin, GameSize gameSize) {
        super(plugin, "ComboFFA", new DefaultGameSettings(gameSize, Bukkit.getWorld("world")));
        setState(GameState.IN_GAME);

        getSettings().addLocations("lobby", new Location(getSettings().getWorld(), -171, 75, 57, -176, 2));
        getSettings().addLocations("random", new Location(getSettings().getWorld(), -179, 67, 74), new Location(getSettings().getWorld(), -189, 67, 74));
    }

    public FFAGame(JavaPlugin plugin) {
        this(plugin, GameSizeTemplate.FFA.getGameSize().clone());
    }

    @Override
    public FFAGamePlayer defaultGamePlayer(UUID uuid, boolean spectator) {
        return new FFAGamePlayer(uuid, 0, 0, 0, spectator);
    }

    @Override
    public DefaultGameTeam defaultGameTeam(GameTeamColor color) {
        return new DefaultGameTeam(color);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGamePlayerJoin(GamePlayerJoinEvent<FFAGamePlayer> event) {
        ifContainsPlayer(event.getPlayer(), player -> {
            FFAGamePlayer gamePlayer = event.getGamePlayer();

            gamePlayer.setupPlayer();
            gamePlayer.sendStats();
            getSettings().getLocation("lobby").ifPresent(player::teleport);

            event.sendJoinMessage();
        });
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        ifContainsPlayer(event.getPlayer(), player -> event.setFormat(coloredMessage("&7[&b1&7] &7%1$s &7: &f%2$s")));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ifContainsPlayer(event.getPlayer(), player -> {
            if (event.getItem() == null) return;
            if (!event.getItem().getType().equals(Material.GOLD_AXE)) return;

            getSettings().getRandomLocation("random").ifPresent(player::teleport);
            getPlayer(player.getUniqueId()).ifPresent(FFAGamePlayer::giveKit);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGamePlayerLeave(GamePlayerLeaveEvent<FFAGamePlayer> event) {
        ifContainsPlayer(event.getPlayer(), player -> event.sendLeaveMessage());
    }

}
