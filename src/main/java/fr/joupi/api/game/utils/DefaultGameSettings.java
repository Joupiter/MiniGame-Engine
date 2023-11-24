package fr.joupi.api.game.utils;

import fr.joupi.api.game.GameSettings;
import fr.joupi.api.game.GameSize;
import org.bukkit.World;

public class DefaultGameSettings extends GameSettings {

    public DefaultGameSettings(GameSize gameSize, World world) {
        super(gameSize, world);
    }

}
