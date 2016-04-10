package org.arabellan.tetris.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.joml.Vector2f;

import java.util.Arrays;
import java.util.stream.Stream;

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
                if (newData[row][column] != 0) {
                    data[row + y][column + x] = newData[row][column];
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

    int height() {
        return data.length;
    }

    int width() {
        return data[0].length;
    }

    public BlockMatrix copy() {
        Integer[][] copy = new Integer[data.length][];
        for (int i = 0; i < data.length; ++i) {
            copy[i] = Arrays.copyOf(data[i], data[i].length);
        }
        return new BlockMatrix(copy);
    }

    public Stream<BlockData> stream() {
        Stream.Builder<BlockData> builder = Stream.builder();
        int i = 0;
        for (int row = 0; row < data.length; ++row) {
            for (int column = 0; column < data[0].length; ++column) {
                builder.add(new BlockData(i, data[row][column]));
                ++i;
            }
        }
        return builder.build();
    }

    @Data
    @AllArgsConstructor
    public class BlockData {
        int index;
        int value;
    }
}
