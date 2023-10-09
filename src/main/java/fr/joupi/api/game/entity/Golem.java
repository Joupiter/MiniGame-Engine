package fr.joupi.api.game.entity;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.function.Consumer;

public class Golem extends AbstractGameEntity<IronGolem> {

    public Golem(String name, int maxHealth, Location location) {
        super(name, maxHealth, location);
    }

    @Override
    public void spawn() {
        IronGolem ironGolem = (IronGolem) getLocation().getWorld().spawnEntity(getLocation(), EntityType.IRON_GOLEM);
        ironGolem.setCustomName(getName());
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
        return event -> update();
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
        getEntity().setCustomName(ChatColor.translateAlternateColorCodes('&', "&7" + getEntity().getHealth() + " &c<3"));
    }

}
