package org.arabellan.lwjgl;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.events.QuitEvent;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWVidMode;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_CLIENT_API;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_API;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

@Slf4j
public class GLFWWindow {

    private static final String WINDOW_TITLE = "Tetris";

    private final EventBus eventBus;
    private long window;

    @Inject
    public GLFWWindow(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void initialize(int width, int height) {
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE);

        long monitorToUse = NULL;
        long windowToShareResourcesWith = NULL;
        window = glfwCreateWindow(width, height, WINDOW_TITLE, monitorToUse, windowToShareResourcesWith);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        IntBuffer windowWidth = BufferUtils.createIntBuffer(1);
        IntBuffer windowHeight = BufferUtils.createIntBuffer(1);
        glfwGetWindowSize(window, windowWidth, windowHeight);
        log.debug(String.format("GLFW window dimensions: %sx%s", windowWidth.get(), windowHeight.get()));

        IntBuffer framebufferWidth = BufferUtils.createIntBuffer(1);
        IntBuffer framebufferHeight = BufferUtils.createIntBuffer(1);
        glfwGetFramebufferSize(window, framebufferWidth, framebufferHeight);
        log.debug(String.format("GLFW framebuffer dimensions: %sx%s", framebufferWidth.get(), framebufferHeight.get()));

        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        // Center our window
        int x = (vidmode.width() - width) / 2;
        int y = (vidmode.height() - height) / 2;
        glfwSetWindowPos(window, x, y);

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    public void update() {
        if (glfwWindowShouldClose(window) == 1) {
            eventBus.post(new QuitEvent());
        }

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    public void shutdown() {
        glfwDestroyWindow(window);
    }

    public long getHandle() {
        return window;
    }
}
