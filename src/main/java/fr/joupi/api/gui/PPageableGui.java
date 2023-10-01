package fr.joupi.api.gui;

import fr.joupi.api.Pagination;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public abstract class PPageableGui<P extends JavaPlugin, E> extends GGui<P> {

    private final int maxItems;

    private final Pagination<E> pagination;
    @Setter
    private Pagination<E>.Page page;

    protected PPageableGui(P plugin, String inventoryName, int rows, int maxItems) {
        super(plugin, inventoryName, rows);
        this.maxItems = maxItems;
        this.pagination = new Pagination<>(getMaxItems());
        this.page = pagination.getPage(1);
    }

    public abstract GuiButton nextPageButton();

    public abstract GuiButton previousPageButton();

    public void updatePage(Pagination<E>.Page page) {
        setPage(page);
        refresh();
    }

}