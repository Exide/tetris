package org.arabellan.lwjgl;

import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.Scene;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GLContext;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.arabellan.lwjgl.GLException.throwIfError;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
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
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

@Slf4j
public class GLRenderer {

    private static final int NO_SHADER = 0;

    private float[] square = {
            -1f, -1f, // A    E--D
            -1f,  1f, // B     \ |    counter-clockwise
             1f, -1f, // C      'F
             1f,  1f, // D    B.
            -1f,  1f, // E    | \     clockwise
             1f, -1f, // F    A--C
    };

    private int vao;
    private int shader;
    private Camera camera;
    private VertexBufferObject vbo;

    public void initialize(int width, int height) {
        createGLContext();
        initializeGLState(width, height);

        vao = createVAO();
        vbo = createVBOFromMesh(square);
        shader = createDefaultShader();
        camera = new Camera(width, height);
    }

    private void createGLContext() {
        GLContext.createFromCurrent();
        throwIfError();

        log.info("GL version: " + glGetString(GL_VERSION));
        throwIfError();

        log.info("GLSL version: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
        throwIfError();
    }

    private void initializeGLState(int width, int height) {
        glViewport(0, 0, width, height);
        throwIfError();

        glClearColor(0, 0, 0, 0);
        throwIfError();
    }

    private int createVAO() {
        int id = glGenVertexArrays();
        throwIfError();

        glBindVertexArray(id);
        throwIfError();

        return id;
    }

    private VertexBufferObject createVBOFromMesh(float[] mesh) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(mesh.length);
        buffer.put(mesh).flip();

        VertexBufferObject vbo = new VertexBufferObject();
        vbo.uploadMesh(buffer);

        return vbo;
    }

    private int createDefaultShader() {
        int vertexShader = createShader("shaders/vertex.glsl", GL_VERTEX_SHADER);
        int fragmentShader = createShader("shaders/fragment.glsl", GL_FRAGMENT_SHADER);
        return createShaderProgram(Arrays.asList(vertexShader, fragmentShader));
    }

    private int createShaderProgram(List<Integer> shaders) {
        int program = glCreateProgram();
        throwIfError();

        shaders.forEach(shaderID -> {
            glAttachShader(program, shaderID);
            throwIfError();
        });

        glLinkProgram(program);
        throwIfError();

        int linkStatus = glGetProgrami(program, GL_LINK_STATUS);
        throwIfError();

        if (linkStatus != GL_TRUE) {
            throw new RuntimeException("Shader linker error: " + glGetProgramInfoLog(program));
        }

        return program;
    }

    private int createShader(String filename, int type) {
        int id = glCreateShader(type);
        throwIfError();

        String source = loadShaderSource(filename);

        glShaderSource(id, source);
        throwIfError();

        glCompileShader(id);
        throwIfError();

        int compileStatus = glGetShaderi(id, GL_COMPILE_STATUS);
        throwIfError();

        if (compileStatus != GL_TRUE) {
            throw new RuntimeException("Shader compilation error: " + glGetShaderInfoLog(id));
        }

        return id;
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

    public void draw(Scene scene) {
        // clear the buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        throwIfError();

        // use our default shader
        glUseProgram(shader);
        throwIfError();

        // get the vertex shader attribute for position
        int positionAttribute = glGetAttribLocation(shader, "position");
        throwIfError();

        // use this attribute in the vertex shader for the vertices
        glEnableVertexAttribArray(positionAttribute);
        throwIfError();

        // TODO: get objects to render from the scene
        Vector3f objectPosition = new Vector3f(0, 0, 0);
        VertexBufferObject objectVBO = vbo;

        setShaderAttributes(objectPosition, camera);
        drawObject(objectVBO);

        glDisableVertexAttribArray(positionAttribute);
        throwIfError();

        glUseProgram(NO_SHADER);
        throwIfError();
    }

    private void drawObject(VertexBufferObject vbo) {
        // use these vertices to draw our object
        glBindBuffer(GL_ARRAY_BUFFER, vbo.id);
        throwIfError();

        // read the vertices in this way
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        throwIfError();

        // use the vertices to draw triangles
        glDrawArrays(GL_TRIANGLES, 0, square.length);
        throwIfError();
    }

    private void setShaderAttributes(Vector3f objectPosition, Camera camera) {
        // get the vertex shader attribute for model
        int modelMatrixLocation = glGetUniformLocation(shader, "model");
        throwIfError();

        // use this model matrix in the vertex shader
        glUniformMatrix4fv(modelMatrixLocation, false, getModelMatrix(objectPosition));
        throwIfError();

        // get the vertex shader attribute for view
        int viewMatrixLocation = glGetUniformLocation(shader, "view");
        throwIfError();

        // use this view matrix in the vertex shader
        glUniformMatrix4fv(viewMatrixLocation, false, getViewMatrix(camera));
        throwIfError();

        // get the vertex shader attribute for projection
        int projectionMatrixLocation = glGetUniformLocation(shader, "projection");
        throwIfError();

        // use this projection matrix in the vertex shader
        glUniformMatrix4fv(projectionMatrixLocation, false, getProjectionMatrix(camera));
        throwIfError();
    }

    private FloatBuffer getModelMatrix(Vector3f position) {
        Matrix4f model = new Matrix4f().translate(position);
//        log.debug("modelMatrix:\n" + model);
        return matrixAsBuffer(model);
    }

    private FloatBuffer getViewMatrix(Camera camera) {
        Matrix4f view = new Matrix4f().lookAt(camera.position, camera.focus, camera.up);
//        log.debug("viewMatrix:\n" + view);
        return matrixAsBuffer(view);
    }

    private FloatBuffer getProjectionMatrix(Camera camera) {
        Matrix4f projection = new Matrix4f().setPerspective(camera.fieldOfView, camera.aspectRatio, camera.nearClip, camera.farClip);
//        log.debug("projectionMatrix:\n" + projection);
        return matrixAsBuffer(projection);
    }

    private FloatBuffer matrixAsBuffer(Matrix4f matrix) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        matrix.get(buffer);
        return buffer;
    }
}
