package fr.joupi.api.matchmaking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.Set;
import java.util.function.Predicate;

@Getter
@AllArgsConstructor
public enum RankDivision {

    CHALLENGER (new Rank("Challenger", 1, ChatColor.WHITE)),
    GRANDMASTER (new Rank("Grand Master", 2, ChatColor.RED)),
    MASTER (new Rank("Master", 3, ChatColor.LIGHT_PURPLE)),

    DIAMOND (new Rank("Diamond", 4, ChatColor.AQUA, true)),
    EMERALD (new Rank("Emerald", 5, ChatColor.GREEN, true)),
    PLATINUM (new Rank("Platinum", 6, ChatColor.DARK_AQUA, true)),

    GOLD (new Rank("Gold", 7, ChatColor.GOLD, true)),
    SILVER (new Rank("Silver", 8, ChatColor.GRAY, true)),
    BRONZE (new Rank("Bronze", 9, ChatColor.DARK_GRAY, true));

    private final Rank rank;

    @Getter
    public static class Rank implements Comparable<Rank> {

        private final String name;
        private final int power;
        private final ChatColor color;

        private final boolean subDivided;

        public Rank(String name, int power, ChatColor color, boolean subDivided) {
            this.name = name;
            this.power = power;
            this.color = color;
            this.subDivided = subDivided;
        }

        public Rank(String name, int power, ChatColor color) {
            this(name, power, color, false);
        }

        public Predicate<Rank> isSubDividedPredicate() {
            return Rank::isSubDivided;
        }

        public Set<RankSubDivision> getSubDivision() {
            return isSubDivided() ? Set.of(RankSubDivision.NONE) : Set.of(RankSubDivision.I, RankSubDivision.II, RankSubDivision.III);
        }

        public String getFormatedName() {
            return getColor() + getName();
        }

        @Override
        public int compareTo(Rank rank) {
            return Integer.compare(getPower(), rank.getPower());
        }

    }

}