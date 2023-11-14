package fr.joupi.api.command;

import fr.joupi.api.Spigot;
import fr.joupi.api.Utils;
import fr.joupi.api.game.party.GameParty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class GamePartyCommand implements CommandExecutor {

    private final Spigot spigot;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("Usage: /party create|invite|join|disband");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                handleCreateCommand(player);
                break;

            case "invite":
                handleInviteCommand(player, args);
                break;

            case "join":
                handleJoinCommand(player, args);
                break;

            case "leave":
                handleLeaveCommand(player);
                break;

            case "disband":
                handleDisbandCommand(player);
                break;

            case "list":
                getSpigot().getGameManager().getPartyManager().getParties().forEach(gameParty -> gameParty.sendDebug(player));
                break;

            case "invits":
                getSpigot().getGameManager().getPartyManager().sendInvitationsDebug(player);
            break;

            case "open":
                getSpigot().getGameManager().getPartyManager().getParty(player)
                        .filter(gameParty -> !gameParty.isOpened())
                        .filter(gameParty -> gameParty.isLeader(player.getUniqueId()))
                        .ifPresent(gameParty -> {
                            gameParty.setOpened(true);
                            player.sendMessage("Vous avez rendu votre partie publique avec succès");
                        });
                break;

            case "close":
                getSpigot().getGameManager().getPartyManager().getParty(player)
                        .filter(GameParty::isOpened)
                        .filter(gameParty -> gameParty.isLeader(player.getUniqueId()))
                        .ifPresent(gameParty -> {
                            gameParty.setOpened(false);
                            player.sendMessage("Vous avez fermé votre partie au public");
                        });
                break;

            default:
                player.sendMessage("Usage: /party create|invite|join|leave|disband");
                break;
        }

        return true;
    }

    private void handleCreateCommand(Player player) {
        Utils.ifPresentOrElse(getSpigot().getGameManager().getPartyManager().getParty(player),
                gameParty -> player.sendMessage("Vous avez déjà une partie tapez /party disband"),
                () -> {
                    getSpigot().getGameManager().getPartyManager().addParty(new GameParty(player.getUniqueId()));
                    player.sendMessage("Vous avez créer une partie !");
                });
    }

    private void handleInviteCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /party invite <player>");
            return;
        }

        Player invitedPlayer = Bukkit.getPlayer(args[1]);

        if (invitedPlayer == null || !invitedPlayer.isOnline()) {
            player.sendMessage("Joueur introuvable ou non connecté");
            return;
        }

        Utils.ifPresentOrElse(getSpigot().getGameManager().getPartyManager().getParty(player),
                gameParty -> getSpigot().getGameManager().getPartyManager().sendRequest(player, invitedPlayer),
                () -> player.sendMessage("Vous n'êtes pas dans une partie"));
    }

    private void handleJoinCommand(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Utilisation : /party join <joueur>");
            return;
        }

        Player leader = Bukkit.getPlayer(args[1]);

        if (leader == null || !leader.isOnline()) {
            player.sendMessage("Joueur introuvable ou non connecté");
            return;
        }

        Utils.ifPresentOrElse(getSpigot().getGameManager().getPartyManager().getParty(leader),
                gameParty -> getSpigot().getGameManager().getPartyManager().joinParty(player, gameParty),
                () -> player.sendMessage("Partie introuvable"));
    }

    public void handleLeaveCommand(Player player) {
        Utils.ifPresentOrElse(getSpigot().getGameManager().getPartyManager().getParty(player),
                gameParty -> {
                    getSpigot().getGameManager().getPartyManager().leaveParty(player);
                    player.sendMessage("Vous avez quitter votre partie");
                }, () -> player.sendMessage("Vous n'avez pas de partie"));
    }

    private void handleDisbandCommand(Player player) {
        Utils.ifPresentOrElse(getSpigot().getGameManager().getPartyManager().getParty(player),
                gameParty -> {
                    getSpigot().getGameManager().getPartyManager().removeParty(gameParty);
                    player.sendMessage("Vous avez supprimer votre partie");
                }, () -> player.sendMessage("Vous n'avez pas de partie"));
    }

}
