package fr.joupi.api.game.gui;

import fr.joupi.api.ItemBuilder;
import fr.joupi.api.Spigot;
import fr.joupi.api.game.Game;
import fr.joupi.api.game.GamePlayer;
import fr.joupi.api.game.team.GameTeam;
import fr.joupi.api.gui.Gui;
import fr.joupi.api.gui.GuiButton;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class TeamGui extends Gui<Spigot> {

    private final Game<?, ?, ?> game;
    private final GamePlayer gamePlayer;

    public TeamGui(Spigot plugin, Game<?, ?, ?> game, GamePlayer gamePlayer) {
        super(plugin, "Équipes", 1);
        this.game = game;
        this.gamePlayer = gamePlayer;
    }

    @Override
    public void setup() {
        getGame().getTeams().forEach(this::addTeamItem);

        /*
        https://minecraft-heads.com/custom-heads/decoration/216-dice-black
        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTE1ZjdjMzEzYmNhOWMyZjk1OGU2OGFiMTRhYjM5Mzg2N2Q2NzUwM2FmZmZmOGYyMGNiMTNmYmU5MTdmZDMxIn19fQ=="
         */
        setItem(8, new GuiButton(new ItemBuilder(Material.STORAGE_MINECART).setName("&7Aléatoire").build(), event ->
                getGame().getRandomTeam().ifPresent(gameTeam -> {
                    getGame().addPlayerToTeam(getGamePlayer(), gameTeam);
                    updateGui();
                })));
    }

    private void updateGui() {
        getGame().getPlayers().values().stream()
                .filter(player -> player.getPlayer().getOpenInventory() != null && player.getPlayer().getOpenInventory().getTitle().equals(getInventoryName()))
                .forEach(player -> new TeamGui(getPlugin(), getGame(), player).onOpen(player.getPlayer()));
    }

    private void addTeamItem(GameTeam gameTeam) {
        addItem(new GuiButton(getTeamItem(gameTeam), event -> {
            if (getGame().getSettings().getGameSize().getTeamMaxPlayer() != gameTeam.getSize()) {
                getGame().addPlayerToTeam(getGamePlayer(), gameTeam);
                updateGui();
            }
        }));
    }

    private ItemStack getTeamItem(GameTeam gameTeam) {
        ItemBuilder itemBuilder = new ItemBuilder(Material.WOOL).setDyeColor(gameTeam.getColor().getDyeColor()).setName(gameTeam.getColoredName() + " &7(" + gameTeam.getSize() + "/" + getGame().getSettings().getGameSize().getTeamMaxPlayer() + ")");
        itemBuilder.addLore("");
        gameTeam.getMembers().forEach(gamePlayer -> itemBuilder.addLore("§7- " + gamePlayer.getPlayer().getName()));
        return itemBuilder.build();
    }

}
