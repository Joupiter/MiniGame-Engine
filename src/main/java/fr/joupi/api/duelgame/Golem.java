package fr.joupi.api.duelgame;

import fr.joupi.api.game.entity.AbstractGameEntity;
import fr.joupi.api.game.team.GameTeam;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.function.Consumer;

@Getter
public class Golem extends AbstractGameEntity<IronGolem> {

    private final GameTeam gameTeam;

    public Golem(GameTeam gameTeam, String name, int maxHealth, Location location) {
        super(name, maxHealth, location);
        this.gameTeam = gameTeam;
    }

    @Override
    public void spawn() {
        IronGolem ironGolem = (IronGolem) getLocation().getWorld().spawnEntity(getLocation(), EntityType.IRON_GOLEM);
        ironGolem.setCustomName(getGameTeam().getColoredName());
        ironGolem.setCustomNameVisible(true);
        ironGolem.setMaxHealth(getMaxHealth());

        removeAI(ironGolem);
        setEntity(ironGolem);
    }

    @Override
    public Consumer<PlayerInteractEntityEvent> interactEvent() {
        return event -> event.getPlayer().sendMessage("INTERACT");
    }

    @Override
    public Consumer<EntityDamageByEntityEvent> damageEvent() {
        return event -> {
            if (getGameTeam().isMember(event.getDamager().getUniqueId()))
                event.setCancelled(true);
            else update();
        };
    }

    @Override
    public Consumer<EntityDeathEvent> deathEvent() {
        return event -> event.getEntity().getKiller().sendMessage("Vous avez tuer le golem");
    }

    @Override
    public void destroy() {
        getEntity().remove();
    }

    @Override
    public void update() {
        getEntity().setCustomName(ChatColor.translateAlternateColorCodes('&',  "&7> " + getGameTeam().getColor().getChatColor() + getEntity().getHealth() + " &c<3"));
    }

}
