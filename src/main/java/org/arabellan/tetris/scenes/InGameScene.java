package org.arabellan.tetris.scenes;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.managers.InputManager;
import org.arabellan.tetris.managers.InputManager.Key;
import org.arabellan.tetris.events.DropEvent;
import org.arabellan.tetris.events.MoveEvent;
import org.arabellan.tetris.events.QuitEvent;
import org.arabellan.tetris.events.RotateEvent;

/**
 * This class is responsible for the logic during the game.
 */
@Slf4j
public class InGameScene implements Scene {

    @Inject
    private InputManager input;

    @Inject
    private EventBus eventBus;

    @Override
    public void initialize() {
        log.debug("Initializing");
        eventBus.register(new InputListener());
        input.bind(Key.ESCAPE, new QuitEvent());
        input.bind(Key.LEFT, new MoveEvent(-1));
        input.bind(Key.RIGHT, new MoveEvent(1));
        input.bind(Key.UP, new RotateEvent());
        input.bind(Key.DOWN, new DropEvent());
    }

    @Override
    public void update(double delta) {
        input.trigger(Key.LEFT);
        input.trigger(Key.RIGHT);
        input.trigger(Key.UP);
        input.trigger(Key.DOWN);
        input.trigger(Key.ESCAPE);
    }

    @Override
    public void cleanup() {
        log.debug("Cleaning up");
        input.clearBindings();
        eventBus.unregister(new InputListener());
    }

    private class InputListener {
        @Subscribe
        public void listenForMove(MoveEvent event) {
            log.debug("MoveEvent received");
            if (event.isLeft()) {
                log.debug("moving piece left");
            } else if (event.isRight()) {
                log.debug("moving piece right");
            } else {
                log.debug("wtf");
            }
        }

        @Subscribe
        public void listenForRotate(RotateEvent event) {
            log.debug("RotateEvent received");
        }

        @Subscribe
        public void listenForDrop(DropEvent event) {
            log.debug("DropEvent received");
        }
    }
}
