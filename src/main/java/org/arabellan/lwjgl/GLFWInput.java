package org.arabellan.lwjgl;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.Controller;
import org.arabellan.tetris.Controller.Key;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

@Slf4j
public class GLFWInput {

    @Inject
    private Controller controller;

    @Inject
    private GLFWWindow window;

    private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (action == GLFW_RELEASE) {
                if (convertEnum(key) != Key.UNKNOWN)
                    controller.trigger(convertEnum(key));
            }
        }
    };

    public void initialize() {
        glfwSetKeyCallback(window.getHandle(), keyCallback);
    }

    public void update() {
    }

    public void shutdown() {
        if (keyCallback != null) {
            keyCallback.release();
        }
    }

    private Key convertEnum(int glfwEnumValue) {
        switch (glfwEnumValue) {
            case GLFW_KEY_ESCAPE:
                return Key.ESCAPE;
            case GLFW_KEY_SPACE:
                return Key.SPACE;
            case GLFW_KEY_LEFT:
                return Key.LEFT;
            case GLFW_KEY_RIGHT:
                return Key.RIGHT;
            case GLFW_KEY_UP:
                return Key.UP;
            case GLFW_KEY_DOWN:
                return Key.DOWN;
            default:
                log.debug("Unknown binding: " + glfwEnumValue);
                return Key.UNKNOWN;
        }
    }
}
