package org.arabellan.tetris.domain;

import lombok.Builder;
import lombok.Value;
import org.arabellan.common.Color;
import org.arabellan.common.Coord;

@Value
@Builder
public class Tetrimino {
    Color color;
    Type type;
    int[][] shape;
    Coord position;
    int rotation;

    public enum Type {I, J, L, O, S, T, Z}
}
