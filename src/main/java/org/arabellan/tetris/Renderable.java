package org.arabellan.tetris;

import lombok.Builder;
import lombok.Value;
import org.arabellan.lwjgl.ShaderProgram;
import org.arabellan.lwjgl.Transform;
import org.joml.Vector4f;

@Value
@Builder
public class Renderable {
    Transform transform;
    ShaderProgram shader;
    Vector4f color;
    int vertexArray;
    int vertexCount;
}
