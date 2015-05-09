package org.arabellan.tetris.events;

import lombok.Value;
import org.arabellan.tetris.Event;

/**
 * Passing in a positive integer denotes right while a negative integer denotes left.
 */

@Value
public class MoveEvent implements Event {
    int direction;

    public boolean isLeft() {
        return direction < 0;
    }

    public boolean isRight() {
        return direction > 0;
    }
}
