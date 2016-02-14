package org.arabellan.tetris;

import lombok.Builder;
import lombok.Value;
import org.arabellan.lwjgl.opengl.VertexArrayObject;
import org.joml.Vector2f;

@Value
@Builder
public class Renderable {
    Vector2f position;
    VertexArrayObject vertexArray;
}
