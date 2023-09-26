package fr.joupi.api;

import fr.joupi.api.ffa.FFAGame;
import fr.joupi.api.game.GameManager;
import fr.joupi.api.listener.GameCommand;
import fr.joupi.api.listener.TestListener;
import fr.joupi.api.shop.Product;
import fr.joupi.api.skyly.KillStreak;
import lombok.Getter;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public class Spigot extends JavaPlugin {

    private GameManager gameManager;

    private List<Product> products;
    private List<User> users;
    private KillStreak killStreak;

    @Override
    public void onEnable() {
        this.gameManager = new GameManager(this);
        this.products = new ArrayList<>();
        this.users = new ArrayList<>();
        this.killStreak = new KillStreak(this);

        addProduct();
        getServer().getPluginManager().registerEvents(new TestListener(this), this);
        getCommand("join").setExecutor(new GameCommand(this));
        getCommand("leave").setExecutor(new GameCommand(this));
        getCommand("info").setExecutor(new GameCommand(this));

        getGameManager().addGame("ffa", new FFAGame(this));
    }

    @Override
    public void onDisable() {
        getKillStreak().saveKillStreak();
    }

    public User getUser(UUID uuid) {
        return getUsers().stream().filter(user -> user.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public void addProduct() {
        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            getProducts().add(new Product("Laine " + dyeColor.name(), new ItemBuilder(Material.WOOL).setDyeColor(dyeColor).build()));
            getProducts().add(new Product("Brick " + dyeColor.name(), new ItemBuilder(Material.STAINED_CLAY).setDyeColor(dyeColor).build()));
            getProducts().add(new Product("InkSac " + dyeColor.name(), new ItemBuilder(Material.INK_SACK).setDyeColor(dyeColor).build()));
            getProducts().add(new Product("StainedGlass " + dyeColor.name(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDyeColor(dyeColor).build()));
            getProducts().add(new Product("Glass " + dyeColor.name(), new ItemBuilder(Material.STAINED_GLASS).setDyeColor(dyeColor).build()));
        });
    }

    public void registerListeners(String packageName) {
        new Reflections(packageName).getSubTypesOf(AListener.class)
                .forEach(clazz -> {
                    try {
                        getServer().getPluginManager().registerEvents(clazz.getDeclaredConstructor(this.getClass()).newInstance(this), this);
                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException exception) {
                        exception.printStackTrace();
                    }
                });
    }

}
