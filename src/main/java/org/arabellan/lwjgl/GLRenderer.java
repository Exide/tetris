package org.arabellan.lwjgl;

import lombok.extern.slf4j.Slf4j;
import org.arabellan.common.Matrix;
import org.arabellan.tetris.Renderable;
import org.arabellan.tetris.scenes.Scene;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

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

    // TODO: move all this shit into a Block class maybe?
    private int blockVAO;
    private int blockSize = 20;
    private float p = 1f * (blockSize / 2);
    private float[] block = {
            -p, -p, // A    E--D
            -p, +p, // B     \ |    counter-clockwise
            +p, -p, // C      'F
            +p, +p, // D    B.
            -p, +p, // E    | \     clockwise
            +p, -p, // F    A--C
    };

    private ShaderProgram shader;
    private Camera camera;

    public void initialize(int width, int height) {
        createGLContext();
        initializeGLState();
        blockVAO = loadBlockMesh();
        shader = createDefaultShader();
        camera = new Camera(width, height);
    }

    private void createGLContext() {
        GL.createCapabilities();
        throwIfError();

        log.info("GL version: " + glGetString(GL_VERSION));
        throwIfError();

        log.info("GLSL version: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
        throwIfError();
    }

    private void initializeGLState() {
        glClearColor(0, 0, 0, 0);
        throwIfError();
    }

    private int loadBlockMesh() {
        int vbo = createVBO(block);
        return createVAO(vbo);
    }

    private int createVBO(float[] vertices) {
        int id = glGenBuffers();
        throwIfError();

        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();

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

        if (scene.getRenderables().isEmpty()) return;

        shader.enable();
        shader.enableAttribute("vertex");
        shader.setUniform("view", getViewMatrix(camera));
        shader.setUniform("projection", getProjectionMatrix(camera));
        scene.getRenderables().forEach(this::drawRenderable);
        shader.disableAttribute("vertex");
        shader.disable();
    }

    private void drawRenderable(Renderable renderable) {
        Vector2f renderablePosition = getRenderableScreenPosition(renderable.getPosition(), renderable.getMatrix());
        renderable.getMatrix().forEach((matrixCoord, block) -> {
            if (block == 1) {
                Vector3f blockPosition = getBlockScreenPosition(renderablePosition, matrixCoord);
                renderBlock(blockPosition);
            }
        });
    }

    private void renderBlock(Vector3f position) {
        shader.setUniform("model", getModelMatrix(position));

        glBindVertexArray(blockVAO);
        throwIfError();

        glDrawArrays(GL_TRIANGLES, 0, 6);
        throwIfError();
    }

    private Vector2f getRenderableScreenPosition(Vector2f position, Matrix<Integer> matrix) {
        float x = position.x - ((float)matrix.width() / 2);
        float y = position.y + ((float)matrix.height() / 2);
        return new Vector2f(x, y);
    }

    private Vector3f getBlockScreenPosition(Vector2f position, Vector2f matrixCoord) {
        float x = (position.x + matrixCoord.x) * blockSize;
        float y = (position.y - matrixCoord.y) * blockSize;
        return new Vector3f(x, y, 0);
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
