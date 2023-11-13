package fr.joupi.api.game.listener;

import lombok.AllArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.util.function.Consumer;

@AllArgsConstructor
public class EventListenerWrapper<E extends Event> implements Listener {

    public final Consumer<E> event;

}