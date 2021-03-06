package org.arabellan.tetris.scenes;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.common.Image;
import org.arabellan.lwjgl.GLRenderer;
import org.arabellan.lwjgl.Shader;
import org.arabellan.lwjgl.ShaderProgram;
import org.arabellan.lwjgl.Transform;
import org.arabellan.tetris.Controller;
import org.arabellan.tetris.Controller.Key;
import org.arabellan.tetris.Renderable;
import org.arabellan.tetris.domain.BlockMatrix;
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
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.arabellan.lwjgl.GLException.throwIfError;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * This class is responsible for the logic during the game.
 */
@Slf4j
public class InGameScene implements Scene {

    private static final long STARTING_SPEED = TimeUnit.MILLISECONDS.toMillis(1000);
    private static final long SPEED_CHANGE = TimeUnit.MILLISECONDS.toMillis(100);
    private static final int LINES_NEEDED_MULTIPLIER = 10;
    private static final int POINTS_PER_LINE = 100;

    private static final int BLOCK_SIZE = 20;

    private int totalLinesCleared;
    private long currentPoints;
    private int currentLevel;
    private long gameSpeed;
    private Instant lastUpdate = Instant.now();

    private Renderable block;

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

    @Inject
    private GLRenderer renderer;

    @Override
    public void initialize() {
        log.debug("Initializing");
        initializeInput();
        initializeState();
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

    private void initializeState() {
        block = createBlock();
        well = new Well();
        activeTetrimino = factory.getRandomTetrimino();
        nextTetrimino = factory.getRandomTetrimino();
        gameSpeed = STARTING_SPEED;
        currentPoints = 0;
        currentLevel = 1;
    }

    private Renderable createBlock() {
        int[] indices = {
                0, 1, 2,    // 1 0  counter
                2, 1, 3     // 3 2  clockwise
        };

        float[] vertices = {
                +1f, +1f, +0f,    // top right
                -1f, +1f, +0f,    // top left
                +1f, -1f, +0f,    // bottom right
                -1f, -1f, +0f     // bottom left
        };

        float[] texcoords = {
                1f, 0f,     // top right
                0f, 0f,     // top left
                1f, 1f,     // bottom right
                0f, 1f      // bottom left
        };

        Image image = new Image("assets/images/block.bmp");

        int vertexArray = glGenVertexArrays();
        throwIfError();

        glBindVertexArray(vertexArray);
        throwIfError();

        Shader vertex = new Shader("assets/shaders/vertex.glsl", GL_VERTEX_SHADER);
        Shader fragment = new Shader("assets/shaders/fragment.glsl", GL_FRAGMENT_SHADER);
        ShaderProgram shader = new ShaderProgram(Arrays.asList(vertex, fragment));

        glUseProgram(shader.getId());
        throwIfError();

        renderer.defineElements(indices);

        renderer.defineArray(vertices);
        shader.setAttribute("position", 3);

        renderer.defineArray(texcoords);
        shader.setAttribute("texcoord", 2);

        renderer.defineTexture(image);
        shader.setUniform("image", 0);

        shader.setUniform("color", new Vector4f(1, 1, 1, 1));

        return Renderable.builder()
                .shader(shader)
                .vertexArray(vertexArray)
                .vertexCount(indices.length)
                .build();
    }

    @Override
    public List<Renderable> getRenderables() {
        return Stream.of(getWellRenderables(), getNextTetriminoRenderables())
                .flatMap(Function.identity())
                .collect(Collectors.toList());
    }

    private Stream<Renderable> getWellRenderables() {
        BlockMatrix matrix = well.getMatrix().copy();
        Vector2i dimensions = new Vector2i(matrix.width(), matrix.height());
        Vector2f position = new Vector2f(-100, 0);
        Vector2f offset = activeTetrimino.getPosition();

        return matrix
                .add(activeTetrimino.getMatrix(), offset)
                .stream()
                .filter(cell -> cell.getValue() != 0)
                .map(cell -> Renderable.builder()
                        .shader(block.getShader())
                        .color(convertCellValueToColor(cell.getValue()))
                        .vertexArray(block.getVertexArray())
                        .vertexCount(block.getVertexCount())
                        .transform(Transform.builder()
                                .position(getWorldPositionForBlock(cell.getIndex(), position, dimensions))
                                .scale(new Vector2f(BLOCK_SIZE / 2, BLOCK_SIZE / 2))
                                .build())
                        .build());
    }

    private Stream<Renderable> getNextTetriminoRenderables() {
        BlockMatrix matrix = new BlockMatrix(new Integer[][]{
                {1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1}});
        Vector2i dimensions = new Vector2i(matrix.width(), matrix.height());
        Vector2f position = new Vector2f(150, 140);
        Vector2f offset = new Vector2f(1, 2);

        return matrix
                .add(nextTetrimino.getMatrix(), offset)
                .stream()
                .filter(cell -> cell.getValue() != 0)
                .map(cell -> Renderable.builder()
                        .shader(block.getShader())
                        .color(convertCellValueToColor(cell.getValue()))
                        .vertexArray(block.getVertexArray())
                        .vertexCount(block.getVertexCount())
                        .transform(Transform.builder()
                                .position(getWorldPositionForBlock(cell.getIndex(), position, dimensions))
                                .scale(new Vector2f(BLOCK_SIZE / 2, BLOCK_SIZE / 2))
                                .build())
                        .build());
    }

    private Vector4f convertCellValueToColor(int value) {
        switch (value) {
            case 1:
                return new Vector4f(0.5f, 0.5f, 0.5f, 1f);  // grey
            case 2:
                return new Vector4f(1.0f, 0.0f, 0.0f, 1f);  // red
            case 3:
                return new Vector4f(1.0f, 1.0f, 1.0f, 1f);  // white
            case 4:
                return new Vector4f(0.5f, 0.0f, 1.0f, 1f);  // purple
            case 5:
                return new Vector4f(0.0f, 0.0f, 1.0f, 1f);  // blue
            case 6:
                return new Vector4f(0.0f, 1.0f, 0.0f, 1f);  // green
            case 7:
                return new Vector4f(1.5f, 0.5f, 0.0f, 1f);  // orange
            case 8:
                return new Vector4f(1.0f, 1.0f, 0.0f, 1f);  // yellow
            default:
                throw new IllegalArgumentException("Unknown cell value: " + value);
        }
    }

    private Vector2f getWorldPositionForBlock(int i, Vector2f anchor, Vector2i dimensions) {
        float blockWidth = BLOCK_SIZE;
        float blockHeight = BLOCK_SIZE;
        int row = i / dimensions.x;
        int column = i - (row * dimensions.x);
        float left = anchor.x - (dimensions.x / 2 * blockWidth);
        float top = anchor.y + (dimensions.y / 2 * blockHeight);
        float x = left + (column * blockWidth) + (blockWidth / 2);
        float y = top - (row * blockHeight) - (blockHeight / 2);
        return new Vector2f(x, y);
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
        log.debug(String.format("Score increased to %s", currentPoints));
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
            moveActiveTetrimino(new Vector2f(0, 1));
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

    private void moveActiveTetrimino(Vector2f nextPosition) {
        Tetrimino movedStub = factory.getMovedStub(activeTetrimino, nextPosition);
        if (well.isPositionAllowed(movedStub)) {
            log.debug("Moving active tetrimino");
            activeTetrimino.setPosition(movedStub.getPosition());
        } else {
            throw new InvalidMoveException();
        }
    }

    private void rotateActiveTetrimino() {
        log.debug("Rotating active tetrimino");
        Tetrimino rotatedStub = factory.getRotatedStub(activeTetrimino);
        if (well.isPositionAllowed(rotatedStub)) {
            activeTetrimino.setOrientation(rotatedStub.getOrientation());
        }
    }

    private void dropActiveTetrimino() {
        log.debug("Dropping tetrimino");
        Vector2f nextPosition = new Vector2f(0, 1);
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
            try {
                log.debug("MoveEvent received");
                if (event.getDirection() == MoveEvent.Direction.Left) {
                    moveActiveTetrimino(new Vector2f(-1, 0));
                } else if (event.getDirection() == MoveEvent.Direction.Right) {
                    moveActiveTetrimino(new Vector2f(1, 0));
                } else if (event.getDirection() == MoveEvent.Direction.Down) {
                    moveActiveTetrimino(new Vector2f(0, 1));
                } else {
                    log.warn("Unknown move event: " + event.getDirection().name());
                }
            } catch (InvalidMoveException e) {
                log.debug("Ignoring invalid movement");
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
