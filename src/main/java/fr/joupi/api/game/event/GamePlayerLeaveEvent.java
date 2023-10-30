package fr.joupi.api.game.event;

import fr.joupi.api.game.Game;
import fr.joupi.api.game.GamePlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class GamePlayerLeaveEvent<G extends GamePlayer> extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Game<G, ?> game;
    private final G gamePlayer;

    public Player getPlayer() {
        return getGamePlayer().getPlayer();
    }

    public void sendLeaveMessage() {
        getGame().broadcast(String.format("&c- &7%s (%d/%d)", getPlayer().getName(), getGame().getPlayers().size(), getGame().getSettings().getGameSize().getMaxPlayer()));
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
