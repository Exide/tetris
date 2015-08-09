package org.arabellan.tetris;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.events.QuitEvent;

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

/**
 * This class is responsible for initializing and updating management objects.
 */
@Slf4j
public class Game {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private boolean isRunning = true;

    @Inject
    private Director director;

    @Inject
//    private ConsoleRenderer renderer;
    private LWJGLRenderer renderer;

    @Inject
    public Game(EventBus eventBus) {
        log.debug("Constructing");
        eventBus.register(new QuitGameListener());
    }

    public void run() {
        initialize();
        while (isRunning) {
            exitOnWindowClose();
            director.update();
            renderer.draw(director.getScene());
        }
        shutdown();
    }

    private void exitOnWindowClose() {
        if (glfwWindowShouldClose(renderer.getWindow()) == 1) {
            isRunning = false;
        }
    }

    private void initialize() {
        director.initialize();
        renderer.initialize(WIDTH, HEIGHT);
    }

    private void shutdown() {
        director.shutdown();
    }

    private class QuitGameListener {
        @Subscribe
        public void listen(QuitEvent event) {
            log.debug("QuitEvent received");
            isRunning = false;
        }
    }
}
