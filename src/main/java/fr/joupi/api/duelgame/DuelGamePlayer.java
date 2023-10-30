package fr.joupi.api.duelgame;

import com.google.gson.annotations.Expose;
import fr.joupi.api.game.GamePlayer;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class DuelGamePlayer extends GamePlayer {

    @Expose @Setter private int kills, killStreak, deaths;

    public DuelGamePlayer(UUID uuid, int kills, int killStreak, int deaths, boolean spectator) {
        super(uuid, spectator);
        this.kills = kills;
        this.killStreak = killStreak;
        this.deaths = deaths;
    }

    public void addKill(int kills) {
        setKills(getKills() + kills);
    }

    public void addKill() {
        addKill(1);
    }

    public void addKillStreak(int killStreak) {
        setKillStreak(getKillStreak() + killStreak);
    }

    public void addKillStreak() {
        addKillStreak(1);
    }

    public void addDeath(int deaths) {
        setDeaths(getDeaths() + deaths);
    }

    public void addDeath() {
        addDeath(1);
    }

}
