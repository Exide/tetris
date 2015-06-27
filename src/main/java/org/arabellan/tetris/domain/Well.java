package org.arabellan.tetris.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.common.Coord;
import org.arabellan.tetris.Renderable;

@Slf4j
public class Well implements Renderable {

    private static final int SPACE = 0;
    private static final int OCCUPIED = 9;

    @Getter
    int[][] matrix = new int[][]{
//            {WALL, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, WALL},
//            {WALL, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, WALL},
//            {WALL, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, WALL},
//            {WALL, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, WALL},
//            {WALL, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, WALL},
//            {WALL, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, WALL},
//            {WALL, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, WALL},
//            {WALL, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, WALL},
//            {WALL, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, WALL},
//            {WALL, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, WALL},
//            {WALL, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, WALL},
//            {WALL, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, WALL},
//            {WALL, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, WALL},
//            {WALL, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, WALL},
//            {WALL, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, WALL},
            {OCCUPIED, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, OCCUPIED},
            {OCCUPIED, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, OCCUPIED},
            {OCCUPIED, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, OCCUPIED},
            {OCCUPIED, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, OCCUPIED},
            {OCCUPIED, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, SPACE, OCCUPIED},
            {OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED}
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
            matrix[y][x] = OCCUPIED;
        } else {
            throw new InvalidMoveException();
        }
    }

    public boolean isPositionAllowed(Tetrimino tetrimino) {
        int x = (int) tetrimino.getPosition().getX();
        int y = (int) tetrimino.getPosition().getY();
        return (matrix[y][x] == SPACE);
    }
}
