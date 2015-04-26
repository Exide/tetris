package org.arabellan.tetris.domain;

import org.arabellan.common.Color;
import org.arabellan.common.Coord;

import java.util.Random;

import static org.arabellan.tetris.domain.Tetrimino.Type;

public class TetriminoFactory {

    Random random = new Random();

    public Tetrimino getRandomTetrimino() {
        Type type = getRandomType();
        Color color = getColor(type);
        int[][] shape = getShape(type);
        Coord position = Coord.builder().x(3).y(0).build();

        return Tetrimino.builder()
                .type(type)
                .color(color)
                .shape(shape)
                .position(position)
                .build();
    }

    private Type getRandomType() {
        Type[] types = Type.values();
        int randomType = random.nextInt(types.length);
        return types[randomType];
    }

    private Color getColor(Type type) {
        switch (type) {
            case I:
                return Color.builder().r(0.5f).g(0.0f).b(0.0f).build(); // dark red
            case J:
                return Color.builder().r(0.8f).g(0.8f).b(0.8f).build(); // light grey
            case L:
                return Color.builder().r(0.5f).g(0.0f).b(0.5f).build(); // dark magenta
            case O:
                return Color.builder().r(0.0f).g(0.0f).b(0.5f).build(); // dark blue
            case S:
                return Color.builder().r(0.0f).g(0.5f).b(0.0f).build(); // dark green
            case T:
                return Color.builder().r(0.5f).g(0.5f).b(0.0f).build(); // dark brown
            case Z:
                return Color.builder().r(0.0f).g(0.5f).b(0.5f).build(); // dark cyan
            default:
                throw new RuntimeException("Unknown Tetrimino type!");
        }
    }

    private int[][] getShape(Type type) {
        switch (type) {
            case I:
                return new int[][]{{1, 1, 1, 1}};
            case J:
                return new int[][]{{1, 1, 1},{0, 0, 1}};
            case L:
                return new int[][]{{1, 1, 1},{1, 0, 0}};
            case O:
                return new int[][]{{1, 1},{1, 1}};
            case S:
                return new int[][]{{0, 1, 1},{1, 1, 0}};
            case T:
                return new int[][]{{1, 1, 1},{0, 1, 0}};
            case Z:
                return new int[][]{{1, 1, 0},{0, 1, 1}};
            default:
                throw new RuntimeException("Unknown Tetrimino type!");
        }
    }
}
