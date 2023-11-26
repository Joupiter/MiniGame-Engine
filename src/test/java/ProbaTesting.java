import fr.joupi.api.Probabilities;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class ProbaTesting {

    private final Map<String, Integer> counter = new HashMap<>();

    @Test
    public void test() {
        final long start = System.currentTimeMillis();
        Probabilities<String> probabilities = new Probabilities<>();

        probabilities.add(Map.of("Legendary", 4f, "Epic", 10f, "Rare", 30f, "Common", 83f));

        System.out.println("-----------------------------");
        IntStream.rangeClosed(1, 50).forEach(i -> probabilities.randomize().ifPresent(this::execute));

        System.out.println("-----------------------------");
        counter.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).forEach(entry -> System.out.printf("Item: %s | Occurrences: %d \n", entry.getKey(), entry.getValue()));
        System.out.printf("Total: %d \n", counter.values().stream().mapToInt(Integer::intValue).sum());

        System.out.println("-----------------------------");
        probabilities.getItems().forEach((item, prob) -> System.out.printf("Item: %s | Prob: %d%% \n", item, prob.intValue()));

        System.out.println("-----------------------------");
        new Probabilities<>().add("pile", 50f).add("face", 50f).randomize().ifPresent(result -> System.out.printf("[CoinFlip] Result: %s \n", result));

        System.out.println("-----------------------------");
        System.out.println("Operation took " + (System.currentTimeMillis() - start) + " ms");
        System.out.println("-----------------------------");
    }

    private void execute(String item) {
        counter.merge(item, 1, Integer::sum);
        System.out.printf("%s (x%d) \n", item, counter.get(item));
    }

}
/*
-----------------------------
Common (x1)
Common (x2)
Common (x3)
Common (x4)
Common (x5)
Common (x6)
Common (x7)
Common (x8)
Common (x9)
Common (x10)
Common (x11)
Common (x12)
Common (x13)
Common (x14)
Common (x15)
Rare (x1)
Epic (x1)
Rare (x2)
Rare (x3)
Common (x16)
Common (x17)
Common (x18)
Common (x19)
Common (x20)
Common (x21)
Common (x22)
Rare (x4)
Rare (x5)
Rare (x6)
Common (x23)
Rare (x7)
Rare (x8)
Common (x24)
Common (x25)
Common (x26)
Common (x27)
Common (x28)
Common (x29)
Epic (x2)
Common (x30)
Common (x31)
Rare (x9)
Common (x32)
Common (x33)
Rare (x10)
Common (x34)
Legendary (x1)
Rare (x11)
Rare (x12)
Epic (x3)
-----------------------------
Item: Legendary | Occurrences: 1
Item: Epic | Occurrences: 3
Item: Rare | Occurrences: 12
Item: Common | Occurrences: 34
Total: 50
-----------------------------
Item: Rare | Prob: 30%
Item: Legendary | Prob: 4%
Item: Epic | Prob: 10%
Item: Common | Prob: 83%
-----------------------------
[CoinFlip] Result: pile
-----------------------------
Operation took 14 ms
-----------------------------
*/