package fr.joupi.api.listener;

import fr.joupi.api.ItemBuilder;
import fr.joupi.api.Spigot;
import fr.joupi.api.gui.GuiButton;
import fr.joupi.api.gui.PPageableGui;
import org.bukkit.Material;

public class fdg8yfht15 extends PPageableGui<Spigot, GuiButton> {

    public fdg8yfht15(Spigot plugin) {
        super(plugin, "page", 3, 9);

        for (int i = 0; i < 50; i++) {
            getPagination().addElement(new GuiButton(new ItemBuilder(Material.COOKED_BEEF).setAmount(i).build()));
        }
    }

    @Override
    public void setup() {
        getPage().getElements().forEach(this::addItem);

        setItem(21, previousPageButton());
        setItem(23, nextPageButton());
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
