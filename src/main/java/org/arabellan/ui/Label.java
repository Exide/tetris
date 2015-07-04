package org.arabellan.ui;

import lombok.Builder;
import lombok.Getter;
import org.arabellan.common.Coord;
import org.arabellan.common.Matrix;
import org.arabellan.tetris.Renderable;

@Builder
public class Label implements Renderable {
    String text;

    @Getter
    Matrix<Character> renderable;

    @Getter
    Coord position;
}
