package fr.joupi.api.game.listener;

import fr.joupi.api.game.Game;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Listener;

@Getter
@AllArgsConstructor
public class GameListenerWrapper<G extends Game<?, ?, ?>> implements Listener {

    private final G game;

}
