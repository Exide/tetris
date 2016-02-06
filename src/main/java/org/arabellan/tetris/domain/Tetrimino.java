package org.arabellan.tetris.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.arabellan.common.Color;
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
                        {1},
                        {1},
                        {1},
                        {1}
                });
            case J:
                return new BlockMatrix(new Integer[][]{
                        {0, 1},
                        {0, 1},
                        {1, 1}
                });
            case L:
                return new BlockMatrix(new Integer[][]{
                        {1, 0},
                        {1, 0},
                        {1, 1}
                });
            case O:
                return new BlockMatrix(new Integer[][]{
                        {1, 1},
                        {1, 1}
                });
            case S:
                return new BlockMatrix(new Integer[][]{
                        {0, 1, 1},
                        {1, 1, 0}
                });
            case T:
                return new BlockMatrix(new Integer[][]{
                        {0, 1, 0},
                        {1, 1, 1}
                });
            case Z:
                return new BlockMatrix(new Integer[][]{
                        {1, 1, 0},
                        {0, 1, 1}
                });
            default:
                throw new RuntimeException("Unknown Tetrimino type!");
        }
    }
}
