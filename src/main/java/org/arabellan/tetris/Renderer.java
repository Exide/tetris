package org.arabellan.tetris;

import lombok.extern.slf4j.Slf4j;
import org.arabellan.common.Function2;

import java.util.List;

@Slf4j
public class Renderer {

    private char[][] display;
    private int width, height;

    public void initialize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void draw(Scene scene) {
        display = new char[height][width];
        List<Renderable> objects = scene.getRenderables();
        log.debug("\n" + renderAsString(objects));
    }

    private String renderAsString(List<Renderable> objects) {
        objects.forEach(this::addToDisplay);
        return matrixToString(display);
    }

    private void addToDisplay(Renderable object) {
        char[][] overlay = matrixCopy(display);

        switch (object.getTypeOfRenderable()) {
            case TETRIMINO:
                int x = ((int) object.getPosition().getX());
                int y = ((int) object.getPosition().getY());
                overlay[y][x] = 'O';
                break;
            case WELL:
                matrixCopyData(object.getRenderable(), overlay);
                break;
            case UI:
                matrixCopyData(object.getRenderable(), overlay);
                break;
            default:
                throw new RuntimeException("Unknown renderable object type!");
        }

        display = overlay;
    }

    private char[][] matrixCopy(char[][] original) {
        char[][] copy = matrixCreateEmpty(original);
        matrixCopyData(original, copy);
        return copy;
    }

    private char[][] matrixCreateEmpty(char[][] original) {
        int rows = original.length;
        int columns = original[0].length;
        return new char[rows][columns];
    }

    private void matrixCopyData(char[][] original, char[][] copy) {
        forEachCell(original, (row, column) -> {
            if (original[row][column] != 0) {
                copy[row][column] = original[row][column];
            }
        });
    }

    private void forEachCell(char[][] matrix, Function2<Integer, Integer> function) {
        for (int row = 0; row < matrix.length; ++row) {
            for (int column = 0; column < matrix[row].length; ++column) {
                function.execute(row, column);
            }
        }
    }

    public String matrixToString(char[][] matrix) {
        StringBuilder sb = new StringBuilder();

        for (char[] row : matrix) {
            sb.append(arrayToString(row)).append("\n");
        }

        return sb.toString();
    }

    private String arrayToString(char[] row) {
        StringBuilder sb = new StringBuilder();

        for (char cell : row) {
            sb.append(cell).append(" ");
        }

        return sb.toString();
    }
}
