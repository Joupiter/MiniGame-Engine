package fr.joupi.api.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

@Getter
@AllArgsConstructor
public enum GameTeamColor {

    RED ("Rouge", ChatColor.RED, DyeColor.RED),
    BLUE ("Bleu", ChatColor.BLUE, DyeColor.BLUE),
    GREEN ("Vert", ChatColor.GREEN, DyeColor.LIME),
    YELLOW ("Jaune", ChatColor.YELLOW, DyeColor.YELLOW);

    private final String name;
    private final ChatColor chatColor;
    private final DyeColor dyeColor;

}
