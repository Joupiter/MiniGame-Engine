package fr.joupi.api.item;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

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

}
