package fr.joupi.api.ffa;

import fr.joupi.api.game.listener.GameListenerWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class FFAGameTestListener extends GameListenerWrapper<FFAGame> {

    public FFAGameTestListener(FFAGame game) {
        super(game);
    }

    @EventHandler
    public void onSomething(PlayerPickupItemEvent event) {
        if (getGame().containsPlayer(event.getPlayer().getUniqueId())) {
            Player player = event.getPlayer();
            player.sendMessage("COUCOU!");
        }
    }

}
