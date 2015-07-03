package org.arabellan.ui;

import org.arabellan.common.Coord;
import org.arabellan.tetris.Renderable;

import static org.arabellan.tetris.Renderable.Type.UI;

public class Label implements Renderable {
    String text;
    int[][] shape;

    public Label(String text) {
        this.text = text;
        this.shape = createShape(text);
    }

    private int[][] createShape(String text) {
        return new int[][]{
                {9, 9, 9, 0, 9, 9, 9, 0, 9, 9, 9, 0, 9, 9, 0, 0, 9, 9, 9, 0, 9, 9, 9},
                {0, 9, 0, 0, 9, 0, 0, 0, 0, 9, 0, 0, 9, 0, 9, 0, 0, 9, 0, 0, 9, 0, 0},
                {0, 9, 0, 0, 9, 0, 0, 0, 0, 9, 0, 0, 9, 0, 9, 0, 0, 9, 0, 0, 9, 0, 0},
                {0, 9, 0, 0, 9, 9, 9, 0, 0, 9, 0, 0, 9, 9, 0, 0, 0, 9, 0, 0, 9, 9, 9},
                {0, 9, 0, 0, 9, 0, 0, 0, 0, 9, 0, 0, 9, 0, 9, 0, 0, 9, 0, 0, 0, 0, 9},
                {0, 9, 0, 0, 9, 0, 0, 0, 0, 9, 0, 0, 9, 0, 9, 0, 0, 9, 0, 0, 0, 0, 9},
                {0, 9, 0, 0, 9, 9, 9, 0, 0, 9, 0, 0, 9, 0, 9, 0, 9, 9, 9, 0, 9, 9, 9}
        };
    }

    @Override
    public Type getTypeOfRenderable() {
        return UI;
    }

    @Override
    public Coord getPosition() {
        return Coord.builder().x(2).y(2).z(0).build();
    }

    @Override
    public int[][] getRenderable() {
        return shape;
    }
}
