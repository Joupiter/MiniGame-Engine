package fr.joupi.api.game.host;

import fr.joupi.api.game.Game;
import fr.joupi.api.gui.GGui;
import fr.joupi.api.gui.GuiManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
public class GameHost<G extends Game<?, ?>> {

    private final G game;

    private final UUID hostUuid;
    @Setter private GameHostState hostState;

    private GGui<?> hostGui;
    private ItemStack hostItem;

    public GameHost(G game, UUID hostUuid) {
        this.game = game;
        this.hostUuid = hostUuid;
        this.hostState = GameHostState.PRIVATE;
    }

    public GameHost<G> setHostGui(GGui<?> gui) {
        this.hostGui = gui;
        return this;
    }

    public GameHost<G> setHostItem(ItemStack itemStack) {
        this.hostItem = itemStack;
        return this;
    }

    public Player getHostPlayer() {
        return Bukkit.getPlayer(getHostUuid());
    }

    public void openGui(GuiManager guiManager, Player player) {
        guiManager.open(player, getHostGui());
    }

    public void giveHostItem(int slot) {
        getHostPlayer().getInventory().setItem(slot, getHostItem());
    }

    public void sendDebugMessage(Player player) {
        player.sendMessage("Hosted by: " + getHostPlayer().getName());
        player.sendMessage("Host State: " + getHostState());
    }

}