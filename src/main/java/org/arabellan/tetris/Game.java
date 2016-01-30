package org.arabellan.tetris;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.lwjgl.GLFWInput;
import org.arabellan.tetris.events.QuitEvent;
import org.arabellan.lwjgl.GLFWWindow;
import org.arabellan.lwjgl.GLRenderer;

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
    private GLFWInput input;

    @Inject
    private GLRenderer renderer;

    @Inject
    private GLFWWindow window;

    @Inject
    public Game(EventBus eventBus) {
        log.debug("Constructing");
        eventBus.register(new QuitGameListener());
    }

    public void run() {
        initialize();
        while (isRunning) {
            director.update();
            renderer.draw(director.getScene().getRenderables());
            window.update();
            input.update();
        }
        shutdown();
    }

    private void initialize() {
        window.initialize(WIDTH, HEIGHT);
        renderer.initialize(WIDTH, HEIGHT);
        director.initialize();
        input.initialize();
    }

    private void shutdown() {
        input.shutdown();
        director.shutdown();
        window.shutdown();
    }

    private class QuitGameListener {
        @Subscribe
        public void listen(QuitEvent event) {
            log.debug("QuitEvent received");
            isRunning = false;
        }
    }
}
