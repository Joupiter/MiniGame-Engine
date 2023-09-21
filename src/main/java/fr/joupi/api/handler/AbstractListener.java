package fr.joupi.api.handler;

import lombok.Data;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Data
public class AbstractListener<P extends JavaPlugin> implements Listener {

    private final P plugin;

}
