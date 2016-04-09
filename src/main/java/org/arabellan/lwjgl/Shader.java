package org.arabellan.lwjgl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.arabellan.lwjgl.GLException.throwIfError;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glShaderSource;

public class Shader {
    int id;

    public Shader(String filename, int type) {
        id = glCreateShader(type);
        throwIfError();

        String source = loadShaderSource(filename);

        glShaderSource(id, source);
        throwIfError();

        glCompileShader(id);
        throwIfError();

        int compileStatus = glGetShaderi(id, GL_COMPILE_STATUS);
        throwIfError();

        if (compileStatus != GL_TRUE) {
            throw new RuntimeException("ShaderProgram compilation error: " + glGetShaderInfoLog(id));
        }
    }

    private String loadShaderSource(String filename) {
        try {
            Path location = Paths.get(filename);
            byte[] bytes = Files.readAllBytes(location);
            return new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Cannot load a shader file: " + filename, e);
        }
    }
}
