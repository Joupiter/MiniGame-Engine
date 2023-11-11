package fr.joupi.api.particle;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ParticleTest {

    public void spawnParticle(Player player, EnumParticle particleType, double x, double y, double z, float offsetX, float offsetY, float offsetZ, float particleData, int count, Object generalData) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldParticles(particleType, false, (float) x, (float) y, (float) z, offsetX, offsetY, offsetZ, particleData, count, generalData instanceof  int[] ? (int[]) generalData : null));
    }

    public void spawnParticle(Player player, Location location, EnumParticle particleType) {
        spawnParticle(player, particleType, location.getX(), location.getY(), location.getZ(), 0, 0, 0, 1, 0, null);
    }

    public void spawnParticle(Player player, EnumParticle particleType, Location location, int count, float offset) {
        spawnParticle(player, particleType, location.getX(), location.getY(), location.getZ(), offset, offset, offset, count, 0, null);
    }

    public void spawnParticle(Player player, EnumParticle particleType, Location location, int count, float offsetX, float offsetY, float offsetZ) {
        spawnParticle(player, particleType, location.getX(), location.getY(), location.getZ(), offsetX, offsetY, offsetZ, count, 0, null);
    }

}
