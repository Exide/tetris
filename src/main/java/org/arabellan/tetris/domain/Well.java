package org.arabellan.tetris.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Well {

    @Getter
    int[][] matrix = new int[][]{
//            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
//            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
//            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
//            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
//            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
//            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
//            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
//            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
//            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
//            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
//            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
//            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
//            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
//            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
//            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
            {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9},
            {9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9}
    };

    public void add(Tetrimino tetrimino) {
        log.debug("Adding " + tetrimino.getType());
        int x = (int) tetrimino.getPosition().getX();
        int y = (int) tetrimino.getPosition().getY();
        matrix[y][x] = 3;
    }

    public boolean isPositionAllowed(Tetrimino tetrimino) {
        int x = (int) tetrimino.getPosition().getX();
        int y = (int) tetrimino.getPosition().getY();
        return (matrix[y][x] == 0);
    }
}
