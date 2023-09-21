package fr.joupi.api.shop;

import fr.joupi.api.ItemBuilder;
import fr.joupi.api.Spigot;
import fr.joupi.api.User;
import fr.joupi.api.gui.GuiButton;
import fr.joupi.api.gui.PageableGui;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class ShopGui extends PageableGui<Spigot, Product> {

    private final User user;

    public ShopGui(Spigot plugin, Player player) {
        super(plugin, "Shop", 6, 45);
        this.user = plugin.getUser(player.getUniqueId());
        getPlugin().getProducts().forEach(this::addProduct);
    }

    @Override
    public void setup() {
        setHorizontalLine(0, 8, new ItemStack(Material.STAINED_GLASS_PANE));
        setItem(0, previousPageButton());
        setItem(8, nextPageButton());
        putStackSizeButton();

        getPage().getElements().forEach(this::updateProduct);
    }

    private void putStackSizeButton() {
        addStackSizeButton(2, 1);
        addStackSizeButton(3, 8);
        addStackSizeButton(4, 16);
        addStackSizeButton(5, 32);
        addStackSizeButton(6, 64);
    }

    private void addStackSizeButton(int slot, int stackSize) {
        ItemBuilder itemBuilder = new ItemBuilder(Material.STORAGE_MINECART).setName("&eChoose stack").setAmount(stackSize);

        if (getUser().getStackSize() == stackSize)
            itemBuilder.setGlowing(true);

        setItem(slot, new GuiButton(itemBuilder.build(), event -> {
            if (getUser().getStackSize() != stackSize) {
                user.setStackSize(stackSize);
                refresh();
            }
        }));
    }

    private void addProduct(Product product) {
        getPagination().addElement(product);
    }

    private void updateProduct(Product product) {
        addItem(getProductButton(product));
    }

    private GuiButton getProductButton(Product product) {
        return new GuiButton(new ItemBuilder(product.getIcon()).setName("&b" + product.getName()).setAmount(getUser().getStackSize()).build());
    }

    @Override
    public GuiButton nextPageButton() {
        return new GuiButton(new ItemBuilder(Material.ARROW).setName("&aSuivant").build(), event -> {
            if (getPagination().hasNext(getPage()))
                updatePage(getPagination().getNext(getPage()));
        });
    }

    @Override
    public GuiButton previousPageButton() {
        return new GuiButton(new ItemBuilder(Material.ARROW).setName("&cRetour").build(), event -> {
            if (getPagination().hasPrevious(getPage()))
                updatePage(getPagination().getPrevious(getPage()));
        });
    }

}