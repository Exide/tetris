package org.arabellan.tetris.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.common.Matrix;
import org.arabellan.tetris.Renderable;
import org.joml.Vector2f;

@Slf4j
public class Well {

    Matrix<Integer> grid = new Matrix<>(new Integer[][]{
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
    });

    @Getter
    Renderable renderable;

    public void initialize() {
        Vector2f position = new Vector2f();
        renderable = Renderable.builder()
                .position(position)
                .matrix(grid)
                .build();
    }

    public void add(Tetrimino tetrimino) throws InvalidMoveException {
        log.debug("Adding " + tetrimino.getType());
        if (isPositionAllowed(tetrimino)) {
            grid.add(tetrimino.getMatrix(), tetrimino.getPosition());
        } else {
            throw new InvalidMoveException();
        }
    }

    public boolean isPositionAllowed(Tetrimino tetrimino) {
        return !isOverlapping(tetrimino.getMatrix(), tetrimino.getPosition());
    }

    public boolean isOverlapping(Matrix<Integer> matrix, Vector2f offset) {
        int x = (int) offset.x;
        int y = (int) offset.y;

        for (int row = 0; row < matrix.height(); ++row) {
            for (int column = 0; column < matrix.width(); ++column) {
                int a = grid.getData()[row + y][column + x];
                int b = matrix.getData()[row][column];
                if (a != 0 && b != 0) {
                    return true;
                }
            }
        }

        return false;
    }
}
