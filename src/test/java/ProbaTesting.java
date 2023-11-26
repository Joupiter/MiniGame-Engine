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
        probabilities.getItems().forEach((item, prob) -> System.out.printf("Item: %s | Prob: %f \n", item, prob));

        System.out.println("-----------------------------");
        new Probabilities<>().add("pile", 50f).add("face", 50f).randomize().ifPresent(result -> System.out.printf("[CoinFlip] Result: %s \n", result));

        System.out.println("-----------------------------");
        System.out.println("Operation took " + (System.currentTimeMillis() - start) + " ms");
        System.out.println("-----------------------------");
    }

    private void execute(String item) {
        counter.merge(item, 1, Integer::sum);
        System.out.printf("%s (x%d)\n", item, counter.get(item));
    }

}
/*
-----------------------------
Common (x1)
Epic (x1)
Rare (x1)
Common (x2)
Common (x3)
Common (x4)
Epic (x2)
Common (x5)
Common (x6)
Rare (x2)
Epic (x3)
Common (x7)
Epic (x4)
Rare (x3)
Rare (x4)
Rare (x5)
Common (x8)
Rare (x6)
Common (x9)
Common (x10)
Common (x11)
Common (x12)
Rare (x7)
Common (x13)
Legendary (x1)
Common (x14)
Common (x15)
Common (x16)
Rare (x8)
Common (x17)
Common (x18)
Rare (x9)
Rare (x10)
Common (x19)
Common (x20)
Common (x21)
Epic (x5)
Rare (x11)
Common (x22)
Common (x23)
Common (x24)
Rare (x12)
Common (x25)
Rare (x13)
Common (x26)
Epic (x6)
Common (x27)
Epic (x7)
Common (x28)
Rare (x14)
-----------------------------
Item: Legendary | Occurrences: 1
Item: Epic | Occurrences: 7
Item: Rare | Occurrences: 14
Item: Common | Occurrences: 28
Total: 50
-----------------------------
Item: Epic | Prob: 10.000000
Item: Rare | Prob: 30.000000
Item: Legendary | Prob: 4.000000
Item: Common | Prob: 83.000000
-----------------------------
[CoinFlip] Result: pile
-----------------------------
Operation took 17 ms
-----------------------------
*/