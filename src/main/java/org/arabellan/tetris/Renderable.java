package org.arabellan.tetris;

import lombok.Builder;
import lombok.Value;
import org.arabellan.lwjgl.ShaderProgram;
import org.arabellan.lwjgl.Transform;

@Value
@Builder
public class Renderable {
    Transform transform;
    ShaderProgram shader;
    int vertexArray;
    int vertexCount;
}
