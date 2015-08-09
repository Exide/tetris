package org.arabellan.tetris.lwjgl;

import lombok.extern.slf4j.Slf4j;
import org.arabellan.common.Platform;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.io.File;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11.GL_TRUE;

@Slf4j
public class LWJGLWrapper {

    private GLFWErrorCallback errorCallback;

    public void initialize() {
        loadLWJGLNativeLibs();
        log.info("LWJGL version: " + org.lwjgl.Sys.getVersion());

        errorCallback = errorCallbackPrint(System.err);
        glfwSetErrorCallback(errorCallback);
        if (glfwInit() != GL_TRUE) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
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

    public void shutdown() {
        glfwTerminate();
        errorCallback.release();
    }
}
