package org.arabellan.tetris;

import org.arabellan.common.Coord;

public interface Renderable {

    Type getTypeOfRenderable();
    Coord getPosition();
    int[][] getRenderable();

    enum Type { WELL, TETRIMINO }
}
