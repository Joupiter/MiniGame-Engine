package fr.joupi.api.duelgame.phase;

import fr.joupi.api.ItemBuilder;
import fr.joupi.api.duelgame.DuelGame;
import fr.joupi.api.duelgame.DuelGamePlayer;
import fr.joupi.api.game.GamePlayer;
import fr.joupi.api.game.GameState;
import fr.joupi.api.duelgame.entity.Golem;
import fr.joupi.api.game.team.GameTeam;
import fr.joupi.api.game.event.GamePlayerLeaveEvent;
import fr.joupi.api.game.phase.AbstractGamePhase;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DuelPhase extends AbstractGamePhase<DuelGame> {

    public DuelPhase(DuelGame game) {
        super(game);
    }

    @Override
    public void onStart() {
        getGame().setState(GameState.IN_GAME);
        getGame().fillTeam();

        getGame().checkSetting(getGame().getSettings().isUseSpecialKit(),
                () -> getGame().getAlivePlayers().stream().map(GamePlayer::getPlayer).forEach(getGame().getSettings()::giveSpecialKit),
                () -> getGame().getAlivePlayers().forEach(gamePlayer -> gamePlayer.getPlayer().getInventory().setItem(0, new ItemBuilder(Material.IRON_SWORD).build())));

        getGame().getAliveTeams().forEach(this::teleportPlayersToBase);

        registerEvent(GamePlayerLeaveEvent.class, GamePlayerLeaveEvent::getPlayer, event -> {
            event.getGamePlayer().setSpectator(true);
            endPhase();
        });

        registerEvent(PlayerDeathEvent.class, PlayerDeathEvent::getEntity, event -> {
            Player player = event.getEntity();
            Player killer = event.getEntity().getKiller();

            if (event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                getGame().getPlayer(player.getUniqueId()).ifPresent(gamePlayer -> {
                    gamePlayer.addDeath(1);
                    gamePlayer.setSpectator(true);
                    gamePlayer.getPlayer().setGameMode(GameMode.SPECTATOR);
                });

                getGame().getPlayer(killer.getUniqueId()).ifPresent(DuelGamePlayer::addKill);

                getGame().broadcast("&a" + player.getName() + " &ea ete tue par &c" + killer.getName() + " &e!");
                endPhase();
            }
        });
    }

    private void teleportPlayersToBase(GameTeam gameTeam) {
        gameTeam.getAlivePlayers().forEach(gamePlayer -> getGame().getSettings().getLocation(gameTeam.getColor().name().toLowerCase()).ifPresent(gamePlayer.getPlayer()::teleport));
    }

    @Override
    public void onEnd() {}

}