package org.arabellan.ui;

import lombok.Builder;
import org.arabellan.common.Coord;
import org.arabellan.tetris.Renderable;

import static org.arabellan.tetris.Renderable.Type.UI;

@Builder
public class Label implements Renderable {
    String text;
    char[][] shape;

    @Override
    public Type getTypeOfRenderable() {
        return UI;
    }

    @Override
    public Coord getPosition() {
        return Coord.builder().x(2).y(2).z(0).build();
    }

    @Override
    public char[][] getRenderable() {
        return shape;
    }
}
