package org.arabellan.lwjgl;

import java.nio.FloatBuffer;
import java.util.List;

import static org.arabellan.lwjgl.GLException.throwIfError;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class ShaderProgram {

    private static final int NO_SHADER = 0;
    int id;

    ShaderProgram(List<Shader> shaders) {
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

    public void enable() {
        glUseProgram(id);
        throwIfError();
    }

    public void enableAttribute(String name) {
        int attribute = glGetAttribLocation(id, name);
        throwIfError();

        glEnableVertexAttribArray(attribute);
        throwIfError();
    }

    public void disableAttribute(String name) {
        int attribute = glGetAttribLocation(id, name);
        throwIfError();

        glDisableVertexAttribArray(attribute);
        throwIfError();
    }

    public void setAttribute(String name, FloatBuffer matrix) {
        int uniform = glGetUniformLocation(id, name);
        throwIfError();

        glUniformMatrix4fv(uniform, false, matrix);
        throwIfError();
    }

    public void disable() {
        glUseProgram(NO_SHADER);
        throwIfError();
    }
}
