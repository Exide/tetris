package org.arabellan.tetris.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;

@Builder
@Getter
public class Tetrimino {

    private static final BlockMatrix O_ALL = new BlockMatrix(new Integer[][]{
            {0, 0, 5, 5, 0},
            {0, 0, 5, 5, 0}
    });

    private static final BlockMatrix I_VERTICAL = new BlockMatrix(new Integer[][]{
            {0, 0, 2, 0, 0},
            {0, 0, 2, 0, 0},
            {0, 0, 2, 0, 0},
            {0, 0, 2, 0, 0}
    });

    private static final BlockMatrix I_HORIZONTAL = new BlockMatrix(new Integer[][]{
            {0, 2, 2, 2, 2}
    });

    private static final BlockMatrix S_HORIZONTAL = new BlockMatrix(new Integer[][]{
            {0, 0, 6, 6, 0},
            {0, 6, 6, 0, 0},
            {0, 0, 0, 0, 0}
    });

    private static final BlockMatrix S_VERTICAL = new BlockMatrix(new Integer[][]{
            {0, 6, 0, 0, 0},
            {0, 6, 6, 0, 0},
            {0, 0, 6, 0, 0}
    });

    private static final BlockMatrix Z_HORIZONTAL = new BlockMatrix(new Integer[][]{
            {0, 8, 8, 0, 0},
            {0, 0, 8, 8, 0},
            {0, 0, 0, 0, 0}
    });

    private static final BlockMatrix Z_VERTICAL = new BlockMatrix(new Integer[][]{
            {0, 0, 0, 8, 0},
            {0, 0, 8, 8, 0},
            {0, 0, 8, 0, 0}
    });

    private static final BlockMatrix J_UP = new BlockMatrix(new Integer[][]{
            {0, 0, 3, 0, 0},
            {0, 0, 3, 0, 0},
            {0, 3, 3, 0, 0},
    });

    private static final BlockMatrix J_DOWN = new BlockMatrix(new Integer[][]{
            {0, 0, 3, 3, 0},
            {0, 0, 3, 0, 0},
            {0, 0, 3, 0, 0},
    });

    private static final BlockMatrix J_LEFT = new BlockMatrix(new Integer[][]{
            {0, 0, 0, 0, 0},
            {0, 3, 3, 3, 0},
            {0, 0, 0, 3, 0},
    });

    private static final BlockMatrix J_RIGHT = new BlockMatrix(new Integer[][]{
            {0, 3, 0, 0, 0},
            {0, 3, 3, 3, 0},
    });

    private static final BlockMatrix L_UP = new BlockMatrix(new Integer[][]{
            {0, 0, 4, 0, 0},
            {0, 0, 4, 0, 0},
            {0, 0, 4, 4, 0},
    });

    private static final BlockMatrix L_DOWN = new BlockMatrix(new Integer[][]{
            {0, 4, 4, 0, 0},
            {0, 0, 4, 0, 0},
            {0, 0, 4, 0, 0},
    });

    private static final BlockMatrix L_LEFT = new BlockMatrix(new Integer[][]{
            {0, 0, 0, 0, 0},
            {0, 4, 4, 4, 0},
            {0, 4, 0, 0, 0},
    });

    private static final BlockMatrix L_RIGHT = new BlockMatrix(new Integer[][]{
            {0, 0, 0, 4, 0},
            {0, 4, 4, 4, 0},
    });

    private static final BlockMatrix T_UP = new BlockMatrix(new Integer[][]{
            {0, 0, 0, 0, 0},
            {0, 0, 7, 0, 0},
            {0, 7, 7, 7, 0}
    });

    private static final BlockMatrix T_DOWN = new BlockMatrix(new Integer[][]{
            {0, 7, 7, 7, 0},
            {0, 0, 7, 0, 0},
            {0, 0, 0, 0, 0}
    });

    private static final BlockMatrix T_LEFT = new BlockMatrix(new Integer[][]{
            {0, 0, 0, 7, 0},
            {0, 0, 7, 7, 0},
            {0, 0, 0, 7, 0}
    });

    private static final BlockMatrix T_RIGHT = new BlockMatrix(new Integer[][]{
            {0, 7, 0, 0, 0},
            {0, 7, 7, 0, 0},
            {0, 7, 0, 0, 0}
    });

    Type type;
    @Setter
    Vector2f position;
    @Setter
    Orientation orientation;

    public BlockMatrix getMatrix() {
        switch (orientation) {
            case UP:
                return getShapeFacingUp();
            case DOWN:
                return getShapeFacingDown();
            case LEFT:
                return getShapeFacingLeft();
            case RIGHT:
                return getShapeFacingRight();
            default:
                throw new IllegalArgumentException("Invalid orientation: " + orientation.name());
        }
    }

    private BlockMatrix getShapeFacingUp() {
        switch (type) {
            case I:
                return I_VERTICAL;
            case J:
                return J_UP;
            case L:
                return L_UP;
            case O:
                return O_ALL;
            case S:
                return S_HORIZONTAL;
            case T:
                return T_UP;
            case Z:
                return Z_HORIZONTAL;
            default:
                throw new IllegalArgumentException("Invalid type: " + type.name());
        }
    }

    private BlockMatrix getShapeFacingDown() {
        switch (type) {
            case I:
                return I_VERTICAL;
            case J:
                return J_DOWN;
            case L:
                return L_DOWN;
            case O:
                return O_ALL;
            case S:
                return S_HORIZONTAL;
            case T:
                return T_DOWN;
            case Z:
                return Z_HORIZONTAL;
            default:
                throw new IllegalArgumentException("Invalid type: " + type.name());
        }
    }

    private BlockMatrix getShapeFacingLeft() {
        switch (type) {
            case I:
                return I_HORIZONTAL;
            case J:
                return J_LEFT;
            case L:
                return L_LEFT;
            case O:
                return O_ALL;
            case S:
                return S_VERTICAL;
            case T:
                return T_LEFT;
            case Z:
                return Z_VERTICAL;
            default:
                throw new IllegalArgumentException("Invalid type: " + type.name());
        }
    }

    private BlockMatrix getShapeFacingRight() {
        switch (type) {
            case I:
                return I_HORIZONTAL;
            case J:
                return J_RIGHT;
            case L:
                return L_RIGHT;
            case O:
                return O_ALL;
            case S:
                return S_VERTICAL;
            case T:
                return T_RIGHT;
            case Z:
                return Z_VERTICAL;
            default:
                throw new IllegalArgumentException("Invalid type: " + type.name());
        }
    }

    enum Type {I, J, L, O, S, T, Z}

    enum Orientation {UP, RIGHT, DOWN, LEFT}
}
