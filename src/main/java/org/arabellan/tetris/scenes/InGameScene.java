package org.arabellan.tetris.scenes;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.Controller;
import org.arabellan.tetris.Controller.Key;
import org.arabellan.tetris.Renderable;
import org.arabellan.tetris.domain.InvalidMoveException;
import org.arabellan.tetris.domain.Tetrimino;
import org.arabellan.tetris.domain.TetriminoFactory;
import org.arabellan.tetris.domain.Well;
import org.arabellan.tetris.events.ChangeSceneEvent;
import org.arabellan.tetris.events.DropEvent;
import org.arabellan.tetris.events.MoveEvent;
import org.arabellan.tetris.events.QuitEvent;
import org.arabellan.tetris.events.RotateEvent;
import org.joml.Vector2f;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class is responsible for the logic during the game.
 */
@Slf4j
public class InGameScene implements Scene {

    private static final long STARTING_SPEED = TimeUnit.MILLISECONDS.toMillis(1500);
    private static final long SPEED_CHANGE = TimeUnit.MILLISECONDS.toMillis(100);
    private static final int LINES_NEEDED_MULTIPLIER = 10;
    private static final int POINTS_PER_LINE = 100;

    private int totalLinesCleared;
    private long currentPoints;
    private int currentLevel;
    private long gameSpeed;
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
        currentPoints = 0;
        currentLevel = 1;
        gameSpeed = STARTING_SPEED;
    }

    private void initializeInput() {
        inputListener = new InputListener();
        eventBus.register(inputListener);
        input.bind(Key.ESCAPE, new QuitEvent());
        input.bind(Key.SPACE, new DropEvent());
        input.bind(Key.UP, new RotateEvent());
        input.bind(Key.DOWN, new MoveEvent(MoveEvent.Direction.Down));
        input.bind(Key.LEFT, new MoveEvent(MoveEvent.Direction.Left));
        input.bind(Key.RIGHT, new MoveEvent(MoveEvent.Direction.Right));
    }

    private void initializeGameObjects() {
        well = new Well();
        activeTetrimino = factory.getRandomTetrimino();
        nextTetrimino = factory.getRandomTetrimino();
    }

    @Override
    public List<Renderable> getRenderables() {
        Renderable wellRenderable = combineActiveTetriminoWithWell();
        return Collections.singletonList(wellRenderable);
    }

    private Renderable combineActiveTetriminoWithWell() {
        return Renderable.builder()
                .matrix(well.getMatrix()
                        .copy()
                        .add(activeTetrimino.getMatrix(), activeTetrimino.getPosition()))
                .position(new Vector2f())
                .build();
    }

    @Override
    public void update() {
        long delta = Duration.between(lastUpdate, Instant.now()).toMillis();
        if (delta >= gameSpeed) {
            log.debug(String.format("Tick! (%sms delta)", delta));
            updateActiveTetrimino();
            clearRowsIfNeeded();
            increaseLevelIfNeeded();
            lastUpdate = Instant.now();
        }
    }

    private void clearRowsIfNeeded() {
        int linesCleared = well.clearCompleteRows();
        increaseScore(linesCleared);
        totalLinesCleared += linesCleared;
    }

    private void increaseScore(int linesCleared) {
        currentPoints += linesCleared * POINTS_PER_LINE * linesCleared;
    }

    private void increaseLevelIfNeeded() {
        int linesNeededForNextLevel = currentLevel * LINES_NEEDED_MULTIPLIER;
        if (totalLinesCleared >= linesNeededForNextLevel) {
            increaseLevel();
        }
    }

    private void increaseLevel() {
        ++currentLevel;
        gameSpeed -= SPEED_CHANGE;
        double gameSpeedInSeconds = ((double) gameSpeed) / 1000;
        log.debug(String.format("Level increased to %s (%ss per tick)", currentLevel, gameSpeedInSeconds));
    }

    private void updateActiveTetrimino() {
        try {
            moveActiveTetrimino(new Vector2f(0, -1));
        } catch (InvalidMoveException e) {
            finalizeActiveTetrimino();
        }
    }

    private void finalizeActiveTetrimino() {
        log.debug("Finalize active tetrimino");
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
        log.debug("Activating next tetrimino");
        if (well.isPositionAllowed(nextTetrimino)) {
            activeTetrimino = nextTetrimino;
            nextTetrimino = factory.getRandomTetrimino();
        } else {
            gameOver();
        }
    }

    public void moveActiveTetrimino(Vector2f nextPosition) {
        log.debug("Moving active tetrimino");
        Tetrimino movedStub = factory.getMovedStub(activeTetrimino, nextPosition);
        if (well.isPositionAllowed(movedStub)) {
            activeTetrimino.setPosition(movedStub.getPosition());
        } else {
            throw new InvalidMoveException();
        }
    }

    public void rotateActiveTetrimino() {
        log.debug("Rotating active tetrimino");
        Tetrimino rotatedStub = factory.getRotatedStub(activeTetrimino);
        if (well.isPositionAllowed(rotatedStub)) {
            activeTetrimino.setOrientation(rotatedStub.getOrientation());
        }
    }

    public void dropActiveTetrimino() {
        log.debug("Dropping tetrimino");
        Vector2f nextPosition = new Vector2f(0, -1);
        Tetrimino movedStub = factory.getMovedStub(activeTetrimino, nextPosition);
        while (well.isPositionAllowed(movedStub)) {
            activeTetrimino.setPosition(movedStub.getPosition());
            movedStub = factory.getMovedStub(activeTetrimino, nextPosition);
        }
    }

    @Override
    public void cleanup() {
        log.debug("Cleaning up");
        input.clearBindings();
        eventBus.unregister(inputListener);
    }

    private class InputListener {
        @Subscribe
        public void listenForMove(MoveEvent event) {
            log.debug("MoveEvent received");
            if (event.getDirection() == MoveEvent.Direction.Left) {
                moveActiveTetrimino(new Vector2f(-1, 0));
            } else if (event.getDirection() == MoveEvent.Direction.Right) {
                moveActiveTetrimino(new Vector2f(1, 0));
            } else if (event.getDirection() == MoveEvent.Direction.Down) {
                moveActiveTetrimino(new Vector2f(0, -1));
            } else {
                log.warn("Unknown move event: " + event.getDirection().name());
            }
        }

        @Subscribe
        public void listenForRotate(RotateEvent event) {
            log.debug("RotateEvent received");
            rotateActiveTetrimino();
        }

        @Subscribe
        public void listenForDrop(DropEvent event) {
            log.debug("DropEvent received");
            dropActiveTetrimino();
        }
    }
}
