package fr.joupi.api.duelgame;

import fr.joupi.api.ItemBuilder;
import fr.joupi.api.Spigot;
import fr.joupi.api.duelgame.phase.CountdownPhase;
import fr.joupi.api.duelgame.phase.DuelPhase;
import fr.joupi.api.duelgame.phase.VictoryPhase;
import fr.joupi.api.duelgame.phase.WaitingPhase;
import fr.joupi.api.game.Game;
import fr.joupi.api.game.GameSize;
import fr.joupi.api.game.GameState;
import fr.joupi.api.game.event.GamePlayerJoinEvent;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import fr.joupi.api.game.gui.TeamGui;
import fr.joupi.api.game.utils.GameHostBuilder;
import fr.joupi.api.game.team.GameTeam;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class DuelGame extends Game<DuelGamePlayer, DuelGameSettings> {

    private final Spigot spigot;

    public DuelGame(Spigot plugin, GameSize gameSize) {
        super(plugin, "Duel", new DuelGameSettings(gameSize, Bukkit.getWorld("world"), false));
        this.spigot = plugin;

        getSettings().addLocation("waiting", new Location(getSettings().getWorld(), -171, 75, 57, -176, 2));
        getSettings().addLocation("red", new Location(getSettings().getWorld(), -179, 67, 74));
        getSettings().addLocation("blue", new Location(getSettings().getWorld(), -189, 67, 74));

        getPhaseManager().addPhases(
                new WaitingPhase(this),
                new CountdownPhase(this),
                new DuelPhase(this),
                new VictoryPhase(this, plugin)
        );

        getPhaseManager().start();
    }

    public DuelGame(Spigot plugin, Player player, GameSize gameSize) {
        this(plugin, gameSize);
        setGameHost(GameHostBuilder.of(this, player.getUniqueId())
                .withHostGui(new DuelGameHostGui(plugin, this))
                .withHostItem(new ItemBuilder(Material.REDSTONE_COMPARATOR).setName("&eParamètres").build())
                .build());
    }

    @Override
    public DuelGamePlayer defaultGamePlayer(UUID uuid, boolean spectator) {
        return new DuelGamePlayer(uuid, 0, 0, 0, spectator);
    }

    /*
        Listeners
     */

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGamePlayerJoin(GamePlayerJoinEvent<DuelGamePlayer> event) {
        ifContainsPlayer(event.getPlayer(), player -> {
            DuelGamePlayer gamePlayer = event.getGamePlayer();

            checkGameState(GameState.WAIT, () -> {
                player.setGameMode(GameMode.ADVENTURE);
                getSettings().getLocation("waiting").ifPresent(player::teleport);
                player.getInventory().setItem(0, new ItemBuilder(Material.CHEST).setName("&eÉquipes").build());

                ifHostedGame(gameHost -> gameHost.getHostUuid().equals(player.getUniqueId()), () -> getGameHost().giveHostItem(8));
            });

            checkGameState(GameState.IN_GAME, () -> {
                gamePlayer.setSpectator(true);
                player.setGameMode(GameMode.SPECTATOR);
                gamePlayer.sendMessage("&aLa partie est déjà commencer !");
            });

            checkGameState(GameState.END, () -> {
                gamePlayer.setSpectator(true);
                player.setGameMode(GameMode.SPECTATOR);
                gamePlayer.sendMessage("&aLa partie est déjà terminée !");
            });

            event.sendJoinMessage();
        });
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ifContainsPlayer(event.getPlayer(), player -> {
            ItemStack itemStack = event.getItem();

            if (itemStack == null) return;

            checkGameState(GameState.WAIT, () -> {
                if (itemStack.getType().equals(Material.CHEST))
                    new TeamGui((Spigot) getPlugin(), this, getPlayer(player.getUniqueId()).orElse(null)).onOpen(player);

                ifHostedGame(gameHost -> itemStack.getType().equals(gameHost.getHostItem().getType()),
                        gameHost -> gameHost.openGui(spigot.getGuiManager(), player));

                event.setCancelled(true);
            });
        });
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        ifContainsPlayer(event.getPlayer(), player -> getPlayer(player.getUniqueId()).ifPresent(gamePlayer -> event.setFormat(coloredMessage(getTeam(gamePlayer).map(GameTeam::getColoredName).orElse("&fAucune") + " &f%1$s &7: &f%2$s"))));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGamePlayerLeave(GamePlayerLeaveEvent<DuelGamePlayer> event) {
        ifContainsPlayer(event.getPlayer(), player -> {
            getPlayers().remove(player.getUniqueId());
            event.sendLeaveMessage();
        });
    }

}