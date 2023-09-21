package fr.joupi.api.handler;

import fr.joupi.api.ClassUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;

@UtilityClass
public class ListenerHandler {

    public void loadListenersFromPackage(JavaPlugin plugin, String packageName) {
        for (Class<?> clazz : ClassUtil.getClassesInPackage(plugin, packageName)) {
            if (isListener(clazz)) {
                try {
                    Constructor<?> constructor = clazz.getConstructor(JavaPlugin.class);
                    AbstractListener<?> listener = (AbstractListener<?>) constructor.newInstance(plugin);
                    plugin.getServer().getPluginManager().registerEvents(listener, plugin);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isListener(Class<?> clazz) {
        for (Class<?> interfaces : clazz.getInterfaces())
            if (interfaces == Listener.class)
                return true;

        return false;
    }

}
