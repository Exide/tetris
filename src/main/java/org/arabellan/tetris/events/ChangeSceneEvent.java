package org.arabellan.tetris.events;

import lombok.Value;

@Value
public class ChangeSceneEvent implements Event {
    Class sceneClass;
}
