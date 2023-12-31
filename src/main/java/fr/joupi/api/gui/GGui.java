package fr.joupi.api.gui;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@Getter
@Setter
public abstract class GGui<P extends JavaPlugin> {

    private final P plugin;

    private Inventory inventory;
    private final String inventoryName;
    private final int rows;
    private Consumer<InventoryCloseEvent> closeConsumer;

    private final ConcurrentMap<Integer, GuiButton> buttons;

    public GGui(P plugin, String inventoryName, int rows) {
        this.plugin = plugin;
        this.inventoryName = inventoryName;
        this.rows = rows;
        this.buttons = new ConcurrentHashMap<>();
        this.inventory = Bukkit.createInventory(null, rows * 9, ChatColor.translateAlternateColorCodes('&', inventoryName));
        defaultLoad();
    }

    public abstract void setup();

    public void onUpdate() {}

    public void setCloseInventory(Consumer<InventoryCloseEvent> closeConsumer) {
        this.closeConsumer = closeConsumer;
    }

    public void onOpen(Player player) {
        setup();
        open(player);
    }

    private void open(Player player) {
        player.openInventory(getInventory());
    }

    public void close(HumanEntity player) {
        player.closeInventory();
    }

    public void setItem(int slot, GuiButton button) {
        getButtons().put(slot, button);
        update();
    }

    public void setItems(int[] slots, GuiButton button) {
        Arrays.stream(slots).forEach(slot -> setItem(slot, button));
    }

    public void setItems(int[] slots, ItemStack itemStack) {
        setItems(slots, new GuiButton(itemStack));
    }

    public void setItems(List<Integer> slots, GuiButton button) {
        slots.forEach(slot -> setItem(slot, button));
    }

    public void setItems(List<Integer> slots, ItemStack itemStack) {
        setItems(slots, new GuiButton(itemStack));
    }

    public void setHorizontalLine(int from, int to, GuiButton button) {
        IntStream.rangeClosed(from, to)
                .forEach(slot -> setItem(slot, button));
    }

    public void setHorizontalLine(int from, int to, ItemStack item) {
        setHorizontalLine(from, to, new GuiButton(item));
    }

    public void setVerticalLine(int from, int to, GuiButton button) {
        for (int slot = from; slot <= to; slot += 9)
            setItem(slot, button);
    }

    public void setVerticalLine(int from, int to, ItemStack item) {
        setVerticalLine(from, to, new GuiButton(item));
    }

    public void addItem(GuiButton item) {
        setItem(getInventory().firstEmpty(), item);
        update();
    }

    public void fillAllInventory(GuiButton button) {
        IntStream.range(0, getSize())
                .filter(i -> getItem(i) != null)
                .forEach(i -> setItem(i, button));
    }

    public int[] getBorders() {
        return IntStream.range(0, getSize())
                .filter(i -> getSize() < 27 || i < 9 || i % 9 == 0 || (i - 8) % 9 == 0 || i > getSize() - 9)
                .parallel()
                .toArray();
    }

    public void removeItem(int slot) {
        getButtons().remove(slot);
        getInventory().remove(getInventory().getItem(slot));
    }

    public void clear() {
        getButtons().keySet().forEach(this::removeItem);
        update();
    }

    private void defaultLoad() {
        update();
    }

    private void update() {
        registerItems();
    }

    public void refresh() {
        clear();
        setup();
    }

    private void registerItems() {
        getButtons().forEach((slot, item) -> getInventory().setItem(slot, item.getItemStack()));
    }

    public GuiButton getItem(int slot) {
        return getButtons().get(slot);
    }

    public int getSize() {
        return rows * 9;
    }

}