package fr.joupi.api.skyly;

import fr.joupi.api.ItemBuilder;
import fr.joupi.api.Spigot;
import fr.joupi.api.gui.GuiButton;
import fr.joupi.api.gui.PageableGui;
import fr.joupi.api.item.SkullBuilder;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlayerGameListGui extends PageableGui<Spigot, GuiButton> {

    private final Player player;

    public PlayerGameListGui(Spigot plugin, Player player) {
        super(plugin, "&8Liste des joueurs", 3, 9);
        this.player = player;

        Bukkit.getOnlinePlayers()
                //.filter(p -> p.hasMetadata("GAME") && p.hasMetadata("GAMETEAM"))
                .forEach(this::addPlayerButton);
    }

    @Override
    public void setup() {
        setHorizontalLine(18, 26, new ItemBuilder(Material.STAINED_GLASS_PANE).setDyeColor(DyeColor.CYAN).build());
        getPage().getElements().forEach(this::addItem);

        if (getPlayerButton(player).getItemStack().equals(new ItemBuilder(SkullBuilder.gejkjdfgdfgdf(player)).setName(player.getName()).addLore("§8• §7Clic pour te teleporter à lui").build()))
            System.out.println("yes");

        setItem(3, new GuiButton(SkullBuilder.getPlusSkull(), event -> event.getWhoClicked().sendMessage("cc")));

        setItem(21, previousPageButton());
        setItem(23, nextPageButton());
    }

    private void addPlayerButton(Player player) {
        getPagination().addElement(getPlayerButton(player));
    }

    private GuiButton getPlayerButton(Player player) {
        return new GuiButton(new ItemBuilder(SkullBuilder.gejkjdfgdfgdf(player)).setName(player.getName()).addLore("§8• §7Clic pour te teleporter à lui").build(), event -> {
            event.getWhoClicked().teleport(player);
            event.getWhoClicked().sendMessage("TELEPORT TO " + player.getName());
        });
    }

    @Override
    public GuiButton nextPageButton() {
        return new GuiButton(new ItemBuilder(SkullBuilder.getRightArrowSkull()).setName("&aSuivant").build(), event -> {
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
