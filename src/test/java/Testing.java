import org.junit.jupiter.api.Test;

import java.util.List;

public class Testing {

    @Test
    public void test() {
        List<String> list = new MergedList<>(List.of("1"), List.of("2"));

        list.forEach(System.out::println);
    }

}