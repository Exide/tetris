package org.arabellan.tetris.events;

import lombok.Value;
import org.arabellan.tetris.Event;

@Value
public class ChangeSceneEvent implements Event {
    Class sceneClass;
}
