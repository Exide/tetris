package org.arabellan.tetris.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.arabellan.common.Color;
import org.arabellan.common.Coord;
import org.arabellan.common.Matrix;
import org.arabellan.tetris.Renderable;

@Builder
@Getter
public class Tetrimino implements Renderable {

    public enum Type {I, J, L, O, S, T, Z}

    public enum Orientation {UP, RIGHT, DOWN, LEFT}

    Color color;
    Type type;

    @Setter
    Coord position;

    @Setter
    Orientation orientation;

    public Matrix<Character> getRenderable() {
        Matrix<Character> shape = getShape().replace(' ', null);
        return correctForOrientation(shape);
    }

    private Matrix<Character> correctForOrientation(Matrix<Character> shape) {
        for (int i = 0; i < orientation.ordinal(); ++i) {
            shape = rotateShape(shape);
        }
        return shape;
    }

    private Matrix<Character> rotateShape(Matrix<Character> shape) {
        Character[][] data = shape.getData();
        final int M = data.length;
        final int N = data[0].length;
        Character[][] output = new Character[N][M];
        for (int r = 0; r < M; r++) {
            for (int c = 0; c < N; c++) {
                output[c][M - 1 - r] = data[r][c];
            }
        }
        return new Matrix<>(output);
    }

    private Matrix<Character> getShape() {
        switch (type) {
            case I:
                return new Matrix<>(new Character[][]{
                        {'I'},
                        {'I'},
                        {'I'},
                        {'I'}
                });
            case J:
                return new Matrix<>(new Character[][]{
                        {' ', 'J'},
                        {' ', 'J'},
                        {'J', 'J'}
                });
            case L:
                return new Matrix<>(new Character[][]{
                        {'L', ' '},
                        {'L', ' '},
                        {'L', 'L'}
                });
            case O:
                return new Matrix<>(new Character[][]{
                        {'O', 'O'},
                        {'O', 'O'}
                });
            case S:
                return new Matrix<>(new Character[][]{
                        {' ', 'S', 'S'},
                        {'S', 'S', ' '}
                });
            case T:
                return new Matrix<>(new Character[][]{
                        {' ', 'T', ' '},
                        {'T', 'T', 'T'}
                });
            case Z:
                return new Matrix<>(new Character[][]{
                        {'Z', 'Z', ' '},
                        {' ', 'Z', 'Z'}
                });
            default:
                throw new RuntimeException("Unknown Tetrimino type!");
        }
    }
}
