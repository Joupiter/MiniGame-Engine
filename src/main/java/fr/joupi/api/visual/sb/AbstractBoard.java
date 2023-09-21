package fr.joupi.api.visual.sb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public abstract class AbstractBoard {

    private final JavaPlugin plugin;

    private String title;

    protected abstract List<String> getLines(Player player);

}
