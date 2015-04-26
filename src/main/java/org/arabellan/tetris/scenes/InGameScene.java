package org.arabellan.tetris.scenes;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.domain.InvalidMoveException;
import org.arabellan.tetris.domain.Tetrimino;
import org.arabellan.tetris.domain.TetriminoFactory;
import org.arabellan.tetris.domain.Well;
import org.arabellan.tetris.events.DropEvent;
import org.arabellan.tetris.events.MoveEvent;
import org.arabellan.tetris.events.QuitEvent;
import org.arabellan.tetris.events.RotateEvent;
import org.arabellan.tetris.managers.InputManager;
import org.arabellan.tetris.managers.InputManager.Key;

import java.time.Duration;
import java.time.Instant;

/**
 * This class is responsible for the logic during the game.
 */
@Slf4j
public class InGameScene implements Scene {

    private static int TIME_STEP_IN_MS = 1750;
    private Instant lastUpdate = Instant.now();

    private Well well;
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
        initializeInput();
        initializeGameObjects();
    }

    private void initializeInput() {
        inputListener = new InputListener();
        eventBus.register(inputListener);

        input.bind(Key.ESCAPE, new QuitEvent());
        input.bind(Key.LEFT, new MoveEvent(-1));
        input.bind(Key.RIGHT, new MoveEvent(1));
        input.bind(Key.UP, new RotateEvent());
        input.bind(Key.DOWN, new DropEvent());
    }

    private void initializeGameObjects() {
        well = new Well();
        activeTetrimino = factory.getRandomTetrimino();
        nextTetrimino = factory.getRandomTetrimino();
    }

    @Override
    public void update() {
        long delta = Duration.between(lastUpdate, Instant.now()).toMillis();
        if (delta >= TIME_STEP_IN_MS) {
            log.debug("Tick!");
            updateActiveTetrimino();
            lastUpdate = Instant.now();
        }
    }

    private void updateActiveTetrimino() {
        try {
            moveTetriminoDown();
        } catch (InvalidMoveException e) {
            finalizeActiveTetrimino();
        }
    }

    private void finalizeActiveTetrimino() {
        well.add(activeTetrimino);
        activateNextTetrimino();
    }

    private void activateNextTetrimino() {
        activeTetrimino = nextTetrimino;
        nextTetrimino = factory.getRandomTetrimino();
    }

    @Override
    public void cleanup() {
        log.debug("Cleaning up");
        input.clearBindings();
        eventBus.unregister(inputListener);
    }

    private void moveTetriminoDown() throws InvalidMoveException {
        log.debug("Moving tetrimino down");
        Tetrimino potentialTetrimino = Tetrimino.builder()
                .type(activeTetrimino.getType())
                .color(activeTetrimino.getColor())
                .shape(activeTetrimino.getShape())
                .position(activeTetrimino.getPosition().translate(0, 1))
                .build();

        if (well.isPositionAllowed(potentialTetrimino)) {
            activeTetrimino.setPosition(potentialTetrimino.getPosition());
        } else {
            throw new InvalidMoveException();
        }
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
