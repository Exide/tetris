package org.arabellan.tetris.scenes;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.domain.Tetrimino;
import org.arabellan.tetris.domain.TetriminoFactory;
import org.arabellan.tetris.events.DropEvent;
import org.arabellan.tetris.events.MoveEvent;
import org.arabellan.tetris.events.QuitEvent;
import org.arabellan.tetris.events.RotateEvent;
import org.arabellan.tetris.managers.InputManager;
import org.arabellan.tetris.managers.InputManager.Key;

/**
 * This class is responsible for the logic during the game.
 */
@Slf4j
public class InGameScene implements Scene {

    private Tetrimino activeTetrimino;
    private Tetrimino nextTetrimino;

    @Inject
    private TetriminoFactory factory;

    @Inject
    private InputManager input;

    @Inject
    private EventBus eventBus;
    private InputListener inputListener;

    @Override
    public void initialize() {
        log.debug("Initializing");
        inputListener = new InputListener();
        eventBus.register(inputListener);

        input.bind(Key.ESCAPE, new QuitEvent());
        input.bind(Key.LEFT, new MoveEvent(-1));
        input.bind(Key.RIGHT, new MoveEvent(1));
        input.bind(Key.UP, new RotateEvent());
        input.bind(Key.DOWN, new DropEvent());

        activeTetrimino = factory.getRandomTetrimino();
        nextTetrimino = factory.getRandomTetrimino();
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
        eventBus.unregister(inputListener);
    }

    private void activateNextTetrimino() {
        activeTetrimino = nextTetrimino;
        nextTetrimino = factory.getRandomTetrimino();
    }

    private void moveTetriminoLeft() {
        log.debug("Moving tetrimino to the left");
    }

    private void moveTetriminoRight() {
        log.debug("Moving tetrimino to the right");
    }

    private void rotateTetrimino() {
        log.debug("Rotating tetrimino");
    }

    private void dropTetrimino() {
        log.debug("Dropping tetrimino");
    }

    private void addTetriminoToWell() {
        if (tetriminoNotAtBottom()) {
            dropTetrimino();
        }

        mergeTetriminoAndWell();
    }

    private boolean tetriminoNotAtBottom() {
        log.debug("Tetrimino not at the bottom of the well");
        return false;
    }

    private void mergeTetriminoAndWell() {
        log.debug("Merging Tetrimino with the well");
    }

    private class InputListener {
        @Subscribe
        public void listenForMove(MoveEvent event) {
            log.debug("MoveEvent received");
            if (event.isLeft()) moveTetriminoLeft();
            if (event.isRight()) moveTetriminoRight();
        }

        @Subscribe
        public void listenForRotate(RotateEvent event) {
            log.debug("RotateEvent received");
            rotateTetrimino();
        }

        @Subscribe
        public void listenForDrop(DropEvent event) {
            log.debug("DropEvent received");
            dropTetrimino();
        }
    }
}
