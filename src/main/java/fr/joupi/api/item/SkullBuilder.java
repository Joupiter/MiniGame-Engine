package fr.joupi.api.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.UUID;

@UtilityClass
public class SkullBuilder {

    @Deprecated
    public ItemStack itemFromName(String name) {
        return itemWithName(getPlayerSkullItem(), name);
    }

    @Deprecated
    public ItemStack itemWithName(ItemStack item, String name) {
        notNull(item, "item");
        notNull(name, "name");

        return Bukkit.getUnsafe().modifyItemStack(item, "{SkullOwner:\"" + name + "\"}");
    }

    public ItemStack getHeadFromValue(String value) {
        UUID id = UUID.nameUUIDFromBytes(value.getBytes());
        int less = (int) id.getLeastSignificantBits();
        int most = (int) id.getMostSignificantBits();
        return Bukkit.getUnsafe().modifyItemStack(
                new ItemStack(Material.SKULL_ITEM),
                "{SkullOwner:{Id:[I;" + (less * most) + "," + (less >> 23) + "," + (most / less) + "," + (most * 8731) + "],Properties:{textures:[{Value:\"" + value + "\"}]}}}"
        );
    }

    public ItemStack itemFromUuid(UUID id) {
        return itemWithUuid(getPlayerSkullItem(), id);
    }

    public ItemStack itemWithUuid(ItemStack item, UUID id) {
        notNull(item, "item");
        notNull(id, "id");

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(String.valueOf(Bukkit.getOfflinePlayer(id)));
        item.setItemMeta(meta);

        return item;
    }

    public ItemStack itemFromUrl(String url) {
        return itemWithUrl(getPlayerSkullItem(), url);
    }

    public ItemStack itemWithUrl(ItemStack item, String url) {
        notNull(item, "item");
        notNull(url, "url");

        return itemWithBase64(item, urlToBase64(url));
    }

    public ItemStack itemFromBase64(String base64) {
        return itemWithBase64(getPlayerSkullItem(), base64);
    }

    public ItemStack itemWithBase64(ItemStack item, String base64) {
        notNull(item, "item");
        notNull(base64, "base64");

        UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());
        return Bukkit.getUnsafe().modifyItemStack(item,
                "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}"
        );
    }

    @Deprecated
    public void blockWithName(Block block, String name) {
        notNull(block, "block");
        notNull(name, "name");

        setBlockType(block);
        ((Skull) block.getState()).setOwner(String.valueOf(Bukkit.getOfflinePlayer(name)));
    }

    public void blockWithUuid(Block block, UUID id) {
        notNull(block, "block");
        notNull(id, "id");

        setBlockType(block);
        ((Skull) block.getState()).setOwner(String.valueOf(Bukkit.getOfflinePlayer(id)));
    }

    public void blockWithUrl(Block block, String url) {
        notNull(block, "block");
        notNull(url, "url");

        blockWithBase64(block, urlToBase64(url));
    }

    public void blockWithBase64(Block block, String base64) {
        notNull(block, "block");
        notNull(base64, "base64");

        UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());

        String args = String.format(
                "%d %d %d %s",
                block.getX(),
                block.getY(),
                block.getZ(),
                "{Owner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}"
        );

        if (newerApi())
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "data merge block " + args);
        else
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"blockdata " + args);
    }

    private boolean newerApi() {
        try {
            Material.valueOf("PLAYER_HEAD");
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private ItemStack getPlayerSkullItem() {
        if (newerApi())
            return new ItemStack(Material.valueOf("PLAYER_HEAD"));
        else
            return new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (byte) 3);
    }

    private void setBlockType(Block block) {
        try {
            block.setType(Material.valueOf("PLAYER_HEAD"), false);
        } catch (IllegalArgumentException e) {
            block.setType(Material.valueOf("SKULL"), false);
        }
    }

    private void notNull(Object o, String name) {
        if (o == null) throw new NullPointerException(name + " should not be null!");
    }

    private String urlToBase64(String url) {
        URI actualUrl;

        try {
            actualUrl = new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String toEncode = "{\"textures\":{\"SKIN\":{\"url\":\"" + actualUrl + "\"}}}";
        return Base64.getEncoder().encodeToString(toEncode.getBytes());
    }


    public ItemStack withTexture(String name) {
        try {
            ItemStack itemStack = getPlayerSkullItem();
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);

            profile.getProperties().put("textures", new Property("textures", name));

            Field profileField = skullMeta.getClass().getDeclaredField("profile");

            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);

            itemStack.setItemMeta(skullMeta);

            return itemStack;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ItemStack withSkullOwner(UUID uuid) {
        ItemStack itemStack = getPlayerSkullItem();
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

        skullMeta.setOwner(player.getName());

        itemStack.setItemMeta(skullMeta);

        return itemStack;
    }

    public ItemStack withSkullOwner(String name) {
        ItemStack itemStack = getPlayerSkullItem();
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

        skullMeta.setOwner(player.getName());

        itemStack.setItemMeta(skullMeta);

        return itemStack;
    }

    public ItemStack getLeftArrowSkull() {
        return withTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0=");
    }

    public ItemStack getRightArrowSkull() {
        return withTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjgyYWQxYjljYjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19");
    }

    public ItemStack getPlusSkull() {
        return withTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWEyZDg5MWM2YWU5ZjZiYWEwNDBkNzM2YWI4NGQ0ODM0NGJiNmI3MGQ3ZjFhMjgwZGQxMmNiYWM0ZDc3NyJ9fX0=");
    }

    public ItemStack gejkjdfgdfgdf(Player player) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(player.getUniqueId(), player.getName());
        Field profileField;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        head.setItemMeta(meta);
        return head;
    }

}
