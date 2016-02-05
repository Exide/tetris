package org.arabellan.tetris.events;

import lombok.Value;

@Value
public class MoveEvent implements Event {
    Direction direction;

    public enum Direction {Left, Right, Down}
}
