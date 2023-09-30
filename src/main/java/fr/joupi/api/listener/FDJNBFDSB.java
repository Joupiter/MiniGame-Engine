package fr.joupi.api.listener;

import fr.joupi.api.ItemBuilder;
import fr.joupi.api.Spigot;
import fr.joupi.api.gui.GGui;
import fr.joupi.api.gui.GuiButton;
import org.bukkit.Material;

public class FDJNBFDSB extends GGui<Spigot> {

    public FDJNBFDSB(Spigot plugin) {
        super(plugin, "Inventory2", 3);
        setCloseInventory(event -> plugin.getGuiManager().getGuis().remove(event.getPlayer().getUniqueId()));
    }

    @Override
    public void setup() {
        setItem(2, new GuiButton(new ItemBuilder(Material.APPLE).setName("Game: " + getPlugin().getGameManager().getSize()).build(),
                event -> System.out.println("Test Gui : 1")));

    }

}
