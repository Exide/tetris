package org.arabellan.lwjgl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.List;

import static org.arabellan.lwjgl.GLException.throwIfError;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

@Slf4j
public class ShaderProgram {

    @Getter
    private final int id;

    public ShaderProgram(List<Shader> shaders) {
        id = glCreateProgram();
        throwIfError();

        buildProgram(shaders);
    }

    private void buildProgram(List<Shader> shaders) {
        shaders.forEach(shader -> {
            glAttachShader(id, shader.id);
            throwIfError();
        });

        glLinkProgram(id);
        throwIfError();

        int linkStatus = glGetProgrami(id, GL_LINK_STATUS);
        throwIfError();

        if (linkStatus != GL_TRUE) {
            throw new RuntimeException("ShaderProgram link error: " + glGetProgramInfoLog(id));
        }
    }

    public void setAttribute(String name, int stride) {
        int attributeLocation = glGetAttribLocation(id, name);
        throwIfError();

        glEnableVertexAttribArray(attributeLocation);
        throwIfError();

        glVertexAttribPointer(attributeLocation, stride, GL_FLOAT, false, 0, 0);
        throwIfError();
    }

    public void setUniform(String name, int value) {
        int uniformLocation = glGetUniformLocation(id, name);
        throwIfError();

        glUniform1i(uniformLocation, value);
        throwIfError();
    }

    public void setUniform(String name, Matrix4f matrix) {
        int uniformLocation = glGetUniformLocation(id, name);
        throwIfError();

        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        matrix.get(matrixBuffer);

        glUniformMatrix4fv(uniformLocation, false, matrixBuffer);
        throwIfError();
    }
}
