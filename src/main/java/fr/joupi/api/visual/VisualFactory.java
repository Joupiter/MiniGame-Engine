package fr.joupi.api.visual;

import fr.joupi.api.threading.MultiThreading;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class VisualFactory {

    public void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subTitle) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut);

        connection.sendPacket(packetPlayOutTimes);

        if (subTitle != null) {
            IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + coloredText(subTitle) + "\"}");
            PacketPlayOutTitle packetPlayOutSubTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatBaseComponent);

            connection.sendPacket(packetPlayOutSubTitle);
        }

        if (title != null) {
            IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + coloredText(title) + "\"}");
            PacketPlayOutTitle packetPlayOutSubTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatBaseComponent);

            connection.sendPacket(packetPlayOutSubTitle);
        }
    }

    public void sendActionbar(Player player, String message) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + coloredText(message) + "\"}");
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(chatBaseComponent, (byte) 2);

        craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutChat);
    }

    public void sendFirework(Player player, int power, FireworkEffect fireworkEffect) {
        Firework firework = (Firework) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        fireworkMeta.addEffect(fireworkEffect);
        fireworkMeta.setPower(power);
        firework.setFireworkMeta(fireworkMeta);

        MultiThreading.runAsync(firework::detonate);
    }

    public void sendMessages(CommandSender commandSender, String... messages) {
        Arrays.asList(messages)
                .forEach(message -> commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
    }

    public void sendMessages(Player player, List<String> messages) {
        messages.forEach(message -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
    }

    public void sendMessages(Player player, String... messages) {
        Arrays.asList(messages)
                .forEach(message -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
    }

    public String coloredText(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public DecimalFormat decimalFormat() {
        return new DecimalFormat("#.##");
    }

}