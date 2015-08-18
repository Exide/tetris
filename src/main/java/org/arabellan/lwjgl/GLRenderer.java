package org.arabellan.lwjgl;

import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.Renderable;
import org.arabellan.tetris.Scene;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GLContext;

import java.nio.FloatBuffer;
import java.util.Arrays;

import static org.arabellan.lwjgl.GLException.throwIfError;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

@Slf4j
public class GLRenderer {

    private float[] square = {
            -10f, -10f, // A    E--D
            -10f,  10f, // B     \ |    counter-clockwise
             10f, -10f, // C      'F
             10f,  10f, // D    B.
            -10f,  10f, // E    | \     clockwise
             10f, -10f, // F    A--C
    };

    private int vbo;
    private int vao;
    private ShaderProgram shader;
    private Camera camera;

    public void initialize(int width, int height) {
        createGLContext();
        initializeGLState(width, height);

        vbo = createVBO(square);
        vao = createVAO(vbo);
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
        glClearColor(0, 0, 0, 0);
        throwIfError();
    }

    private int createVBO(float[] mesh) {
        int id = glGenBuffers();
        throwIfError();

        FloatBuffer buffer = BufferUtils.createFloatBuffer(mesh.length);
        buffer.put(mesh).flip();

        glBindBuffer(GL_ARRAY_BUFFER, id);
        throwIfError();

        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        throwIfError();

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        throwIfError();

        return id;
    }

    private int createVAO(int vbo) {
        int id = glGenVertexArrays();
        throwIfError();

        glBindVertexArray(id);
        throwIfError();

        // use these vertices to draw our object
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        throwIfError();

        // read the vertices in this way
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        throwIfError();

        return id;
    }

    private ShaderProgram createDefaultShader() {
        Shader vertex = new Shader("shaders/vertex.glsl", GL_VERTEX_SHADER);
        Shader fragment = new Shader("shaders/fragment.glsl", GL_FRAGMENT_SHADER);
        return new ShaderProgram(Arrays.asList(vertex, fragment));
    }

    public void draw(Scene scene) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        throwIfError();

        shader.enable();
        shader.enableAttribute("position");

        for (Renderable r : scene.getRenderables()) {
            float x = (float) r.getPosition().getX();
            float y = (float) r.getPosition().getY();
            float z = (float) r.getPosition().getZ();
            Vector3f objectPosition = new Vector3f(x, y, z);

            shader.setUniform("model", getModelMatrix(objectPosition));
            shader.setUniform("view", getViewMatrix(camera));
            shader.setUniform("projection", getProjectionMatrix(camera));

            drawObject(vao);
        }

        shader.disableAttribute("position");
        shader.disable();
    }

    private void drawObject(int vao) {
        glBindVertexArray(vao);
        throwIfError();

        glDrawArrays(GL_TRIANGLES, 0, square.length);
        throwIfError();
    }

    private FloatBuffer getModelMatrix(Vector3f position) {
        Matrix4f model = new Matrix4f().translate(position);
        return matrixAsBuffer(model);
    }

    private FloatBuffer getViewMatrix(Camera camera) {
        Matrix4f view = new Matrix4f().lookAt(camera.position, camera.focus, camera.up);
        return matrixAsBuffer(view);
    }

    private FloatBuffer getProjectionMatrix(Camera camera) {
        Matrix4f projection = new Matrix4f().setOrtho(camera.left, camera.right, camera.bottom, camera.top, camera.nearClip, camera.farClip);
        return matrixAsBuffer(projection);
    }

    private FloatBuffer matrixAsBuffer(Matrix4f matrix) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        matrix.get(buffer);
        return buffer;
    }
}
