package fr.joupi.api;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MainTest {

    public static void main(String[] args) {
        List<?> list = new ArrayList<>(List.of(1, 2, 3));

        list.forEach(System.out::println);

        System.out.println("---------------------------");
        CompletableFuture.runAsync(() -> System.out.println("run async")).whenComplete((unused, throwable) -> System.out.println("finish"));

        System.out.println("---------------------------");
        get(UUID.randomUUID()).join();

        System.out.println("---------------------------");
        System.out.println("future: " + future().join());

        System.out.println("---------------------------");
        writeFile(UUID.randomUUID());
    }

    private static void writeFile(UUID uuid) {
        CompletableFuture.runAsync(() -> System.out.println("RUNNING FILE (" + uuid + ")"))
                .thenAccept(unused -> System.out.println("ACCEPTED TASK WRITING FILE (" + uuid + ")"))
                .whenComplete((unused, throwable) -> System.out.println("FINISHED WRITING FILE (" + uuid + ")"));
    }

    private static CompletableFuture<Void> get(UUID uuid) {
        return CompletableFuture.runAsync(() -> System.out.println("GETTING FILE (" + uuid + ")"));
    }

    private static CompletableFuture<UUID> future() {
        return CompletableFuture.supplyAsync(UUID::randomUUID);
    }

}