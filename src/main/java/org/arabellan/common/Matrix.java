package org.arabellan.common;

import lombok.Getter;
import org.joml.Vector2f;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Matrix<T> {

    @Getter
    T[][] data;

    public Matrix(T[][] data) {
        this.data = data;
    }

    public void add(Matrix<T> matrix) {
        add(matrix, new Vector2f());
    }

    public void add(Matrix<T> matrix, Vector2f offset) {
        int x = ((int) offset.x);
        int y = ((int) offset.y);
        T[][] newData = matrix.getData();

        for (int row = 0; row < newData.length; ++row) {
            for (int column = 0; column < newData[row].length; ++column) {
                data[row + y][column + x] = newData[row][column];
            }
        }
    }

    public Matrix<T> replace(T a, T b) {
        for (int row = 0; row < data.length; ++row) {
            for (int column = 0; column < data[row].length; ++column) {
                if (data[row][column] == a) {
                    data[row][column] = b;
                }
            }
        }

        return this;
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

    public int count(T t) {
        int count = 0;
        for (int row = 0; row < data.length; ++row) {
            for (int column = 0; column < data[row].length; ++column) {
                if (data[row][column] == t) {
                    ++count;
                }
            }
        }
        return count;
    }

    public int height() {
        return data.length;
    }

    public int width() {
        return data[0].length;
    }

    public void forEach(BiConsumer<Vector2f, T> consumer) {
        for (int row = 0; row < data.length; ++row) {
            for (int column = 0; column < data[row].length; ++column) {
                consumer.accept(new Vector2f(column, row), data[row][column]);
            }
        }
    }
}
