package org.arabellan.ui;

import org.arabellan.common.Coord;
import org.arabellan.common.Matrix;

public class WidgetFactory {

    public Label createLabel(String text) {
        return Label.builder()
                .text(text)
                .renderable(createShape(text))
                .position(Coord.builder().x(2).y(2).build())
                .build();
    }

    private Matrix<Character> createShape(String text) {
        // TODO: Build the shape from the text
        return new Matrix<>(new Character[][]{
                {'T', 'T', 'T', ' ', 'E', 'E', 'E', ' ', 'T', 'T', 'T', ' ', 'R', 'R', ' ', ' ', 'I', 'I', 'I', ' ', 'S', 'S', 'S'},
                {' ', 'T', ' ', ' ', 'E', ' ', ' ', ' ', ' ', 'T', ' ', ' ', 'R', ' ', 'R', ' ', ' ', 'I', ' ', ' ', 'S', ' ', ' '},
                {' ', 'T', ' ', ' ', 'E', ' ', ' ', ' ', ' ', 'T', ' ', ' ', 'R', ' ', 'R', ' ', ' ', 'I', ' ', ' ', 'S', ' ', ' '},
                {' ', 'T', ' ', ' ', 'E', 'E', 'E', ' ', ' ', 'T', ' ', ' ', 'R', 'R', ' ', ' ', ' ', 'I', ' ', ' ', 'S', 'S', 'S'},
                {' ', 'T', ' ', ' ', 'E', ' ', ' ', ' ', ' ', 'T', ' ', ' ', 'R', ' ', 'R', ' ', ' ', 'I', ' ', ' ', ' ', ' ', 'S'},
                {' ', 'T', ' ', ' ', 'E', ' ', ' ', ' ', ' ', 'T', ' ', ' ', 'R', ' ', 'R', ' ', ' ', 'I', ' ', ' ', ' ', ' ', 'S'},
                {' ', 'T', ' ', ' ', 'E', 'E', 'E', ' ', ' ', 'T', ' ', ' ', 'R', ' ', 'R', ' ', 'I', 'I', 'I', ' ', 'S', 'S', 'S'}
        });
    }
}
