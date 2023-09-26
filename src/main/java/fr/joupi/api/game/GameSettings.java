package fr.joupi.api.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;

@Getter
@Setter
@AllArgsConstructor
public class GameSettings {

    private GameSize gameSize;
    private World world;

}
