package fr.joupi.api.listener;

import fr.joupi.api.Spigot;
import fr.joupi.api.game.Game;
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
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] strings) {

        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if (label.equals("join"))
                getPlugin().getGameManager().findGame(player, "duel");

            if (label.equals("leave"))
                getPlugin().getGameManager().getGame(player, game -> game.leaveGame(player.getUniqueId()));

            if (label.equals("info"))
                getPlugin().getGameManager().getGames().values().forEach(games -> games.forEach(game -> game.sendDebugInfoMessage(player)));

        }

        return false;
    }

}
