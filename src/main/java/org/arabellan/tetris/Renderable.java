package org.arabellan.tetris;

import org.arabellan.common.Coord;

public interface Renderable {

    Type getTypeOfRenderable();
    Coord getPosition();
    char[][] getRenderable();

    enum Type { WELL, TETRIMINO, UI }
}
