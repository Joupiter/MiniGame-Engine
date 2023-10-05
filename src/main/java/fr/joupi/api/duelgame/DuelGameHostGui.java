package fr.joupi.api.duelgame;

import fr.joupi.api.ItemBuilder;
import fr.joupi.api.Spigot;
import fr.joupi.api.game.host.GameHostState;
import fr.joupi.api.gui.GGui;
import fr.joupi.api.gui.GuiButton;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;

@Getter
public class DuelGameHostGui extends GGui<Spigot> {

    private final DuelGame game;

    public DuelGameHostGui(Spigot plugin, DuelGame game) {
        super(plugin, "&6Host", 4);
        this.game = game;
    }

    @Override
    public void setup() {
        setItem(0, new GuiButton(new ItemBuilder(Material.ENDER_PEARL).setName("&eAccessibilité").addLore(ChatColor.AQUA + getGame().getGameHost().getHostState().name()).build(), event -> {
            getGame().getGameHost().setHostState(Arrays.stream(GameHostState.values()).filter(hostState -> !hostState.equals(getGame().getGameHost().getHostState())).findFirst().orElse(GameHostState.PRIVATE));
            refresh();
        }));

        setItem(1, new GuiButton(new ItemBuilder(Material.IRON_SWORD).setName("&eKit Special").addLore(getGame().getSettings().isUseSpecialKit() ? "&aTrue" : "&cFalse").build(), event -> {
            getGame().getSettings().setUseSpecialKit(!getGame().getSettings().isUseSpecialKit());
            refresh();
        }));
    }

}
