package fr.joupi.api.game.duel;

import fr.joupi.api.ItemBuilder;
import fr.joupi.api.Spigot;
import fr.joupi.api.gui.GGui;
import fr.joupi.api.gui.GuiButton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class DuelRequestGui extends GGui<Spigot> {

    private final Player sender, target;

    private Map selectedMap;
    private Kit selectedKit;
    private KnockBack selectedKnockBack;

    public DuelRequestGui(Spigot plugin, Player sender, Player target) {
        super(plugin, "&eDuel", 5);
        this.sender = sender;
        this.target = target;
        this.selectedMap = Map.DEFAULT;
        this.selectedKit = Kit.DEFAULT;
        this.selectedKnockBack = KnockBack.DEFAULT;
    }

    @Override
    public void setup() {
        setHorizontalLine(9, 17, getFiller());
        setItems(List.of(1, 8), getFiller());

        setItem(0, new GuiButton(new ItemBuilder(Material.BOOK_AND_QUILL).setName("&aEnvoyer").build(), event -> {
            getPlugin().getDuelManager().sendRequest(getSender(), getTarget(), new DuelRequest(getSender().getUniqueId(), getTarget().getUniqueId(), getSelectedMap().getName(), getSelectedKit().getName(), getSelectedKnockBack().getName()));
            close(event.getWhoClicked());
        }));

        setItem(2, new GuiButton(new ItemBuilder(Material.PAPER).setName("Map &e(" + getSelectedMap().getName() + ")").build(),
                event -> Map.getMaps().forEach(map -> setItem(18 + Map.getMaps().indexOf(map), getMapButton(map)))));

        setItem(3, new GuiButton(new ItemBuilder(Material.IRON_SWORD).setName("Kit &e(" + getSelectedKit().getName() + ")").build(),
                event -> Kit.getKits().forEach(kit -> setItem(18 + Kit.getKits().indexOf(kit), getKitButton(kit)))));

        setItem(4, new GuiButton(new ItemBuilder(Material.FISHING_ROD).setName("KnockBacks &e(" + getSelectedKnockBack().getName() + ")").build(),
                event -> KnockBack.getKnockBacks().forEach(knockBack -> setItem(18 + KnockBack.getKnockBacks().indexOf(knockBack), getKnockBackButton(knockBack)))));
    }

    private GuiButton getMapButton(Map map) {
        return new GuiButton(map.getItemStack(), event -> {
            if (!getSelectedMap().equals(map)) {
                event.getWhoClicked().sendMessage("Vous avez sélectionner la map " + map.getName());
                setSelectedMap(map);
                refresh();
            }
        });
    }

    private GuiButton getKitButton(Kit kit) {
        return new GuiButton(kit.getItemStack(), event -> {
            if (!getSelectedKit().equals(kit)) {
                event.getWhoClicked().sendMessage("Vous avez sélectionner la kit " + kit.getName());
                setSelectedKit(kit);
                refresh();
            }
        });
    }

    private GuiButton getKnockBackButton(KnockBack knockBack) {
        return new GuiButton(knockBack.getItemStack(), event -> {
            if (!getSelectedKnockBack().equals(knockBack)) {
                event.getWhoClicked().sendMessage("Vous avez sélectionner le knockback " + knockBack.getName());
                setSelectedKnockBack(knockBack);
                refresh();
            }
        });
    }

    private GuiButton getFiller() {
        return new GuiButton(new ItemBuilder(Material.STAINED_GLASS_PANE).setDyeColor(DyeColor.BLACK).setName("").build());
    }

    @Getter
    @AllArgsConstructor
    enum Map {

        DEFAULT ("Default", new ItemBuilder(Material.MAP).setName("Default").build()),
        DESERT ("Desert", new ItemBuilder(Material.DEAD_BUSH).setName("Desert").build()),
        CHRISTMAS ("Christmas", new ItemBuilder(Material.SNOW_BALL).setName("Noel").build());

        private final String name;
        private final ItemStack itemStack;

        public static List<Map> getMaps() {
            return Arrays.stream(values()).collect(Collectors.toList());
        }

    }

    @Getter
    @AllArgsConstructor
    enum Kit {

        DEFAULT ("Default", new ItemBuilder(Material.IRON_SWORD).setName("Default").build()),
        DIAMOND ("Diamond", new ItemBuilder(Material.DIAMOND_SWORD).setName("Diamond").build()),
        HCF ("HCF", new ItemBuilder(new ItemStack(Material.POTION, 1, (short) 16389)).setGlowing(true).setName("HCF").build());

        private final String name;
        private final ItemStack itemStack;

        public static List<Kit> getKits() {
            return Arrays.stream(values()).collect(Collectors.toList());
        }

    }

    @Getter
    @AllArgsConstructor
    enum KnockBack {

        DEFAULT ("Default", new ItemBuilder(Material.IRON_SWORD).setName("KB par default").build()),
        GOLEM_RUSH ("GolemRush", new ItemBuilder(Material.IRON_BLOCK).setName("KB GolemRush").build()),
        ARENA ("Arène", new ItemBuilder(Material.DIAMOND_SWORD).setName("KB Arène").build());

        private final String name;
        private final ItemStack itemStack;

        public static List<KnockBack> getKnockBacks() {
            return Arrays.stream(values()).collect(Collectors.toList());
        }

    }

}
