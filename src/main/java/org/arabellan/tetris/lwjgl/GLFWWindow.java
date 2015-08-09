package org.arabellan.tetris.lwjgl;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import org.arabellan.tetris.Window;
import org.arabellan.tetris.events.QuitEvent;
import org.lwjgl.glfw.GLFWvidmode;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWWindow implements Window {

    private static final String WINDOW_TITLE = "Tetris";

    private final EventBus eventBus;
    private long window;

    @Inject
    GLFWWindow(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void initialize(int width, int height) {
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);

        long monitorToUse = NULL;
        long windowToShareResourcesWith = NULL;
        window = glfwCreateWindow(width, height, WINDOW_TITLE, monitorToUse, windowToShareResourcesWith);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        // Center our window
        int x = (GLFWvidmode.width(vidmode) - width) / 2;
        int y = (GLFWvidmode.height(vidmode) - height) / 2;
        glfwSetWindowPos(window, x, y);

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    @Override
    public void update() {
        glfwPollEvents();
        glfwSwapBuffers(window);

        if (glfwWindowShouldClose(window) == 1) {
            eventBus.post(new QuitEvent());
        }
    }

    @Override
    public void shutdown() {
        glfwDestroyWindow(window);
    }
}
