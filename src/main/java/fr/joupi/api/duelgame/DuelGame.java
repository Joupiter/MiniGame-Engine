package fr.joupi.api.duelgame;

import fr.joupi.api.ItemBuilder;
import fr.joupi.api.Spigot;
import fr.joupi.api.duelgame.phase.CountdownPhase;
import fr.joupi.api.duelgame.phase.DuelPhase;
import fr.joupi.api.duelgame.phase.VictoryPhase;
import fr.joupi.api.duelgame.phase.WaitingPhase;
import fr.joupi.api.game.*;
import fr.joupi.api.game.event.GamePlayerJoinEvent;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import fr.joupi.api.game.gui.TeamGui;
import fr.joupi.api.game.host.GameHost;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Optional;
import java.util.UUID;

public class DuelGame extends Game<DuelGamePlayer, DuelGameSettings> {

    public DuelGame(Spigot plugin, GameSize gameSize) {
        super(plugin, "Duel", new DuelGameSettings(gameSize, Bukkit.getWorld("world"), false));

        getSettings().addLocation("waiting", new Location(getSettings().getWorld(), -171, 75, 57, -176, 2));
        getSettings().addLocation("red", new Location(getSettings().getWorld(), -179, 67, 74));
        getSettings().addLocation("blue", new Location(getSettings().getWorld(), -189, 67, 74));

        /*
            Les phases doivent être dans l'ordre !
         */
        getPhaseManager().addPhase(
                new WaitingPhase(this),
                new CountdownPhase(this),
                new DuelPhase(this),
                new VictoryPhase(this, plugin)
        );

        getPhaseManager().start();
    }

    public DuelGame(Spigot plugin, Player player, GameSize gameSize) {
        this(plugin, gameSize);
        setGameHost(new GameHost<>(this, player.getUniqueId())
                .setHostGui(new DuelGameHostGui(plugin, this))
                .setHostItem(new ItemBuilder(Material.REDSTONE_COMPARATOR).setName("&eParamètres").build()));
    }

    @Override
    public DuelGamePlayer defaultGamePlayer(UUID uuid) {
        return new DuelGamePlayer(uuid, 0, 0, 0, !getState().equals(GameState.WAIT));
    }

    /*
        Listeners
     */

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGamePlayerJoin(GamePlayerJoinEvent<DuelGamePlayer> event) {
        if (containsPlayer(event.getPlayer().getUniqueId())) {
            Player player = event.getPlayer();
            DuelGamePlayer gamePlayer = event.getGamePlayer();

            checkGameState(GameState.WAIT, () -> {
                player.setGameMode(GameMode.ADVENTURE);
                player.teleport(getSettings().getLocation("waiting"));
                player.getInventory().setItem(0, new ItemBuilder(Material.CHEST).setName("&eÉquipes").build());

                ifHostedGame(() -> getGameHost().giveHostItem(8));
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
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() == null) return;

        if (containsPlayer(player.getUniqueId())) {
            DuelGamePlayer gamePlayer = getPlayer(player.getUniqueId()).orElse(null);

                checkGameState(GameState.WAIT, () -> {
                    if (event.getItem().getType().equals(Material.CHEST))
                        new TeamGui((Spigot) getPlugin(), this, gamePlayer).onOpen(player);

                    ifHostedGame(event.getItem().getType().equals(getGameHost().getHostItem().getType()),
                            () -> getGameHost().getHostGui().onOpen(player));
                });
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (containsPlayer(event.getPlayer().getUniqueId()))
            getPlayer(event.getPlayer().getUniqueId())
                    .ifPresent(gamePlayer -> event.setFormat(ChatColor.translateAlternateColorCodes('&', getTeam(gamePlayer).map(GameTeam::getColoredName).orElse("&fAucune") + " &f%1$s &7: &f%2$s")));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGamePlayerLeave(GamePlayerLeaveEvent<DuelGamePlayer> event) {
        if (containsPlayer(event.getPlayer().getUniqueId())) {
            getPlayers().remove(event.getPlayer().getUniqueId());
            event.sendLeaveMessage();
        }
    }

}
