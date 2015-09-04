package org.arabellan.tetris.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.arabellan.common.Color;
import org.arabellan.common.Matrix;
import org.arabellan.tetris.Renderable;
import org.joml.Vector2f;

@Builder
@Getter
public class Tetrimino {

    public enum Type {I, J, L, O, S, T, Z}

    public enum Orientation {UP, RIGHT, DOWN, LEFT}

    Color color;
    Type type;

    @Setter
    Vector2f position;

    @Setter
    Orientation orientation;

    public Matrix<Integer> getMatrix() {
        Matrix<Integer> shape = getShape();
        return correctForOrientation(shape);
    }

    private Matrix<Integer> correctForOrientation(Matrix<Integer> shape) {
        for (int i = 0; i < orientation.ordinal(); ++i) {
            shape = rotateShape(shape);
        }
        return shape;
    }

    private Matrix<Integer> rotateShape(Matrix<Integer> shape) {
        Integer[][] data = shape.getData();
        final int M = data.length;
        final int N = data[0].length;
        Integer[][] output = new Integer[N][M];
        for (int r = 0; r < M; r++) {
            for (int c = 0; c < N; c++) {
                output[c][M - 1 - r] = data[r][c];
            }
        }
        return new Matrix<>(output);
    }

    private Matrix<Integer> getShape() {
        switch (type) {
            case I:
                return new Matrix<>(new Integer[][]{
                        {1},
                        {1},
                        {1},
                        {1}
                });
            case J:
                return new Matrix<>(new Integer[][]{
                        {0, 1},
                        {0, 1},
                        {1, 1}
                });
            case L:
                return new Matrix<>(new Integer[][]{
                        {1, 0},
                        {1, 0},
                        {1, 1}
                });
            case O:
                return new Matrix<>(new Integer[][]{
                        {1, 1},
                        {1, 1}
                });
            case S:
                return new Matrix<>(new Integer[][]{
                        {0, 1, 1},
                        {1, 1, 0}
                });
            case T:
                return new Matrix<>(new Integer[][]{
                        {0, 1, 0},
                        {1, 1, 1}
                });
            case Z:
                return new Matrix<>(new Integer[][]{
                        {1, 1, 0},
                        {0, 1, 1}
                });
            default:
                throw new RuntimeException("Unknown Tetrimino type!");
        }
    }
}
