package fr.joupi.api.listener;

import fr.joupi.api.ItemBuilder;
import fr.joupi.api.Spigot;
import fr.joupi.api.gui.GGui;
import fr.joupi.api.gui.GuiButton;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TestGui extends GGui<Spigot> {

    public TestGui(Spigot plugin) {
        super(plugin, "Inventory", 3);
        setCloseInventory(event -> plugin.getGuiManager().getGuis().remove(event.getPlayer().getUniqueId()));
    }

    @Override
    public void setup() {
        setItems(getBorders(), new GuiButton(new ItemBuilder(Material.STAINED_GLASS_PANE).setDyeColor(DyeColor.YELLOW).build()));

        setItem(0, new GuiButton(new ItemBuilder(Material.APPLE).setName("Autre Menu").build(),
                event -> getPlugin().getGuiManager().open((Player) event.getWhoClicked(), new FDJNBFDSB(getPlugin()))));

        setItem(2, new GuiButton(new ItemBuilder(Material.CHEST).setAmount(getPlugin().getGameManager().getSize()).build(),
                event -> event.getWhoClicked().sendMessage("GAME COUCOU")));

        setItem(3, new GuiButton(new ItemBuilder(Material.EMERALD).setAmount(0).setName("User: " + getPlugin().getUsers().get(0).getStackSize()).build(),
                event -> event.getWhoClicked().sendMessage("USER COUCOU")));
    }

    /*@Override
    public void onUpdate() {
        setItem(2, new GuiButton(new ItemBuilder(Material.CHEST).setAmount(getPlugin().getGameManager().getSize()).build(),
                event -> event.getWhoClicked().sendMessage("GAME COUCOU")));

        setItem(3, new GuiButton(new ItemBuilder(Material.EMERALD).setAmount(0).setName("User: " + getPlugin().getUsers().get(0).getStackSize()).build(),
                event -> event.getWhoClicked().sendMessage("USER COUCOU")));
    }*/

}
