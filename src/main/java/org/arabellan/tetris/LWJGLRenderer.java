package org.arabellan.tetris;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.common.Platform;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLContext;

import java.io.File;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
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
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryUtil.NULL;

@Slf4j
public class LWJGLRenderer implements Renderer {

    private static final String WINDOW_TITLE = "Tetris";

    private int width;
    private int height;

    @Getter
    private long window;

    private GLFWErrorCallback errorCallback;

    @Override
    public void initialize(int width, int height) {
        this.width = width;
        this.height = height;

        loadLWJGLNativeLibs();
        log.info("LWJGL version: " + org.lwjgl.Sys.getVersion());

        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
        if (glfwInit() != GL_TRUE) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);

        long monitorToUse = NULL;
        long windowToShareResourcesWith = NULL;
        window = glfwCreateWindow(this.width, this.height, WINDOW_TITLE, monitorToUse, windowToShareResourcesWith);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        // Center our window
        int x = (GLFWvidmode.width(vidmode) - this.width) / 2;
        int y = (GLFWvidmode.height(vidmode) - this.height) / 2;
        glfwSetWindowPos(window, x, y);

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    @Override
    public void draw(Scene scene) {
        GLContext.createFromCurrent();
        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    private void loadLWJGLNativeLibs() {
        String platform = getLWJGLPlatform();
        String path = new File("libs/" + platform).getAbsolutePath();
        System.setProperty("org.lwjgl.librarypath", path);
    }

    private String getLWJGLPlatform() {
        Platform currentPlatform = new Platform();
        if (currentPlatform.isMacOS()) {
            return "osx";
        } else if (currentPlatform.isWindows()) {
            return "windows";
        } else {
            return "linux";
        }
    }
}
