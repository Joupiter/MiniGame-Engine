package fr.joupi.api;

import lombok.Data;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Data
public abstract class AListener<P extends JavaPlugin> implements Listener {

    private final P plugin;

}
