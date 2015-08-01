package org.arabellan.tetris.scenes;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.Controller;
import org.arabellan.tetris.Controller.Key;
import org.arabellan.tetris.Renderable;
import org.arabellan.tetris.Scene;
import org.arabellan.tetris.domain.InvalidMoveException;
import org.arabellan.tetris.domain.Tetrimino;
import org.arabellan.tetris.domain.TetriminoFactory;
import org.arabellan.tetris.domain.Well;
import org.arabellan.tetris.events.ChangeSceneEvent;
import org.arabellan.tetris.events.DropEvent;
import org.arabellan.tetris.events.MoveEvent;
import org.arabellan.tetris.events.QuitEvent;
import org.arabellan.tetris.events.RotateEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.function.LongConsumer;

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
    private Controller input;

    @Inject
    private EventBus eventBus;
    private InputListener inputListener;

    @Override
    public void initialize() {
        log.debug("Initializing");
        initializeInput();
        initializeGameObjects();
    }

    @Override
    public List<Renderable> getRenderables() {
        return Arrays.asList(well, activeTetrimino);
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
        doAtTimeStep(delta -> {
            log.debug("Tick!");
            moveTetriminoLeft();
            updateActiveTetrimino();
        });
    }

    private void doAtTimeStep(LongConsumer consumer) {
        long delta = Duration.between(lastUpdate, Instant.now()).toMillis();
        if (delta >= TIME_STEP_IN_MS) {
            consumer.accept(delta);
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
        try {
            well.add(activeTetrimino);
            activateNextTetrimino();
        } catch (InvalidMoveException e) {
            gameOver();
        }
    }

    private void gameOver() {
        log.debug("Game over!");
        eventBus.post(new ChangeSceneEvent(MainMenuScene.class));
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
        Tetrimino potentialTetrimino = getPotentialTetrimino(0, 1);

        if (well.isPositionAllowed(potentialTetrimino)) {
            activeTetrimino.setPosition(potentialTetrimino.getPosition());
        } else {
            throw new InvalidMoveException();
        }
    }

    private void moveTetriminoLeft() {
        log.debug("Moving tetrimino to the left");
        Tetrimino potentialTetrimino = getPotentialTetrimino(-1, 0);
        if (well.isPositionAllowed(potentialTetrimino)) {
            activeTetrimino.setPosition(potentialTetrimino.getPosition());
        }
    }

    private void moveTetriminoRight() {
        log.debug("Moving tetrimino to the right");
        Tetrimino potentialTetrimino = getPotentialTetrimino(1, 0);
        if (well.isPositionAllowed(potentialTetrimino)) {
            activeTetrimino.setPosition(potentialTetrimino.getPosition());
        }
    }

    private void rotateTetrimino() {
        log.debug("Rotating tetrimino");
    }

    private void dropTetrimino() {
        log.debug("Dropping tetrimino");
        Tetrimino potentialTetrimino = getPotentialTetrimino(0, 1);

        while (well.isPositionAllowed(potentialTetrimino)) {
            activeTetrimino.setPosition(potentialTetrimino.getPosition());
            potentialTetrimino = getPotentialTetrimino(0, 1);
        }
    }

    private Tetrimino getPotentialTetrimino(int x, int y) {
        return Tetrimino.builder()
                .type(activeTetrimino.getType())
                .color(activeTetrimino.getColor())
                .renderable(activeTetrimino.getRenderable())
                .position(activeTetrimino.getPosition().translate(x, y))
                .build();
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
