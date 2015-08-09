package org.arabellan.tetris;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.common.Platform;
import org.arabellan.tetris.lwjgl.LWJGLWrapper;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.io.File;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11.GL_TRUE;

@Slf4j
public class Main {

    public static void main(String[] args) {
        log.debug("Main entered");
        LWJGLWrapper lwjgl = new LWJGLWrapper();
        lwjgl.initialize();
        Injector injector = Guice.createInjector(new GuiceModule());
        injector.getInstance(Game.class).run();
        lwjgl.shutdown();
        log.debug("Main exiting");
    }
}
