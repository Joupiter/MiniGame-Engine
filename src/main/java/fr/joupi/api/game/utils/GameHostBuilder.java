package fr.joupi.api.game.utils;

import fr.joupi.api.game.Game;
import fr.joupi.api.game.host.GameHost;
import fr.joupi.api.gui.GGui;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class GameHostBuilder {

    private final GameHost<?> gameHost;

    private GameHostBuilder(Game<?, ?, ?> game, UUID hostUuid) {
        this.gameHost = new GameHost<>(game, hostUuid);
    }

    public static GameHostBuilder of(Game<?, ?, ?> game, UUID hostUuid) {
        return new GameHostBuilder(game, hostUuid);
    }

    public GameHostBuilder withHostGui(GGui<?> hostGui) {
        gameHost.setHostGui(hostGui);
        return this;
    }

    public GameHostBuilder withHostItem(ItemStack hostItem) {
        gameHost.setHostItem(hostItem);
        return this;
    }

    public GameHost<?> build() {
        return gameHost;
    }

}