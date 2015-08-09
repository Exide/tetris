package org.arabellan.tetris.lwjgl;

import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.Renderer;
import org.arabellan.tetris.Scene;
import org.lwjgl.opengl.GLContext;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

@Slf4j
public class LWJGLRenderer implements Renderer {

    @Override
    public void initialize(int width, int height) {
        GLContext.createFromCurrent();
        glClearColor(0, 0, 0, 0);
    }

    @Override
    public void draw(Scene scene) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

}
