package fr.joupi.api.game.entity;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.function.Consumer;

public interface GameEntity<T extends Entity> {

    void spawn();

    void destroy();

    default Consumer<EntitySpawnEvent> spawnEvent() {
        return event -> {};
    }

    default Consumer<PlayerInteractEntityEvent> interactEvent() {
        return event -> {};
    }

    default Consumer<EntityDamageByEntityEvent> damageEvent() {
        return event -> {};
    }

    default Consumer<EntityDeathEvent> deathEvent() {
        return event -> {};
    }

    default void update() {}

    default String colored(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
