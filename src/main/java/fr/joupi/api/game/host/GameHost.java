package fr.joupi.api.game.host;

import fr.joupi.api.game.Game;
import fr.joupi.api.gui.GGui;
import fr.joupi.api.gui.GuiManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class GameHost<G extends Game<?, ?, ?>> {

    private final G game;

    private UUID hostUuid;
    private GameHostState hostState;

    private GGui<?> hostGui;
    private ItemStack hostItem;

    private final List<UUID> coHost;

    public GameHost(G game, UUID hostUuid) {
        this.game = game;
        this.hostUuid = hostUuid;
        this.hostState = GameHostState.PRIVATE;
        this.coHost = new ArrayList<>();
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

    public void addCoHost(UUID uuid) {
        getCoHost().add(uuid);
    }

    public boolean isCoHost(UUID uuid) {
        return getCoHost().contains(uuid);
    }

    public void removeCoHost(UUID uuid) {
        getCoHost().remove(uuid);
    }

    public void sendDebugMessage(Player player) {
        player.sendMessage(String.format("Hosted by: %s", getHostPlayer().getName()));
        player.sendMessage(String.format("Host State: %s", getHostState()));
    }

}

