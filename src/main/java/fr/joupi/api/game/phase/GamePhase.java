package fr.joupi.api.game.phase;

public interface GamePhase {

    void onStart();

    void onEnd();

    default void onCancel() {}

}
