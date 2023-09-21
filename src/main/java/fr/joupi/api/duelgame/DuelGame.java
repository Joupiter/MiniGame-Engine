package fr.joupi.api.duelgame;

import fr.joupi.api.Spigot;
import fr.joupi.api.game.Game;
import fr.joupi.api.game.GameSettings;
import fr.joupi.api.game.GameSize;
import org.bukkit.Bukkit;

public class DuelGame extends Game<Spigot> {

    public DuelGame(Spigot plugin, GameSize gameSize) {
        super(plugin, "Duel",
                new GameSettings(gameSize, Bukkit.getWorld("world")));
    }

}
