package org.arabellan.tetris.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.arabellan.common.Color;
import org.arabellan.common.Coord;

@Builder
@Getter
public class Tetrimino {
    Color color;
    Type type;
    int[][] shape;
    int rotation;

    @Setter
    Coord position;

    public enum Type {I, J, L, O, S, T, Z}
}
