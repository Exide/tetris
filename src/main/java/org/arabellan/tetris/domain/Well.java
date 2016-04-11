package org.arabellan.tetris.domain;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class Well {

    private BlockMatrix grid = new BlockMatrix(new Integer[][]{
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
    });

    public BlockMatrix getMatrix() {
        return grid;
    }

    public void add(Tetrimino tetrimino) {
        log.debug("Adding " + tetrimino.getType());
        grid.add(tetrimino.getMatrix(), tetrimino.getPosition());
    }

    public boolean isPositionAllowed(Tetrimino tetrimino) {
        int x = (int) tetrimino.getPosition().x;
        int y = (int) tetrimino.getPosition().y;
        BlockMatrix matrix = tetrimino.getMatrix();

        for (int row = 0; row < matrix.height(); ++row) {
            for (int column = 0; column < matrix.width(); ++column) {
                int matrixCell = matrix.getData()[row][column];
                if (matrixCell == 0) continue;

                int gridCell = grid.getData()[row + y][column + x];
                if (gridCell == 0) continue;

                return false;
            }
        }

        return true;
    }

    public int clearCompleteRows() {
        int rowsCleared = 0;
        for (int row = 0; row < grid.height() - 1; ++row) {
            if (isRowComplete(row)) {
                clearRow(row);
                ++rowsCleared;
            }
        }
        return rowsCleared;
    }

    private boolean isRowComplete(int row) {
        return !Arrays.asList(grid.getData()[row]).stream()
                .anyMatch(value -> value == 0);
    }

    private void clearRow(Integer row) {
        for (; row > 0; --row) {
            grid.getData()[row] = Arrays.copyOf(grid.getData()[row - 1], grid.getData()[row - 1].length);
        }
    }
}
