package fr.joupi.api.duelgame.phase;

import fr.joupi.api.CountdownTimer;
import fr.joupi.api.game.Game;
import fr.joupi.api.game.GamePlayer;
import fr.joupi.api.game.GameState;
import fr.joupi.api.game.GameTeam;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import fr.joupi.api.game.phase.AbstractGamePhase;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@Getter
public class CountdownPhase extends AbstractGamePhase {

    private final CountdownTimer countdownTimer;

    public CountdownPhase(Game game) {
        super(game);
        this.countdownTimer = new CountdownTimer(getGame().getPlugin(), 10);
    }

    @Override
    public void onStart() {
        getCountdownTimer().setBeforeTimer(() -> getGame().broadcast("&eLa partie va se lancer !"));
        getCountdownTimer().setEverySecond(timer -> getGame().getPlayers().values().stream().map(GamePlayer::getPlayer).forEach(player -> player.getPlayer().setLevel(timer.getSecondsLeft())));
        getCountdownTimer().setAfterTimer(this::endPhase);

        registerEvent(AsyncPlayerChatEvent.class, event -> {
            if (canTriggerEvent(event.getPlayer().getUniqueId()))
                getGame().getPlayer(event.getPlayer().getUniqueId())
                    .ifPresent(gamePlayer -> event.setFormat(ChatColor.translateAlternateColorCodes('&', getGame().getTeam(gamePlayer).map(GameTeam::getColoredName).orElse("&fAucune") + " &f%1$s &7: &f%2$s")));
        });

        registerEvent(GamePlayerLeaveEvent.class, event -> {
            if (canTriggerEvent(event.getPlayer().getUniqueId())) {
                event.getGame().getPlayers().remove(event.getPlayer().getUniqueId());

                getGame().checkGameState(GameState.WAIT, () -> {
                    if (getGame().getSize() < getGame().getSettings().getSize().getMinPlayer()) {
                        getCountdownTimer().cancelTimer();
                        cancelPhase();
                        System.out.println("CANCEL TIMER BCS HAVING NO MUCH PLAYER FOR START!");
                    }

                });
                event.sendLeaveMessage();
            }
        });

        getCountdownTimer().setSecondsLeft(10);
        getCountdownTimer().scheduleTimer();
    }

    @Override
    public void onEnd() {
        getGame().broadcast("&eBonne chance !");
        getCountdownTimer().cancelTimer();
    }

}
