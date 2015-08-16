package org.arabellan.lwjgl;

import lombok.extern.slf4j.Slf4j;
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
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

@Slf4j
public class GLRenderer {

    private static final int NO_SHADER = 0;

    private float[] square = {
            -10f, -10f, // A    E--D
            -10f,  10f, // B     \ |    counter-clockwise
             10f, -10f, // C      'F
             10f,  10f, // D    B.
            -10f,  10f, // E    | \     clockwise
             10f, -10f, // F    A--C
    };

    private int vao;
    private ShaderProgram shader;
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

        // TODO: get objects to render from the scene
        Vector3f objectPosition = new Vector3f(0, 0, 0);
        VertexBufferObject objectVBO = vbo;

        shader.setAttribute("model", getModelMatrix(objectPosition));
        shader.setAttribute("view", getViewMatrix(camera));
        shader.setAttribute("projection", getProjectionMatrix(camera));

        drawObject(objectVBO);

        shader.disableAttribute("position");
        shader.disable();
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
