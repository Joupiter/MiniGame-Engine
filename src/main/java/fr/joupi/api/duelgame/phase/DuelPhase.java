package fr.joupi.api.duelgame.phase;

import fr.joupi.api.ItemBuilder;
import fr.joupi.api.game.Game;
import fr.joupi.api.game.GamePlayer;
import fr.joupi.api.game.GameState;
import fr.joupi.api.game.GameTeam;
import fr.joupi.api.game.event.GamePlayerJoinEvent;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import fr.joupi.api.game.phase.AbstractGamePhase;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class DuelPhase extends AbstractGamePhase {

    public DuelPhase(Game<?> game) {
        super(game);
    }

    @Override
    public void onStart() {
        getGame().setState(GameState.IN_GAME);
        getGame().fillTeam();
        getGame().getAlivePlayers().forEach(gamePlayer -> gamePlayer.getPlayer().getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).build()));

        registerEvent(GamePlayerJoinEvent.class, event -> {
            getGame().checkGameState(GameState.IN_GAME, () -> {
                event.getPlayer().setGameMode(GameMode.SPECTATOR);
                event.getGamePlayer().sendMessage("&aLa partie est déjà commencer !");
            });

            event.sendJoinMessage();
        });

        registerEvent(GamePlayerLeaveEvent.class, event -> {
            endPhase();
            event.sendLeaveMessage();
        });

        registerEvent(AsyncPlayerChatEvent.class, event -> {
            getGame().getPlayer(event.getPlayer().getUniqueId())
                    .ifPresent(gamePlayer -> event.setFormat(ChatColor.translateAlternateColorCodes('&', getGame().getTeam(gamePlayer).map(GameTeam::getColoredName).orElse("&fAucune") + " &f%1$s &7: &f%2$s")));
        });

        registerEvent(PlayerDeathEvent.class, event -> {
            Player player = event.getEntity();
            Player killer = event.getEntity().getKiller();

            if (event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                getGame().getPlayer(player.getUniqueId()).ifPresent(gamePlayer -> {
                    gamePlayer.addDeath();
                    gamePlayer.setSpectator(true);
                    gamePlayer.getPlayer().setGameMode(GameMode.SPECTATOR);
                });

                getGame().getPlayer(killer.getUniqueId()).ifPresent(GamePlayer::addKill);

                getGame().broadcast("&a" + player.getName() + " &ea ete tue par &c" + killer.getName() + " &e!");
                endPhase();
            }
        });
    }

    @Override
    public void onEnd() {

    }

    /*
    registerEvent(PlayerDeathEvent.class, event -> {
            Optional<GamePlayer> player = getGame().getPlayer(event.getEntity().getUniqueId());
            Optional<GamePlayer> killer = getGame().getPlayer(event.getEntity().getKiller().getUniqueId());
            Optional<EntityDamageEvent> lastDamage = Optional.ofNullable(event.getEntity().getLastDamageCause()).stream().filter(damage -> damage.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)).findFirst();

            lastDamage.ifPresent(entityDamageEvent -> {
                player.ifPresent(GamePlayer::addDeath);
                player.ifPresent(gamePlayer -> {
                    gamePlayer.setSpectator(true);
                    gamePlayer.getPlayer().setGameMode(GameMode.SPECTATOR);
                });
                killer.ifPresent(GamePlayer::addKill);
                endPhase();
            });
        });
     */

}
