package fr.joupi.api.duelgame.phase;

import fr.joupi.api.game.Game;
import fr.joupi.api.game.GameState;
import fr.joupi.api.game.GameTeam;
import fr.joupi.api.game.event.GamePlayerJoinEvent;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import fr.joupi.api.game.phase.AbstractGamePhase;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class WaitingPhase extends AbstractGamePhase {

    public WaitingPhase(Game<?> game) {
        super(game);
    }

    @Override
    public void onStart() {
        registerEvent(GamePlayerJoinEvent.class, event -> {
            getGame().checkGameState(GameState.WAIT, () -> {
                event.getPlayer().setGameMode(GameMode.ADVENTURE);

                if (getGame().getSettings().getSize().getMinPlayer() == getGame().getSize())
                    endPhase();

                event.sendJoinMessage();
            });
        });

        registerEvent(AsyncPlayerChatEvent.class, event -> {
            getGame().getPlayer(event.getPlayer().getUniqueId())
                    .ifPresent(gamePlayer -> event.setFormat(ChatColor.translateAlternateColorCodes('&', getGame().getTeam(gamePlayer).map(GameTeam::getColoredName).orElse("&fAucune") + " &f%1$s &7: &f%2$s")));
        });

        registerEvent(GamePlayerLeaveEvent.class, GamePlayerLeaveEvent::sendLeaveMessage);
    }

    @Override
    public void onEnd() { }

}
