package org.arabellan.tetris;

import lombok.Builder;
import lombok.Value;
import org.arabellan.common.Matrix;
import org.joml.Vector2f;

@Value
@Builder
public class Renderable {
    Vector2f position;
    Matrix<Integer> matrix;
}
