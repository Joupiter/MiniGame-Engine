package fr.joupi.api.duelgame;

import fr.joupi.api.ItemBuilder;
import fr.joupi.api.game.Game;
import fr.joupi.api.game.GamePlayer;
import fr.joupi.api.game.GameState;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import fr.joupi.api.game.phase.AbstractGamePhase;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Optional;

public class DuelPhase extends AbstractGamePhase {

    public DuelPhase(Game<?> game) {
        super(game);
    }

    @Override
    protected void startPhase() {
        getGame().setState(GameState.IN_GAME);
        getGame().broadcast("&aBonne chance!");
        getGame().fillTeam();
        getGame().getAlivePlayers().forEach(gamePlayer -> gamePlayer.getPlayer().getInventory().addItem(new ItemBuilder(Material.IRON_SWORD).build()));

        registerEvent(GamePlayerLeaveEvent.class, event -> {
            end();
            event.sendLeaveMessage();
        });

        registerEvent(PlayerDeathEvent.class, event -> {
            Optional<GamePlayer> player = getGame().getPlayer(event.getEntity().getUniqueId());
            Optional<GamePlayer> killer = getGame().getPlayer(event.getEntity().getKiller().getUniqueId());
            Optional<EntityDamageEvent> lastDamage = Optional.ofNullable(event.getEntity().getLastDamageCause()).stream().filter(damage -> damage.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)).findFirst();

            lastDamage.ifPresent(entityDamageEvent -> {
                player.ifPresent(GamePlayer::addDeath);
                player.ifPresent(GamePlayer::setSpectator);
                killer.ifPresent(GamePlayer::addKill);
                end();
            });
        });
    }

    @Override
    protected void endPhase() {

    }

}
