package org.arabellan.tetris.domain;

import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.Arrays;
import java.util.function.BiConsumer;

public class BlockMatrix {

    @Getter
    Integer[][] data;

    public BlockMatrix(Integer[][] data) {
        this.data = data;
    }

    public BlockMatrix add(BlockMatrix matrix, Vector2f offset) {
        int x = ((int) offset.x);
        int y = ((int) -offset.y);
        Integer[][] newData = matrix.getData();

        for (int row = 0; row < newData.length; ++row) {
            for (int column = 0; column < newData[row].length; ++column) {
                if (newData[row][column] == 1) {
                    data[row + y][column + x] = newData[row][column];
                }
            }
        }

        return this;
    }

    public BlockMatrix add(Integer item, Vector2i position) {
        data[position.x][position.y] = item;
        return this;
    }

    public BlockMatrix replace(Integer a, Integer b) {
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

        for (Integer[] row : data) {
            for (Integer cell : row) {
                sb.append(cell).append(" ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public int count(Integer integer) {
        int count = 0;
        for (int row = 0; row < data.length; ++row) {
            for (int column = 0; column < data[row].length; ++column) {
                if (data[row][column] == integer) {
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

    public void forEach(BiConsumer<Vector2f, Integer> consumer) {
        for (int row = 0; row < data.length; ++row) {
            for (int column = 0; column < data[row].length; ++column) {
                consumer.accept(new Vector2f(column, row), data[row][column]);
            }
        }
    }

    public BlockMatrix copy() {
        Integer[][] copy = new Integer[data.length][];
        for (int i = 0; i < data.length; ++i) {
            copy[i] = Arrays.copyOf(data[i], data[i].length);
        }
        return new BlockMatrix(copy);
    }
}
