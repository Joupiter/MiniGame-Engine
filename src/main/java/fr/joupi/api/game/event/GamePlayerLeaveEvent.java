package fr.joupi.api.game.event;

import fr.joupi.api.game.Game;
import fr.joupi.api.game.GamePlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class GamePlayerLeaveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Game game;
    private GamePlayer gamePlayer;

    public Player getPlayer() {
        return Bukkit.getPlayer(gamePlayer.getUuid());
    }

    public void sendLeaveMessage() {
        getGame().broadcast("&c- &7" + getPlayer().getName() + " (" + getGame().getPlayers().size() + "/" + getGame().getSettings().getSize().getMaxPlayer() +")");
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
