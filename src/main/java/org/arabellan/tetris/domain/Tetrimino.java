package org.arabellan.tetris.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.arabellan.common.Color;
import org.arabellan.common.Coord;
import org.arabellan.common.Matrix;
import org.arabellan.tetris.Renderable;

@Builder
@Getter
public class Tetrimino implements Renderable {

    Color color;
    Type type;
    int rotation;

    @Getter
    Matrix<Character> renderable;

    @Setter
    Coord position;

    public enum Type {I, J, L, O, S, T, Z}
}
