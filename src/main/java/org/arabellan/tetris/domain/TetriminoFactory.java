package org.arabellan.tetris.domain;

import org.arabellan.tetris.domain.Tetrimino.Orientation;
import org.joml.Vector2f;

import java.util.Random;

import static org.arabellan.tetris.domain.Tetrimino.Type;

public class TetriminoFactory {

    private Random random = new Random();

    public Tetrimino getRandomTetrimino() {
        Type type = getRandomType();
        Vector2f position = new Vector2f(3, 0);

        return Tetrimino.builder()
                .type(type)
                .position(position)
                .orientation(Orientation.UP)
                .build();
    }

    private Type getRandomType() {
        Type[] types = Type.values();
        int randomType = random.nextInt(types.length);
        return types[randomType];
    }

    public Tetrimino getMovedStub(Tetrimino tetrimino, Vector2f positionChange) {
        float x = tetrimino.getPosition().x + positionChange.x;
        float y = tetrimino.getPosition().y + positionChange.y;
        return Tetrimino.builder()
                .type(tetrimino.getType())
                .position(new Vector2f(x, y))
                .orientation(tetrimino.getOrientation())
                .build();
    }

    public Tetrimino getRotatedStub(Tetrimino tetrimino) {
        return Tetrimino.builder()
                .type(tetrimino.getType())
                .orientation(getNextOrientation(tetrimino))
                .position(tetrimino.getPosition())
                .build();
    }

    private Orientation getNextOrientation(Tetrimino tetrimino) {
        switch (tetrimino.getOrientation()) {
            case UP:
                return Orientation.RIGHT;
            case RIGHT:
                return Orientation.DOWN;
            case DOWN:
                return Orientation.LEFT;
            case LEFT:
                return Orientation.UP;
            default:
                throw new RuntimeException("Unknown orientation!");
        }
    }
}
