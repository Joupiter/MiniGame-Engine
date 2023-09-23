package fr.joupi.api.listener;

import fr.joupi.api.Spigot;
import fr.joupi.api.game.GamePlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class GameCommand implements CommandExecutor {

    private final Spigot plugin;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] strings) {

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (label.equals("join"))
                getPlugin().getDuelGame().joinGame(player);

            if (label.equals("leave"))
                getPlugin().getDuelGame().leaveGame(player.getUniqueId());


            if (label.equals("!info")) {
                player.sendMessage("-----------------------------");
                player.sendMessage("Game: " + getPlugin().getDuelGame().getFullName());
                player.sendMessage("Size: type=" + getPlugin().getDuelGame().getSettings().getSize().getName() + ", min=" + getPlugin().getDuelGame().getSettings().getSize().getMinPlayer() + ", max=" + getPlugin().getDuelGame().getSettings().getSize().getMaxPlayer() + ", tn=" + getPlugin().getDuelGame().getSettings().getSize().getTeamNeeded() + ", tm=" + getPlugin().getDuelGame().getSettings().getSize().getTeamMaxPlayer());
                player.sendMessage("State: " + getPlugin().getDuelGame().getState());

                player.sendMessage("Teams: " + getPlugin().getDuelGame().getTeamsCount());
                getPlugin().getDuelGame().getTeams().forEach(gameTeam -> player.sendMessage(gameTeam.getName() + ": " + gameTeam.getMembers().stream().map(GamePlayer::getPlayer).map(Player::getName).collect(Collectors.joining(", "))));

                player.sendMessage("Players: " + getPlugin().getDuelGame().getSize() + "(" + getPlugin().getDuelGame().getAlivePlayersCount() + "|" + getPlugin().getDuelGame().getSpectatorsCount() + ")");
                player.sendMessage("Alive players: " + getPlugin().getDuelGame().getAlivePlayers().stream().map(GamePlayer::getPlayer).map(Player::getName).collect(Collectors.joining(", ")));
                player.sendMessage("Spectator players: " + getPlugin().getDuelGame().getSpectators().stream().map(GamePlayer::getPlayer).map(Player::getName).collect(Collectors.joining(", ")));
                player.sendMessage("-----------------------------");
            }

        }

        return false;
    }

}
