package fr.joupi.api.command;

import fr.joupi.api.Spigot;
import fr.joupi.api.Utils;
import fr.joupi.api.game.duel.DuelRequestGui;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class DuelCommand implements CommandExecutor {

    private final Spigot spigot;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("Usage: /duel invite|accept|cancel");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "invite":
                handleInviteCommand(player, args);
                break;

            case "accept":
                handleAcceptCommand(player, args);
                break;

            case "cancel":
                handleCancelCommand(player, args);
                break;

            case "list":
                getSpigot().getDuelManager().getRequests().forEach((uuid, request) -> player.sendMessage(String.format("[@] ID: %s | Sender: %s | Target: %s", uuid.toString(), request.getSenderPlayer().map(Player::getName).orElse(request.getSender().toString()), request.getTargetPlayer().map(Player::getName).orElse(request.getTarget().toString()))));
                break;

            default:
                player.sendMessage("Usage: /duel invite|accept|cancel");
                break;
        }

        return true;
    }

    private void handleInviteCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("[Duel] Usage: /duel invite <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (player.equals(target)) {
            player.sendMessage("[Duel] Vous ne pouvez pas vous inviter vous même");
            return;
        }

        if (target == null || !target.isOnline()) {
            player.sendMessage("[Duel] Joueur introuvable ou non connecté");
            return;
        }

        Utils.ifPresentOrElse(getSpigot().getGameManager().getGame(player),
                game -> player.sendMessage("[Duel] Impossible en étant déjà dans un jeu"),
                () -> getSpigot().getGuiManager().open(player, new DuelRequestGui(getSpigot(), player, target)));
    }

    private void handleAcceptCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("[Duel] Utilisation : /duel accept <joueur>");
            return;
        }

        Player sender = Bukkit.getPlayer(args[1]);

        if (player.equals(sender)) {
            player.sendMessage("[Duel] Vous ne pouvez pas faire ça");
            return;
        }

        if (sender == null || !sender.isOnline()) {
            player.sendMessage("[Duel] Joueur introuvable ou non connecté");
            return;
        }

        if (getSpigot().getGameManager().getGame(player).isPresent()) {
            player.sendMessage("[Duel] Impossible en étant déjà dans un jeu");
            return;
        }

        Utils.ifPresentOrElse(getSpigot().getDuelManager().getRequest(sender.getUniqueId(), player.getUniqueId()),
                getSpigot().getDuelManager()::acceptRequest,
                () -> player.sendMessage("[Duel] Introuvable"));
    }

    public void handleCancelCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("[Duel] Utilisation : /duel cancel <joueur>");
            return;
        }

        Player sender = Bukkit.getPlayer(args[1]);

        if (player.equals(sender)) {
            player.sendMessage("[Duel] Vous ne pouvez pas faire ça");
            return;
        }

        if (sender == null || !sender.isOnline()) {
            player.sendMessage("[Duel] Joueur introuvable ou non connecté");
            return;
        }

        getSpigot().getDuelManager().cancelRequest(sender, player);
    }

}
