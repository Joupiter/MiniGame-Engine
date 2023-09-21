package fr.joupi.api.game.event;

import fr.joupi.api.game.Game;
import fr.joupi.api.game.phase.AbstractGamePhase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@AllArgsConstructor
public class GamePhaseChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Game<?> game;
    private AbstractGamePhase gamePhase;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
