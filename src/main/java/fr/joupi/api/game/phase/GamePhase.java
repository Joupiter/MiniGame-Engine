package fr.joupi.api.game.phase;

public interface GamePhase {

    void start();

    default void tick() {}

    void end();

    default void cancel() {
        end();
    }

}