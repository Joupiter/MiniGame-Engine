package fr.joupi.api.skyly;

import fr.joupi.api.ItemBuilder;
import fr.joupi.api.Spigot;
import fr.joupi.api.gui.GuiButton;
import fr.joupi.api.gui.PageableGui;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class GamesPending extends PageableGui<Spigot, GuiButton> {

    public GamesPending(Spigot plugin) {
        super(plugin, "&eListe des Joueurs", 3, 9);

        Bukkit.getOnlinePlayers()
                .forEach(this::addPendingGameButton);
    }

    @Override
    public void setup() {
        setHorizontalLine(18, 27, new ItemBuilder(Material.STAINED_GLASS_PANE).setDyeColor(DyeColor.CYAN).build());

        getPage().getElements().forEach(this::addItem);

        setItem(21, previousPageButton());
        setItem(23, nextPageButton());
    }

    private void addPendingGameButton(Player player) {
        getPagination().addElement(getPendingGameButton(player));
    }

    private GuiButton getPendingGameButton(Player player) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        skullMeta.setOwner(player.getName());
        skullMeta.setDisplayName("§f§l" + player.getName());
        List<String> lore = new ArrayList<>();
        lore.add("§8• §7Monde §f: §b" + player.getWorld().getName());

        if (player.hasMetadata("GAME")) {
            lore.add("§8• §7Jeu §f: §b" + player.getMetadata("GAME").get(0).asString());
            if (player.hasMetadata("GAMEID"))
                lore.add("§8• §7ID §f: §b" + player.getMetadata("GAMEID").get(0).asString());
            else
                lore.add("§8• §7Jeu §f: §bAucun");
        }

        skullMeta.setLore(lore);
        item.setItemMeta(skullMeta);
        return new GuiButton(item, event -> event.getWhoClicked().teleport(player));
    }

    @Override
    public GuiButton nextPageButton() {
        return new GuiButton(new ItemBuilder(Material.ARROW).setName("&aSuivant").build(), event -> {
            if (getPagination().hasNext(getPage()))
                updatePage(getPagination().getNext(getPage()));
        });
    }

    @Override
    public GuiButton previousPageButton() {
        return new GuiButton(new ItemBuilder(Material.ARROW).setName("&cRetour").build(), event -> {
            if (getPagination().hasPrevious(getPage()))
                updatePage(getPagination().getPrevious(getPage()));
        });
    }

}