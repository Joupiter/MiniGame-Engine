package fr.joupi.api;

import fr.joupi.api.ffa.FFAGame;
import fr.joupi.api.game.GameManager;
import fr.joupi.api.game.entity.GameEntityManager;
import fr.joupi.api.gui.GuiManager;
import fr.joupi.api.command.GameCommand;
import fr.joupi.api.command.GamePartyCommand;
import fr.joupi.api.listener.TestListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Spigot extends JavaPlugin {

    private GameManager gameManager;

    private GameEntityManager gameEntityManager;
    private GuiManager guiManager;

    @Override
    public void onEnable() {
        this.gameManager = new GameManager(this);
        this.guiManager = new GuiManager(this);
        this.gameEntityManager = new GameEntityManager(this);

        getServer().getPluginManager().registerEvents(new TestListener(this), this);

        getCommand("join").setExecutor(new GameCommand(this));
        getCommand("leave").setExecutor(new GameCommand(this));
        getCommand("info").setExecutor(new GameCommand(this));
        getCommand("end").setExecutor(new GameCommand(this));
        getCommand("party").setExecutor(new GamePartyCommand(this));

        getGameManager().addGame("comboffa", new FFAGame(this));
    }

    @Override
    public void onDisable() {}

}
