package org.arabellan.tetris;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for input bindings.
 */
@Slf4j
public class Controller {

    public enum Key {ESCAPE, LEFT, RIGHT, UP, DOWN, SPACE }

    @Inject
    private EventBus eventBus;
    private Map<Key, Event> bindings = new HashMap<>();

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
}
