package fr.joupi.api.matchmaking;

import fr.joupi.api.Spigot;
import fr.joupi.api.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class DivisionCommand implements CommandExecutor {

    private final Spigot plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Utilisation incorrecte. /division <nom-du-rank>");
            return true;
        }

        if (args[0].equals("debug")) {
            player.sendMessage("Divisions: ");
            Arrays.stream(RankDivision.values()).map(RankDivision::getRank).forEach(rank -> {
                player.sendMessage("Name: " + rank.getName());
                player.sendMessage("Power: " + rank.getPower());
                player.sendMessage("Color: " + rank.getColor().name());
                player.sendMessage("SubDivision: " + rank.getSubDivision().stream().map(RankSubDivision::name).collect(Collectors.joining(", ")));
                player.sendMessage("---------------------");
            });
            return true;
        }

        if (Arrays.stream(RankDivision.values()).map(RankDivision::name).noneMatch(args[0]::equalsIgnoreCase)) {
            sender.sendMessage(ChatColor.RED + "Rang invalide. Rangs disponibles : " + getAvailableRanks());
            return true;
        }

        Utils.ifPresentOrElse(getPlugin().getMatchMakingManager().getUser(player.getUniqueId()),
                user -> changeRank(user, RankDivision.valueOf(args[0].toUpperCase())),
                () -> player.sendMessage(ChatColor.RED + "Impossible de trouver l'utilisateur."));

        return true;
    }

    private void changeRank(User user, RankDivision division) {
        user.setDivision(division, division.getRank().isSubDivided() ? RankSubDivision.IV : RankSubDivision.NONE);
        user.sendMessages("&aVotre rang a été mis à jour avec succès. Nouveau rang : " + division.getRank().getFormatedName());
    }

    private String getAvailableRanks() {
        return Arrays.stream(RankDivision.values())
                .map(rank -> rank.getRank().getFormatedName())
                .collect(Collectors.joining(", "));
    }

}
