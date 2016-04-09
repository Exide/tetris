package org.arabellan.lwjgl;

import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.Renderable;
import org.arabellan.tetris.scenes.Scene;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import java.nio.IntBuffer;

import static org.arabellan.lwjgl.GLException.throwIfError;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glGetIntegerv;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.GL_MAJOR_VERSION;
import static org.lwjgl.opengl.GL30.GL_MINOR_VERSION;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * The GLRenderer draws {@link Renderable}s returned by {@link Scene}.getRenderables().
 */

@Slf4j
public class GLRenderer {

    private Camera camera;

    public void initialize(int width, int height) {
        GL.createCapabilities();
        throwIfError();

        logGLVersion();
        logGLSLVersion();

        initializeGLState();
        logGLState();

        camera = new Camera(width, height);
    }

    private void logGLVersion() {
        int majorVersion = glGetInteger(GL_MAJOR_VERSION);
        throwIfError();

        int minorVersion = glGetInteger(GL_MINOR_VERSION);
        throwIfError();

        log.debug(String.format("GL version: %s.%s", majorVersion, minorVersion));
    }

    private void logGLSLVersion() {
        String glslVersion = glGetString(GL_SHADING_LANGUAGE_VERSION);
        throwIfError();

        log.debug(String.format("GLSL version: %s", glslVersion));
    }

    private void logGLState() {
        IntBuffer viewportState = BufferUtils.createIntBuffer(4);
        glGetIntegerv(GL_VIEWPORT, viewportState);
        throwIfError();

        log.debug(String.format("GL viewport window coordinates: %s,%s", viewportState.get(0), viewportState.get(1)));
        log.debug(String.format("GL viewport dimensions: %sx%s", viewportState.get(2), viewportState.get(3)));
    }

    private void initializeGLState() {
        glClearColor(0, 0, 0, 0);
        throwIfError();
    }

    public void draw(Scene scene) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        throwIfError();

        if (scene.getRenderables().isEmpty()) return;
        scene.getRenderables().forEach(this::drawRenderable);
    }

    private void drawRenderable(Renderable renderable) {
        ShaderProgram shader = renderable.getShader();
        Vector2f position = renderable.getTransform().getPosition();
        Vector2f scale = renderable.getTransform().getScale();

        glUseProgram(shader.getId());
        throwIfError();

        shader.setUniform("view", getViewMatrix(camera));
        shader.setUniform("projection", getProjectionMatrix(camera));
        shader.setUniform("model", getModelMatrix(position, scale));

        glBindVertexArray(renderable.getVertexArray());
        throwIfError();

        glDrawElements(GL_TRIANGLES, renderable.getVertexCount(), GL_UNSIGNED_INT, 0);
        throwIfError();
    }

    private Matrix4f getModelMatrix(Vector2f position, Vector2f scale) {
        Vector3f positionIn3D = new Vector3f(position.x, position.y, 0);
        Vector3f scaleIn3D = new Vector3f(scale.x, scale.y, 0);
        return new Matrix4f().translate(positionIn3D).scale(scaleIn3D);
    }

    private Matrix4f getViewMatrix(Camera camera) {
        return new Matrix4f().lookAt(camera.position, camera.focus, camera.up);
    }

    private Matrix4f getProjectionMatrix(Camera camera) {
        return new Matrix4f().setOrtho(camera.left, camera.right, camera.bottom, camera.top, camera.nearClip, camera.farClip);
    }
}
