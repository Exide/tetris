package org.arabellan.ui;

public class WidgetFactory {

    public Label createLabel(String text) {
        return Label.builder()
                .text(text)
                .shape(createShape(text))
                .build();
    }

    private char[][] createShape(String text) {
        // TODO: Build the shape from the text
        return new char[][]{
                {'T', 'T', 'T', ' ', 'E', 'E', 'E', ' ', 'T', 'T', 'T', ' ', 'R', 'R', ' ', ' ', 'I', 'I', 'I', ' ', 'S', 'S', 'S'},
                {' ', 'T', ' ', ' ', 'E', ' ', ' ', ' ', ' ', 'T', ' ', ' ', 'R', ' ', 'R', ' ', ' ', 'I', ' ', ' ', 'S', ' ', ' '},
                {' ', 'T', ' ', ' ', 'E', ' ', ' ', ' ', ' ', 'T', ' ', ' ', 'R', ' ', 'R', ' ', ' ', 'I', ' ', ' ', 'S', ' ', ' '},
                {' ', 'T', ' ', ' ', 'E', 'E', 'E', ' ', ' ', 'T', ' ', ' ', 'R', 'R', ' ', ' ', ' ', 'I', ' ', ' ', 'S', 'S', 'S'},
                {' ', 'T', ' ', ' ', 'E', ' ', ' ', ' ', ' ', 'T', ' ', ' ', 'R', ' ', 'R', ' ', ' ', 'I', ' ', ' ', ' ', ' ', 'S'},
                {' ', 'T', ' ', ' ', 'E', ' ', ' ', ' ', ' ', 'T', ' ', ' ', 'R', ' ', 'R', ' ', ' ', 'I', ' ', ' ', ' ', ' ', 'S'},
                {' ', 'T', ' ', ' ', 'E', 'E', 'E', ' ', ' ', 'T', ' ', ' ', 'R', ' ', 'R', ' ', 'I', 'I', 'I', ' ', 'S', 'S', 'S'}
        };
    }
}
