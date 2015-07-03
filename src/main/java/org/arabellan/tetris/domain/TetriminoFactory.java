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
        char[][] shape = getShape(type);
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

    private char[][] getShape(Type type) {
        switch (type) {
            case I:
                return new char[][] {
                        {'I'},
                        {'I'},
                        {'I'},
                        {'I'}
                };
            case J:
                return new char[][] {
                        {' ', 'J'},
                        {' ', 'J'},
                        {'J', 'J'}
                };
            case L:
                return new char[][] {
                        {'L', ' '},
                        {'L', ' '},
                        {'L', 'L'}
                };
            case O:
                return new char[][] {
                        {'O', 'O'},
                        {'O', 'O'}
                };
            case S:
                return new char[][] {
                        {' ', 'S', 'S'},
                        {'S', 'S', ' '}
                };
            case T:
                return new char[][] {
                        {' ', 'T', ' '},
                        {'T', 'T', 'T'}
                };
            case Z:
                return new char[][] {
                        {'Z', 'Z', ' '},
                        {' ', 'Z', 'Z'}
                };
            default:
                throw new RuntimeException("Unknown Tetrimino type!");
        }
    }
}
