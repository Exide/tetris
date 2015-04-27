package org.arabellan.tetris.scenes;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.domain.InvalidMoveException;
import org.arabellan.tetris.domain.Tetrimino;
import org.arabellan.tetris.domain.TetriminoFactory;
import org.arabellan.tetris.domain.Well;
import org.arabellan.tetris.events.ChangeSceneEvent;
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
        render();
    }

    @Override
    public void update() {
        long delta = Duration.between(lastUpdate, Instant.now()).toMillis();
        if (delta >= TIME_STEP_IN_MS) {
            log.debug("Tick!");
            updateActiveTetrimino();
            render();
            lastUpdate = Instant.now();
        }
    }

    private void render() {
        log.debug("\n" + getVisibleComponents());
    }

    private String getVisibleComponents() {
        int[][] matrix = matrixCopy(well.getMatrix());
        int x = ((int) activeTetrimino.getPosition().getX());
        int y = ((int) activeTetrimino.getPosition().getY());
        matrix[y][x] = 3;
        return matrixToString(matrix);
    }

    private int[][] matrixCopy(int[][] original) {
        int[][] copy = matrixCreateEmpty(original);
        matrixCopyData(original, copy);
        return copy;
    }

    private int[][] matrixCreateEmpty(int[][] original) {
        int rows = matrixGetRows(original);
        int columns = matrixGetColumns(original);
        return new int[rows][columns];
    }

    private void matrixCopyData(int[][] original, int[][] copy) {
        for (int row = 0; row < original.length; ++row) {
            for (int column = 0; column < original[row].length; ++column) {
                copy[row][column] = original[row][column];
            }
        }
    }

    private int matrixGetColumns(int[][] original) {
        return matrixGetColumns(original[0]);
    }

    private int matrixGetColumns(int[] row) {
        return row.length;
    }

    private int matrixGetRows(int[][] original) {
        return original.length;
    }

    public String matrixToString(int[][] matrix) {
        StringBuilder sb = new StringBuilder();

        for (int[] row : matrix) {
            sb.append(arrayToString(row)).append("\n");
        }

        return sb.toString();
    }

    private String arrayToString(int[] row) {
        StringBuilder sb = new StringBuilder();

        for (int cell : row) {
            sb.append(getSymbol(cell)).append(" ");
        }

        return sb.toString();
    }

    private char getSymbol(int symbol) {
        switch (symbol) {
            case 0:
                return '.';
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                return 'O';
            case 8:
                return ' ';
            case 9:
                return 'X';
            default:
                return '?';
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
