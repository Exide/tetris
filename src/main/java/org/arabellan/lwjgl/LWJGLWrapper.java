package org.arabellan.lwjgl;

import lombok.extern.slf4j.Slf4j;
import org.arabellan.common.Platform;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.io.File;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.memDecodeUTF8;

@Slf4j
public class LWJGLWrapper {

    public void initialize() {
        loadLWJGLNativeLibs();
        log.info("LWJGL version: " + Version.getVersion());

        glfwSetErrorCallback(createErrorLogger());
        if (glfwInit() != GL_TRUE) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
    }

    private GLFWErrorCallback createErrorLogger() {
        return new GLFWErrorCallback() {
            @Override
            public void invoke(int error, long description) {
                log.error(String.format("(%s) %s", error, memDecodeUTF8(description)));
            }
        };
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
    }
}
