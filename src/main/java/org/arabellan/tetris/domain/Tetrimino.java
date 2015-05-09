package org.arabellan.tetris.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.arabellan.common.Color;
import org.arabellan.common.Coord;
import org.arabellan.tetris.Renderable;

@Builder
@Getter
public class Tetrimino implements Renderable {
    Color color;
    Type type;
    int[][] shape;
    int rotation;

    @Setter
    Coord position;

    public int[][] getRenderable() {
        return shape;
    }

    public Renderable.Type getTypeOfRenderable() {
        return Renderable.Type.TETRIMINO;
    }

    public enum Type {I, J, L, O, S, T, Z}
}
