package org.arabellan.lwjgl;

import lombok.extern.slf4j.Slf4j;
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
import static org.arabellan.lwjgl.VertexBufferObject.Type.VERTICES;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * The GLRenderer draws {@link Renderable}s returned by {@link Scene}.getRenderables().
 */

@Slf4j
public class GLRenderer {

    private ShaderProgram defaultShader;
    private Camera camera;

    public void initialize(int width, int height) {
        createGLContext();
        initializeGLState();
        defaultShader = createDefaultShader();
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

    private ShaderProgram createDefaultShader() {
        Shader vertex = new Shader("shaders/vertex.glsl", GL_VERTEX_SHADER);
        Shader fragment = new Shader("shaders/fragment.glsl", GL_FRAGMENT_SHADER);
        return new ShaderProgram(Arrays.asList(vertex, fragment));
    }

    public void draw(Scene scene) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        throwIfError();

        if (scene.getRenderables().isEmpty()) return;

        defaultShader.enable();
        defaultShader.enableAttribute("vertex");
        defaultShader.setUniform("view", getViewMatrix(camera));
        defaultShader.setUniform("projection", getProjectionMatrix(camera));
        scene.getRenderables().forEach(this::drawRenderable);
        defaultShader.disableAttribute("vertex");
        defaultShader.disable();
    }

    private void drawRenderable(Renderable renderable) {
        Vector2f position = renderable.getTransform().getPosition();
        Vector2f scale = renderable.getTransform().getScale();
        defaultShader.setUniform("model", getModelMatrix(position, scale));

        glBindVertexArray(renderable.getVertexArray().getId());
        throwIfError();

        glDrawArrays(GL_TRIANGLES, 0, renderable.getVertexArray().getBuffer(VERTICES).getVertexCount());
        throwIfError();
    }

    private FloatBuffer getModelMatrix(Vector2f position, Vector2f scale) {
        Vector3f positionIn3D = new Vector3f(position.x, position.y, 0);
        Vector3f scaleIn3D = new Vector3f(scale.x, scale.y, 0);
        return getModelMatrix(positionIn3D, scaleIn3D);
    }

    private FloatBuffer getModelMatrix(Vector3f position, Vector3f scale) {
        Matrix4f model = new Matrix4f().translate(position).scale(scale);
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
