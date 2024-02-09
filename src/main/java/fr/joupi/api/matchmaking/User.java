package fr.joupi.api.matchmaking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class User {

    private final UUID uuid;

    private RankDivision division;
    private RankSubDivision subDivision;
    private int elo;

    public User(UUID uuid) {
        this(uuid, RankDivision.BRONZE, RankSubDivision.IV, 0);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getUuid());
    }

    public void setDivision(RankDivision division, RankSubDivision subDivision) {
        this.division = division;
        this.subDivision = subDivision;
    }

    public void setDivision(RankDivision division) {
        setDivision(division, RankSubDivision.NONE);
    }

    public void sendMessages(String... messages) {
        Arrays.asList(messages)
                .forEach(message -> getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
    }

}
