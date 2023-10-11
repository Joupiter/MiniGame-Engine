package fr.joupi.api.game.phase;

import fr.joupi.api.Utils;
import fr.joupi.api.game.Game;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Getter
public class PhaseManager<G extends Game<?, ?>> {

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
        Utils.ifPresentOrElse(getNextPhase(previousPhase).filter(((Predicate<? super AbstractGamePhase<?>>) getCurrentPhase()::equals).negate()),
                this::setPhase, this::unregisterPhases);
    }

    public void tryRetreat(AbstractGamePhase<?> phase) {
        Utils.ifPresentOrElse(getPreviousPhase(phase).filter(((Predicate<? super AbstractGamePhase<?>>) getCurrentPhase()::equals).negate()), this::setPhase, () -> {
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
        Optional.ofNullable(getCurrentPhase()).filter(phase -> phase.getClass().equals(phaseClass))
                .ifPresent(phase -> consumer.accept((T) phase));
    }

    public Optional<AbstractGamePhase<?>> getNextPhase(AbstractGamePhase<?> phase) {
        return Optional.ofNullable(getPhases().get(getPhases().indexOf(phase) + 1));
    }

    public Optional<AbstractGamePhase<?>> getPreviousPhase(AbstractGamePhase<?> phase) {
        return Optional.ofNullable(getPhases().get(getPhases().indexOf(phase) - 1));
    }

}
