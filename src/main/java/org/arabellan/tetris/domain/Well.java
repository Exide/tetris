package org.arabellan.tetris.domain;

import lombok.extern.slf4j.Slf4j;
import org.joml.Vector2f;

@Slf4j
public class Well {

    BlockMatrix grid = new BlockMatrix(new Integer[][]{
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

    public BlockMatrix getMatrix() {
        return grid;
    }

    public void add(Tetrimino tetrimino) {
        log.debug("Adding " + tetrimino.getType());
        grid.add(tetrimino.getMatrix(), tetrimino.getPosition());
    }

    public boolean isPositionAllowed(Tetrimino tetrimino) {
        Vector2f position = invertYAxis(tetrimino.getPosition());
        return !isOverlapping(tetrimino.getMatrix(), position);
    }

    private Vector2f invertYAxis(Vector2f position) {
        return new Vector2f(position.x, -position.y);
    }

    public boolean isOverlapping(BlockMatrix matrix, Vector2f offset) {
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
