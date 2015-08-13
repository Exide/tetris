package org.arabellan.tetris.lwjgl;

import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.Renderer;
import org.arabellan.tetris.Scene;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GLContext;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GLContext.translateGLErrorString;

@Slf4j
public class LWJGLRenderer implements Renderer {

    private float[] square = {
            -.1f, -.1f, // A    E--D
            -.1f,  .1f, // B     \ |    counter-clockwise
             .1f, -.1f, // C      'F
             .1f,  .1f, // D    B.
            -.1f,  .1f, // E    | \     clockwise
             .1f, -.1f, // F    A--C
    };

    private int vertexArrayID;
    private int vertexBufferID;
    private int shaderID;

    @Override
    public void initialize(int width, int height) {
        GLContext.createFromCurrent();
        log.info("GL version: " + glGetString(GL_VERSION));
        log.info("GLSL version: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
        {
            glClearColor(0, 0, 0, 0);
            int error = glGetError();
            if (error != 0) log.debug("glClearColor: " + translateGLErrorString(error));
        }
        loadResources();
    }

    public void loadResources() {
        vertexArrayID = createVertexArray();
        vertexBufferID = createVertexBuffer();
        int vertexShader = buildShader("shaders/vertex.glsl", GL_VERTEX_SHADER);
        int fragmentShader = buildShader("shaders/fragment.glsl", GL_FRAGMENT_SHADER);
        shaderID = buildShaderProgram(Arrays.asList(vertexShader, fragmentShader));
    }

    private int createVertexArray() {
        int id = glGenVertexArrays();
        {
            glBindVertexArray(id);
            int error = glGetError();
            if (error != 0) log.debug("glBindVertexArray: " + translateGLErrorString(error));
        }
        log.info("Vertex Array created: " + id);
        return id;
    }

    private int createVertexBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(square.length);
        buffer.put(square).flip();
        int id = glGenBuffers();
        {
            glBindBuffer(GL_ARRAY_BUFFER, id);
            int error = glGetError();
            if (error != 0) log.debug("glBindBuffer: " + translateGLErrorString(error));
        }
        {
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
            int error = glGetError();
            if (error != 0) log.debug("glBufferData: " + translateGLErrorString(error));
        }
        {
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            int error = glGetError();
            if (error != 0) log.debug("glBindBuffer: " + translateGLErrorString(error));
        }
        log.info("Vertex Buffer created: " + id);
        return id;
    }

    private int buildShaderProgram(List<Integer> shaders) {
        int id = glCreateProgram();
        shaders.forEach(shader -> {
            glAttachShader(id, shader);
            int error = glGetError();
            if (error != 0) log.debug("glAttachShader: " + translateGLErrorString(error));
        });
        {
            glLinkProgram(id);
            int error = glGetError();
            if (error != 0) log.debug("glLinkProgram: " + translateGLErrorString(error));
        }
        shaders.forEach(shader -> {
            glDetachShader(id, shader);
            int error = glGetError();
            if (error != 0) log.debug("glDetachShader: " + translateGLErrorString(error));
        });

        if (glGetShaderi(id, GL_LINK_STATUS) == GL_TRUE) {
            log.info("Shader Program created: " + id);
            return id;
        } else {
            throw new RuntimeException("Shader linker error: " + glGetProgramInfoLog(id));
        }
    }

    private int buildShader(String filename, int type) {
        int id = glCreateShader(type);
        String source = loadShaderSource(filename);
        {
            glShaderSource(id, source);
            int error = glGetError();
            if (error != 0) log.debug("glShaderSource: " + translateGLErrorString(error));
        }
        {
            glCompileShader(id);
            int error = glGetError();
            if (error != 0) log.debug("glCompileShader: " + translateGLErrorString(error));
        }

        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_TRUE) {
            log.info("Shader created: " + id + " from " + filename);
            return id;
        } else {
            throw new RuntimeException("Shader compilation error: " + glGetShaderInfoLog(id));
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

    @Override
    public void draw(Scene scene) {
        {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            int error = glGetError();
            if (error != 0) log.debug("glClear: " + translateGLErrorString(error));
        }
        {
            glUseProgram(shaderID);
            int error = glGetError();
            if (error != 0) log.debug("glUseProgram: " + translateGLErrorString(error));
        }
        {
            glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
            int error = glGetError();
            if (error != 0) log.debug("glBindBuffer: " + translateGLErrorString(error));
        }
        {
            glBindAttribLocation(shaderID, 0, "position");
            int error = glGetError();
            if (error != 0) log.debug("glBindAttribLocation: " + translateGLErrorString(error));
        }

        {
            glEnableVertexAttribArray(0);
            int error = glGetError();
            if (error != 0) log.debug("glEnableVertexAttribArray: " + translateGLErrorString(error));
        }
        {
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
            int error = glGetError();
            if (error != 0) log.debug("glVertexAttribPointer: " + translateGLErrorString(error));
        }
        {
            glDrawArrays(GL_TRIANGLES, 0, 6);
            int error = glGetError();
            if (error != 0) log.debug("glDrawArrays: " + translateGLErrorString(error));
        }
        {
            glDisableVertexAttribArray(0);
            int error = glGetError();
            if (error != 0) log.debug("glDisableVertexAttribArray: " + translateGLErrorString(error));
        }
        {
            glUseProgram(0);
            int error = glGetError();
            if (error != 0) log.debug("glUseProgram: " + translateGLErrorString(error));
        }
    }
}
