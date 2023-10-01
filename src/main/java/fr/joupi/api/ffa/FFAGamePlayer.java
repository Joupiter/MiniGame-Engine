package fr.joupi.api.ffa;

import fr.joupi.api.ItemBuilder;
import fr.joupi.api.game.GamePlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class FFAGamePlayer extends GamePlayer {

    @Setter
    private int kills, killStreak, deaths;

    public FFAGamePlayer(UUID uuid, int kills, int killStreak, int deaths, boolean spectator) {
        super(uuid, spectator);
        this.kills = kills;
        this.killStreak = killStreak;
        this.deaths = deaths;
    }

    public void sendStats() {
        sendMessage("&7&m------------------------",
                "",
                "&eVos stats: &b" + getKills() + " &ekills &c" + getDeaths() + " &emorts",
                "",
                "&7&m------------------------");
    }

    public void giveKit() {
        getPlayer().getInventory().setItem(0, new ItemBuilder(Material.IRON_SWORD).build());
        getPlayer().getInventory().setBoots(new ItemBuilder(Material.IRON_BOOTS).build());
        getPlayer().getInventory().setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).build());
        getPlayer().getInventory().setHelmet(new ItemBuilder(Material.IRON_HELMET).build());
        getPlayer().getInventory().setChestplate(new ItemBuilder(Material.IRON_CHESTPLATE).build());
    }

    public void addKill(int kills) {
        setKills(getKills() + kills);
    }

    public void addKill() {
        addKill(1);
    }

    public void addKillStreak(int killStreak) {
        setKillStreak(getKillStreak() + killStreak);
    }

    public void addKillStreak() {
        addKillStreak(1);
    }

    public void addDeath(int deaths) {
        setDeaths(getDeaths() + deaths);
    }

    public void addDeath() {
        addDeath(1);
    }

}
