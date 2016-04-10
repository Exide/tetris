package org.arabellan.tetris.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;

@Builder
@Getter
public class Tetrimino {

    Type type;
    @Setter
    Vector2f position;
    @Setter
    Orientation orientation;

    public BlockMatrix getMatrix() {
        BlockMatrix shape = getShape();
        return correctForOrientation(shape);
    }

    private BlockMatrix correctForOrientation(BlockMatrix shape) {
        for (int i = 0; i < orientation.ordinal(); ++i) {
            shape = rotateShape(shape);
        }
        return shape;
    }

    private BlockMatrix rotateShape(BlockMatrix shape) {
        Integer[][] data = shape.getData();
        final int M = data.length;
        final int N = data[0].length;
        Integer[][] output = new Integer[N][M];
        for (int r = 0; r < M; r++) {
            for (int c = 0; c < N; c++) {
                output[c][M - 1 - r] = data[r][c];
            }
        }
        return new BlockMatrix(output);
    }

    private BlockMatrix getShape() {
        switch (type) {
            case I:
                return new BlockMatrix(new Integer[][]{
                        {2},
                        {2},
                        {2},
                        {2}
                });
            case J:
                return new BlockMatrix(new Integer[][]{
                        {0, 3},
                        {0, 3},
                        {3, 3}
                });
            case L:
                return new BlockMatrix(new Integer[][]{
                        {4, 0},
                        {4, 0},
                        {4, 4}
                });
            case O:
                return new BlockMatrix(new Integer[][]{
                        {5, 5},
                        {5, 5}
                });
            case S:
                return new BlockMatrix(new Integer[][]{
                        {0, 6, 6},
                        {6, 6, 0}
                });
            case T:
                return new BlockMatrix(new Integer[][]{
                        {0, 7, 0},
                        {7, 7, 7}
                });
            case Z:
                return new BlockMatrix(new Integer[][]{
                        {8, 8, 0},
                        {0, 8, 8}
                });
            default:
                throw new IllegalArgumentException("Unknown tetrimino type");
        }
    }

    enum Type {I, J, L, O, S, T, Z}

    enum Orientation {UP, RIGHT, DOWN, LEFT}
}
