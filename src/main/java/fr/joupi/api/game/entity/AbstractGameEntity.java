package fr.joupi.api.game.entity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

@Getter
@Setter
public abstract class AbstractGameEntity<E extends Entity> implements GameEntity<E> {

    private E entity;

    private final String name;
    private final int maxHealth;
    private final Location location;

    public AbstractGameEntity(String name, int maxHealth, Location location) {
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        this.maxHealth = maxHealth;
        this.location = location;
    }

    public void removeAI(Entity entity) {
        net.minecraft.server.v1_8_R3.Entity nmsEnt = ((CraftEntity) entity).getHandle();
        NBTTagCompound tag = nmsEnt.getNBTTag() != null ? nmsEnt.getNBTTag() : new NBTTagCompound();

        nmsEnt.c(tag);
        tag.setInt("NoAI", 1);
        nmsEnt.f(tag);
    }

}
