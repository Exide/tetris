package org.arabellan.tetris.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.common.Coord;
import org.arabellan.tetris.Renderable;

@Slf4j
public class Well implements Renderable {

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

    public Coord getPosition() {
        return Coord.builder().build();
    }

    public int[][] getRenderable() {
        return matrix;
    }

    public Renderable.Type getTypeOfRenderable() {
        return Renderable.Type.WELL;
    }

    public void add(Tetrimino tetrimino) throws InvalidMoveException {
        log.debug("Adding " + tetrimino.getType());
        int x = (int) tetrimino.getPosition().getX();
        int y = (int) tetrimino.getPosition().getY();
        if (isPositionAllowed(tetrimino)) {
            matrix[y][x] = 3;
        } else {
            throw new InvalidMoveException();
        }
    }

    public boolean isPositionAllowed(Tetrimino tetrimino) {
        int x = (int) tetrimino.getPosition().getX();
        int y = (int) tetrimino.getPosition().getY();
        return (matrix[y][x] == 0);
    }
}
