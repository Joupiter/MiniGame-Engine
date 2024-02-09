package fr.joupi.api.matchmaking;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Comparator;

@Getter
@AllArgsConstructor
public enum RankSubDivision {

    I (1),
    II (2),
    III (3),
    IV (4),
    NONE (0);

    private final int power;

    public static Comparator<RankSubDivision> comparator() {
        return Comparator.comparingInt(RankSubDivision::getPower).reversed();
    }

    public static RankSubDivision compare(RankSubDivision o1, RankSubDivision o2) {
        return comparator().compare(o1, o2) > 0 ? o1 : o2;
    }

}