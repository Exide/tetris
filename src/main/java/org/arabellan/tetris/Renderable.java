package org.arabellan.tetris;

import lombok.Builder;
import lombok.Value;
import org.arabellan.lwjgl.Transform;
import org.arabellan.lwjgl.VertexArrayObject;

@Value
@Builder
public class Renderable {
    Transform transform;
    VertexArrayObject vertexArray;
}
