package fr.joupi.api.module;

import fr.joupi.api.ItemBuilder;
import fr.joupi.api.Spigot;
import fr.joupi.api.gui.GuiButton;
import fr.joupi.api.gui.PageableGui;
import org.bukkit.Material;

public class ModuleGui extends PageableGui<Spigot, GuiButton> {

    public ModuleGui(Spigot plugin) {
        super(plugin, "&a&lModules", 3, 9);

        plugin.getModuleManager().getModules().forEach(pair -> getPagination().addElement(getModuleButton(pair.getLeft())));
    }

    @Override
    public void setup() {
        getPage().getElements().forEach(this::addItem);

        setItem(21, previousPageButton());
        setItem(23, nextPageButton());
    }

    private GuiButton getModuleButton(ModuleInfo info) {
        return new GuiButton(new ItemBuilder(Material.PAPER).setName("&6&l" + info.getName()).addLore("&eVersion: &b" + info.getVersion(), "&ePath: &b" + info.getPath(), "&eStatus: " + (info.isEnable() ? "&aEnabled" : "&cDisabled")).build(), event -> {
            if (info.isEnable())
                getPlugin().getModuleManager().disableModule(info);
            else
                getPlugin().getModuleManager().enableModule(info);

            event.getWhoClicked().closeInventory();
        });
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
