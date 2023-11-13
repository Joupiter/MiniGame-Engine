package fr.joupi.api.game.entity;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class GameEntityManager implements Listener {

    private final Map<String, AbstractGameEntity<?>> entities;

    public GameEntityManager(JavaPlugin plugin) {
        this.entities = new ConcurrentHashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void spawn(AbstractGameEntity<?> entity) {
        entity.spawn();
        getEntities().putIfAbsent(entity.getName(), entity);
    }

    public void destroy(String entityName) {
        Optional.ofNullable(getEntities().get(entityName)).ifPresent(GameEntity::destroy);
        getEntities().remove(entityName);
    }

    public void destroy(String... entitiesName) {
        Arrays.asList(entitiesName)
                .forEach(this::destroy);
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        getEntities().values().stream()
                .filter(entity -> entity.getEntity().equals(event.getEntity()))
                .forEach(entity -> entity.spawnEvent().accept(event));
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        getEntities().values().stream()
                .filter(entity -> entity.getEntity().equals(event.getRightClicked()))
                .forEach(entity -> entity.interactEvent().accept(event));
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        getEntities().values().stream()
                .filter(entity -> event.getDamager() instanceof Player)
                .filter(entity -> entity.getEntity().equals(event.getEntity()))
                .forEach(entity -> entity.damageEvent().accept(event));
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        getEntities().values().stream()
                .filter(entity -> entity.getEntity().equals(event.getEntity()))
                .forEach(entity -> {
                    entity.deathEvent().accept(event);
                    entity.destroy();
                    getEntities().remove(entity.getName());
                });
    }

}
