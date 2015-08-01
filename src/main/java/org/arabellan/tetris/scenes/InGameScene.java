package org.arabellan.tetris.scenes;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.common.Coord;
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

/**
 * This class is responsible for the logic during the game.
 */
@Slf4j
public class InGameScene implements Scene {

    private static final int INITIAL_TIMESTEP_IN_MS = 1750;
    private static final int GAMESPEED_INCREASE_IN_MS = 50;

    private int currentLevel;
    private int currentTimestep;
    private long currentPoints;
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
        currentLevel = 1;
        currentTimestep = INITIAL_TIMESTEP_IN_MS;
        currentPoints = 0;
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
        long delta = Duration.between(lastUpdate, Instant.now()).toMillis();
        if (delta >= currentTimestep) {
            log.debug(String.format("Tick! (%sms delta)", delta));
            if (shouldIncreaseLevel()) increaseLevel();
            updateActiveTetrimino();
            lastUpdate = Instant.now();
        }
    }

    private boolean shouldIncreaseLevel() {
        ++currentPoints; // debug hack until we can clear blocks
        int pointsRequiredMod = 2;
        int pointsNeededForNextLevel = currentLevel * pointsRequiredMod * currentLevel;
        return currentPoints > pointsNeededForNextLevel;
    }

    private void increaseLevel() {
        ++currentLevel;
        currentTimestep -= GAMESPEED_INCREASE_IN_MS;
        double currentTimestepInSeconds = ((double) currentTimestep) / 1000;
        log.debug(String.format("Level increased to %s (%ss per tick)", currentLevel, currentTimestepInSeconds));
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
        Coord nextPosition = Coord.builder().x(0).y(1).build();
        Tetrimino stub = factory.getMovedStub(activeTetrimino, nextPosition);
        if (well.isPositionAllowed(stub)) {
            activeTetrimino.setPosition(stub.getPosition());
        } else {
            throw new InvalidMoveException();
        }
    }

    private void moveTetriminoLeft() {
        log.debug("Moving tetrimino to the left");
        Coord nextPosition = Coord.builder().x(-1).y(0).build();
        Tetrimino stub = factory.getMovedStub(activeTetrimino, nextPosition);
        if (well.isPositionAllowed(stub)) {
            activeTetrimino.setPosition(stub.getPosition());
        }
    }

    private void moveTetriminoRight() {
        log.debug("Moving tetrimino to the right");
        Coord nextPosition = Coord.builder().x(1).y(0).build();
        Tetrimino stub = factory.getMovedStub(activeTetrimino, nextPosition);
        if (well.isPositionAllowed(stub)) {
            activeTetrimino.setPosition(stub.getPosition());
        }
    }

    private void rotateTetrimino() {
        log.debug("Rotating tetrimino");
        Tetrimino rotatedStub = factory.getRotatedStub(activeTetrimino);
        if (well.isPositionAllowed(rotatedStub)) {
            activeTetrimino.setOrientation(rotatedStub.getOrientation());
        }
    }

    private void dropTetrimino() {
        log.debug("Dropping tetrimino");
        Coord nextPosition = Coord.builder().x(0).y(1).build();
        Tetrimino stub = factory.getMovedStub(activeTetrimino, nextPosition);
        while (well.isPositionAllowed(stub)) {
            activeTetrimino.setPosition(stub.getPosition());
            stub = factory.getMovedStub(activeTetrimino, nextPosition);
        }
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
