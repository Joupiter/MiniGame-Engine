package fr.joupi.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BooleanWrapper {

    private final boolean condition;

    public static BooleanWrapper of(boolean condition) {
        return new BooleanWrapper(condition);
    }

    public BooleanWrapper ifTrue(Runnable runnable) {
        if (condition) runnable.run();
        return this;
    }

    public void ifFalse(Runnable runnable) {
        if (!condition) runnable.run();
    }

}