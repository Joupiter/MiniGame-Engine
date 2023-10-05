package fr.joupi.api.listener;

import fr.joupi.api.Spigot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class GameCommand implements CommandExecutor {

    private final Spigot plugin;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (label.equals("join") && args.length > 0)
                getPlugin().getGameManager().findGame(player, args[0]);

            if (label.equals("leave"))
                getPlugin().getGameManager().getGame(player, game -> game.leaveGame(player.getUniqueId()));

            if (label.equals("info"))
                getPlugin().getGameManager().getGames().values().forEach(games -> games.forEach(game -> game.sendDebugInfoMessage(player)));

            if (label.equals("end") && args.length > 0) {
                if (args.length == 1)
                    getPlugin().getGameManager().getGame(args[0]).ifPresent(game -> game.endGame(getPlugin().getGameManager()));
                else
                    getPlugin().getGameManager().getGame(args[0], args[1]).ifPresent(game -> game.endGame(getPlugin().getGameManager()));
            }

        }

        return false;
    }

}
