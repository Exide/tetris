package org.arabellan.ui;

public class WidgetFactory {

    public Label createLabel(String text) {
        return Label.builder()
                .text(text)
                .shape(createShape(text))
                .build();
    }

    private int[][] createShape(String text) {
        // TODO: Build the shape from the text
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
}
