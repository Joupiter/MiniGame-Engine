package fr.joupi.api.game.phase;

import fr.joupi.api.game.Game;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Getter
public class PhaseManager<G extends Game<?>> {

    private final G game;

    private final List<AbstractGamePhase<?>> phases;
    @Setter private AbstractGamePhase<?> currentPhase;

    public PhaseManager(G game) {
        this.game = game;
        this.phases = new LinkedList<>();
    }

    public final void addPhase(AbstractGamePhase<?>... phases) {
        Arrays.asList(phases)
                .forEach(getPhases()::add);
    }

    private void setPhase(AbstractGamePhase<?> phase) {
        setCurrentPhase(phase);
        phase.startPhase();
    }

    public void tryAdvance(AbstractGamePhase<?> previousPhase) {
        getNextPhase(previousPhase)
                .filter(((Predicate<? super AbstractGamePhase<?>>) getCurrentPhase()::equals).negate())
                .ifPresentOrElse(this::setPhase, this::unregisterPhases);
    }

    public void tryRetreat(AbstractGamePhase<?> phase) {
        getPreviousPhase(phase)
                .filter(((Predicate<? super AbstractGamePhase<?>>) getCurrentPhase()::equals).negate())
                .ifPresentOrElse(this::setPhase, () -> {
                    phase.unregister();
                    phase.startPhase();
                });
    }

    public void start() {
        setPhase(getPhases().get(0));
    }

    public void unregisterPhases() {
        getPhases().forEach(AbstractGamePhase::unregister);
        getPhases().clear();
        setCurrentPhase(null);
    }

    public Optional<AbstractGamePhase<?>> getNextPhase(AbstractGamePhase<?> phase) {
        return Optional.ofNullable(getPhases().get(getPhases().indexOf(phase) + 1));
    }

    public Optional<AbstractGamePhase<?>> getPreviousPhase(AbstractGamePhase<?> phase) {
        return Optional.ofNullable(getPhases().get(getPhases().indexOf(phase) - 1));
    }

}
