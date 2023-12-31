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
public class GamePlayerJoinEvent<G extends GamePlayer> extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Game<G, ?, ?> game;
    private final G gamePlayer;

    public Player getPlayer() {
        return getGamePlayer().getPlayer();
    }

    public void sendJoinMessage() {
        getGame().broadcast(String.format("&a+ &7%s (%d/%d)", getPlayer().getName(), getGame().getPlayers().size(), getGame().getSettings().getGameSize().getMaxPlayer()));
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
