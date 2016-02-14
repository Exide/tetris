package org.arabellan.lwjgl;

import lombok.Builder;
import lombok.Value;
import org.joml.Vector2f;

@Value
@Builder
public class Transform {
    Vector2f position;
    Vector2f scale;
}
