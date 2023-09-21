package fr.joupi.api.skyly;

import fr.joupi.api.ItemBuilder;
import fr.joupi.api.Spigot;
import fr.joupi.api.gui.GuiButton;
import fr.joupi.api.gui.PageableGui;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class PlayerGameListGui extends PageableGui<Spigot, GuiButton> {

    public PlayerGameListGui(Spigot plugin, Player player) {
        super(plugin, "&8Liste des joueurs", 3, 9);

        Bukkit.getWorld(player.getWorld().getUID()).getPlayers().stream()
                .filter(p -> p.hasMetadata("GAME") && p.hasMetadata("GAMETEAM"))
                .forEach(this::addPlayerButton);
    }

    @Override
    public void setup() {
        setHorizontalLine(18, 27, new ItemBuilder(Material.STAINED_GLASS_PANE).setDyeColor(DyeColor.CYAN).build());
        getPage().getElements().forEach(this::addItem);

        setItem(21, previousPageButton());
        setItem(23, nextPageButton());
    }

    private String getTextTeam(String team) {
        if (team.equals("blue"))
            return "§9Bleu ";
        else if (team.equals("red"))
            return "§cRouge ";
        else
            return "§8Team Inconnue";
    }

    private void addPlayerButton(Player player) {
        getPagination().addElement(getPlayerButton(player));
    }

    private GuiButton getPlayerButton(Player player) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        skullMeta.setOwner(player.getName());
        //GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        skullMeta.setDisplayName(getTextTeam(player.getMetadata("GAMETEAM").get(0).asString()) + player.getName());
        List<String> lore = new ArrayList<>();
        lore.add("§8• §7Clic pour te teleporter à lui");
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
