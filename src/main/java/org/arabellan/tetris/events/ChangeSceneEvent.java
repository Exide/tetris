package org.arabellan.tetris.events;

import lombok.Value;

@Value
public class ChangeSceneEvent {
    Class sceneClass;
}
