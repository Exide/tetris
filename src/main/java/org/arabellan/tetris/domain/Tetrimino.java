package org.arabellan.tetris.domain;

import lombok.Builder;
import lombok.Value;
import org.arabellan.common.Color;

@Value
@Builder
public class Tetrimino {
    public enum Type { I, J, L, O, S, T, Z }
    Color color;
    Type type;
}
