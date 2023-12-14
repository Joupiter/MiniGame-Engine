package fr.joupi.api.game.phase;

import fr.joupi.api.Utils;
import fr.joupi.api.game.Game;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Getter
@Setter
public class PhaseManager<G extends Game<?, ?, ?>> {

    private final G game;

    private final List<AbstractGamePhase<?>> phases;
    private AbstractGamePhase<?> currentPhase;

    public PhaseManager(G game) {
        this.game = game;
        this.phases = new ArrayList<>();
    }

    public final void addPhases(AbstractGamePhase<?>... phases) {
        Arrays.asList(phases)
                .forEach(getPhases()::add);
    }

    private void setPhase(AbstractGamePhase<?> phase) {
        setCurrentPhase(phase);
        phase.startPhase();
    }

    public void tryAdvance(AbstractGamePhase<?> previousPhase) {
        Utils.ifPresentOrElse(getNextPhase(previousPhase).filter(equalsToCurrentPhase().negate()),
                this::setPhase, this::unregisterPhases);
    }

    public void tryRetreat(AbstractGamePhase<?> phase) {
        Utils.ifPresentOrElse(getPreviousPhase(phase).filter(equalsToCurrentPhase().negate()), this::setPhase, () -> {
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

    public <T extends AbstractGamePhase<?>> void checkGamePhase(Class<T> phaseClass, Consumer<T> consumer) {
        Optional.ofNullable(getCurrentPhase())
                .filter(phaseClass::isInstance)
                .map(phaseClass::cast)
                .ifPresent(consumer);
    }

    public Optional<AbstractGamePhase<?>> getNextPhase(AbstractGamePhase<?> phase) {
        return Optional.ofNullable(getPhases().get(getPhases().indexOf(phase) + 1));
    }

    public Optional<AbstractGamePhase<?>> getPreviousPhase(AbstractGamePhase<?> phase) {
        return Optional.ofNullable(getPhases().get(getPhases().indexOf(phase) - 1));
    }

    private Predicate<AbstractGamePhase<?>> equalsToCurrentPhase() {
        return getCurrentPhase()::equals;
    }

}
