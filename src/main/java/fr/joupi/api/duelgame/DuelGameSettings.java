package fr.joupi.api.duelgame;

import com.google.gson.annotations.Expose;
import fr.joupi.api.ItemBuilder;
import fr.joupi.api.game.GameSettings;
import fr.joupi.api.game.GameSize;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

@Getter
@Setter
public class DuelGameSettings extends GameSettings {

    @Expose private boolean useSpecialKit;

    public DuelGameSettings(GameSize gameSize, World world, boolean useSpecialKit) {
        super(gameSize, world);
        this.useSpecialKit = useSpecialKit;
    }

    public void giveSpecialKit(Player player) {
        player.getInventory().setItem(0, new ItemBuilder(Material.DIAMOND_SWORD).build());
        player.getInventory().setBoots(new ItemBuilder(Material.IRON_BOOTS).build());
        player.getInventory().setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).build());
        player.getInventory().setHelmet(new ItemBuilder(Material.IRON_HELMET).build());
        player.getInventory().setChestplate(new ItemBuilder(Material.IRON_CHESTPLATE).build());
    }

}
