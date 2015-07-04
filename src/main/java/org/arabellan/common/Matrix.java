package org.arabellan.common;

import lombok.Getter;

public class Matrix<T> {

    @Getter
    T[][] data;

    public Matrix(T[][] data) {
        this.data = data;
    }

    public void add(Matrix<T> matrix) {
        Coord noOffset = Coord.builder().build();
        add(matrix, noOffset);
    }

    public void add(Matrix<T> matrix, Coord offset) {
        int x = ((int) offset.getX());
        int y = ((int) offset.getY());
        T[][] newData = matrix.getData();

        for (int row = 0; row < newData.length; ++row) {
            for (int column = 0; column < newData[row].length; ++column) {
                data[row + y][column + x] = newData[row][column];
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (T[] row : data) {
            for (T cell : row) {
                sb.append(cell).append(" ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
