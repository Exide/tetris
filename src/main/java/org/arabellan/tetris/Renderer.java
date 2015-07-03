package org.arabellan.tetris;

import lombok.extern.slf4j.Slf4j;
import org.arabellan.common.Function2;

import java.util.List;

import static org.arabellan.tetris.domain.Well.ACTIVE;
import static org.arabellan.tetris.domain.Well.OCCUPIED;
import static org.arabellan.tetris.domain.Well.SPACE;

@Slf4j
public class Renderer {

    private int[][] display;
    private int width, height;

    public void initialize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void draw(Scene scene) {
        display = new int[height][width];
        List<Renderable> objects = scene.getRenderables();
        log.debug("\n" + renderAsString(objects));
    }

    private String renderAsString(List<Renderable> objects) {
        objects.forEach(this::addToDisplay);
        return matrixToString(display);
    }

    private void addToDisplay(Renderable object) {
        int[][] overlay = matrixCopy(display);

        switch (object.getTypeOfRenderable()) {
            case TETRIMINO:
                int x = ((int) object.getPosition().getX());
                int y = ((int) object.getPosition().getY());
                overlay[y][x] = ACTIVE;
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

    private int[][] matrixCopy(int[][] original) {
        int[][] copy = matrixCreateEmpty(original);
        matrixCopyData(original, copy);
        return copy;
    }

    private int[][] matrixCreateEmpty(int[][] original) {
        int rows = original.length;
        int columns = original[0].length;
        return new int[rows][columns];
    }

    private void matrixCopyData(int[][] original, int[][] copy) {
        forEachCell(original, (row, column) -> {
            if (original[row][column] != 0) {
                copy[row][column] = original[row][column];
            }
        });
    }

    private void forEachCell(int[][] matrix, Function2<Integer, Integer> function) {
        for (int row = 0; row < matrix.length; ++row) {
            for (int column = 0; column < matrix[row].length; ++column) {
                function.execute(row, column);
            }
        }
    }

    public String matrixToString(int[][] matrix) {
        StringBuilder sb = new StringBuilder();

        for (int[] row : matrix) {
            sb.append(arrayToString(row)).append("\n");
        }

        return sb.toString();
    }

    private String arrayToString(int[] row) {
        StringBuilder sb = new StringBuilder();

        for (int cell : row) {
            sb.append(getSymbol(cell)).append(" ");
        }

        return sb.toString();
    }

    private char getSymbol(int symbol) {
        switch (symbol) {
            case SPACE:
                return '.';
            case ACTIVE:
                return 'O';
            case OCCUPIED:
                return 'X';
            default:
                return '?';
        }
    }
}
