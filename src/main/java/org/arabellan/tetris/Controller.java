package org.arabellan.tetris;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.events.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for input bindings.
 */
@Slf4j
public class Controller {

    private EventBus eventBus;
    private Map<Key, Event> bindings = new HashMap<>();

    public Controller(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void bind(Key key, Event event) {
        log.debug("Binding " + key + " to " + event.getClass().getSimpleName());
        bindings.put(key, event);
    }

    public void trigger(Key key) {
        if (bindings.containsKey(key)) {
            eventBus.post(bindings.get(key));
        }
    }

    public void clearBindings() {
        bindings.keySet().forEach((key) -> log.debug("Unbinding " + key));
        bindings.clear();
    }

    public enum Key {UNKNOWN, ESCAPE, LEFT, RIGHT, UP, DOWN, SPACE}
}
