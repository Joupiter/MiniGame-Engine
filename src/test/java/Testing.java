import fr.joupi.api.matchmaking.RankSubDivision;
import org.junit.jupiter.api.Test;

public class Testing {

    @Test
    public void test() {
        RankSubDivision one = RankSubDivision.IV;
        RankSubDivision four = RankSubDivision.IV;

        System.out.println(RankSubDivision.compare(one, four));
    }

}