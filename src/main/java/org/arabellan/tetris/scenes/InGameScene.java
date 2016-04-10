package org.arabellan.tetris.scenes;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.common.Image;
import org.arabellan.lwjgl.Shader;
import org.arabellan.lwjgl.ShaderProgram;
import org.arabellan.lwjgl.Transform;
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
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.arabellan.lwjgl.GLException.throwIfError;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
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
        // build a vertex array
        int vertexArray = glGenVertexArrays();
        throwIfError();

        glBindVertexArray(vertexArray);
        throwIfError();

        // build the shader
        Shader vertex = new Shader("assets/shaders/vertex.glsl", GL_VERTEX_SHADER);
        Shader fragment = new Shader("assets/shaders/fragment.glsl", GL_FRAGMENT_SHADER);
        ShaderProgram shader = new ShaderProgram(Arrays.asList(vertex, fragment));

        // enable the shader for this vertex array
        glUseProgram(shader.getId());
        throwIfError();

        // define elements
        IntBuffer elements = BufferUtils.createIntBuffer(6);
        elements.put(new int[]{
                0, 1, 2,    // 1 0  counter
                2, 1, 3     // 3 2  clockwise
        }).flip();

        // push the elements to the gpu
        int elementBuffer = glGenBuffers();
        throwIfError();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBuffer);
        throwIfError();

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);
        throwIfError();

        // define vertices
        int vertexCount = 12;
        FloatBuffer vertices = BufferUtils.createFloatBuffer(vertexCount);
        vertices.put(new float[]{
                +1f, +1f, +0f,    // top right
                -1f, +1f, +0f,    // top left
                +1f, -1f, +0f,    // bottom right
                -1f, -1f, +0f     // bottom left
        });
        vertices.flip();

        // push the vertices to the gpu
        int vertexBuffer = glGenBuffers();
        throwIfError();

        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        throwIfError();

        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        throwIfError();

        // tell the shader how to read vertices
        shader.setAttribute("position", 3);

        // define texture coordinates
        FloatBuffer texcoords = BufferUtils.createFloatBuffer(12);
        texcoords.put(new float[]{
                1f, 0f,     // top right
                0f, 0f,     // top left
                1f, 1f,     // bottom right
                0f, 1f      // bottom left
        });
        texcoords.flip();

        // push the texture coordinates to the gpu
        int texcoordBuffer = glGenBuffers();
        throwIfError();

        glBindBuffer(GL_ARRAY_BUFFER, texcoordBuffer);
        throwIfError();

        glBufferData(GL_ARRAY_BUFFER, texcoords, GL_STATIC_DRAW);
        throwIfError();

        // tell the shader how to read texture coordinates
        shader.setAttribute("texcoord", 2);

        // define the default block color
        Vector4f color = new Vector4f(1f, 1f, 1f, 1f);

        // tell the shader how to read color components
        shader.setUniform("color", color);

        // define the texture
        Image image = new Image("assets/images/block.bmp");

        // push the texture to the gpu
        int textureBuffer = glGenTextures();
        throwIfError();

        glBindTexture(GL_TEXTURE_2D, textureBuffer);
        throwIfError();

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, image.getWidth(), image.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, image.getPixels());
        throwIfError();

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        throwIfError();

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        throwIfError();

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        throwIfError();

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        throwIfError();

        // tell the shader how to read the texture
        shader.setUniform("image", 0);

        // finally bundle up the pieces needed for the renderer
        return Renderable.builder()
                .shader(shader)
                .vertexArray(vertexArray)
                .vertexCount(vertexCount)
                .build();
    }

    @Override
    public List<Renderable> getRenderables() {
        return well.getMatrix()
                .copy()
                .add(activeTetrimino.getMatrix(), activeTetrimino.getPosition())
                .stream()
                .filter(cell -> cell.getValue() != 0)
                .map(cell -> Renderable.builder()
                        .shader(block.getShader())
                        .color(convertCellValueToColor(cell.getValue()))
                        .vertexArray(block.getVertexArray())
                        .vertexCount(block.getVertexCount())
                        .transform(Transform.builder()
                                .position(getWorldPositionForBlock(cell.getIndex()))
                                .scale(new Vector2f(BLOCK_SIZE / 2, BLOCK_SIZE / 2))
                                .build())
                        .build())
                .collect(Collectors.toList());
    }

    private Vector4f convertCellValueToColor(int value) {
        switch (value) {
            case 1:
                return new Vector4f(0.4f, 0.4f, 0.4f, 1f);  // dark grey
            case 2:
                return new Vector4f(0.5f, 0.0f, 0.0f, 1f);  // dark red
            case 3:
                return new Vector4f(0.8f, 0.8f, 0.8f, 1f);  // light grey
            case 4:
                return new Vector4f(0.5f, 0.0f, 0.5f, 1f);  // dark magenta
            case 5:
                return new Vector4f(0.0f, 0.0f, 0.5f, 1f);  // dark blue
            case 6:
                return new Vector4f(0.0f, 0.5f, 0.0f, 1f);  // dark green
            case 7:
                return new Vector4f(0.5f, 0.5f, 0.0f, 1f);  // dark brown
            case 8:
                return new Vector4f(0.0f, 0.5f, 0.5f, 1f);  // dark cyan
            default:
                throw new IllegalArgumentException("Unknown cell value: " + value);
        }
    }

    private Vector2f getWorldPositionForBlock(int i) {
        float wellX = 0;
        float wellY = 0;
        int wellWidth = 12;
        int wellHeight = 22;
        float blockWidth = BLOCK_SIZE;
        float blockHeight = BLOCK_SIZE;
        int row = i / wellWidth;
        int column = i - (row * wellWidth);
        float left = wellX - (wellWidth / 2 * blockWidth);
        float top = wellY + (wellHeight / 2 * blockHeight);
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
            try {
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
