package fr.joupi.api.game.phase;

import com.google.common.collect.Lists;
import fr.joupi.api.game.Game;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class PhaseNode<G extends Game<?>> {

    private final G game;

    private final List<AbstractGamePhase> phases;
    private AbstractGamePhase currentPhase;

    public PhaseNode(G game) {
        this.game = game;
        this.phases = Lists.newLinkedList();
    }

    public void addPhase(AbstractGamePhase... phases) {
        Arrays.asList(phases)
                .forEach(this::addPhase);
    }

    public void addPhase(AbstractGamePhase phase) {
        getPhases().add(phase);
        phase.setOnEnd(() -> tryAdvance(phase));
        phase.setOnCancel(() -> tryRetreat(phase));
    }

    public void tryAdvance(AbstractGamePhase previous) {
        if (currentPhase != previous)
            return;

        AbstractGamePhase next = getNext(previous);

        if (next == null) {
            dispose();
            throw new RuntimeException("Phase Chain is somehow broken, next phase not found");
        }

        setPhase(next);
        /*AbstractGamePhase next = getNext(previous);

        Optional.ofNullable(getNext(previous)).stream()
                .filter(((Predicate<? super AbstractGamePhase>) previous::equals).negate())
                .findFirst()
                .ifPresentOrElse(this::setPhase, this::dispose);*/
    }

    public void tryRetreat(AbstractGamePhase current) {
        if (currentPhase != current)
            return;

        AbstractGamePhase previous = getPrevious(current);

        if (previous == null) {
            current.dispose();
            current.start(); // We dispose and start again, which reboots the phase
            return;
        }

        setPhase(previous);

        /*AbstractGamePhase previous = getPrevious(current);

        Optional.ofNullable(current).stream()
                .filter(((Predicate<? super AbstractGamePhase>) getCurrentPhase()::equals).negate())
                .findFirst()
                .ifPresentOrElse(this::setPhase, () -> {
                    current.dispose();
                    current.start();
                });*/
    }

    public void start() {
        setPhase(getPhases().get(0));
    }

    public void dispose() {
        getPhases().forEach(AbstractGamePhase::dispose);
        getPhases().clear();
        currentPhase = null;
    }

    private void setPhase(AbstractGamePhase current) {
        System.out.println("New phase: " + (current == null ? "null" : current.getClass().getSimpleName()) + " (from " + (currentPhase == null ? "null" : currentPhase.getClass().getSimpleName()) + ")");
        currentPhase = current;
        current.start();
    }

    private AbstractGamePhase getNext(AbstractGamePhase current) {
        if (current == null && !getPhases().isEmpty())
            return getPhases().get(0);

        int index = getPhases().indexOf(current);

        if (index == -1 || index == getPhases().size() - 1)
            return null;

        return getPhases().get(index + 1);
    }

    private AbstractGamePhase getPrevious(AbstractGamePhase current) {
        int index = getPhases().indexOf(current);

        if (index < 1)
            return null;

        return getPhases().get(index -1);
    }

}
