package org.arabellan.tetris;

import org.arabellan.common.Coord;
import org.arabellan.common.Matrix;

public interface Renderable {
    Matrix<Character> getRenderable();
    Coord getPosition();
}
