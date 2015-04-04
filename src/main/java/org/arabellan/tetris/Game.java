package org.arabellan.tetris;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.events.QuitEvent;
import org.arabellan.tetris.managers.SceneManager;

/**
 * This class is responsible for initializing and updating management objects.
 */
@Slf4j
public class Game {

    private boolean isRunning = true;

    @Inject
    private SceneManager sceneManager;

    @Inject
    public Game(EventBus eventBus) {
        log.debug("Constructing");
        eventBus.register(new QuitGameListener());
    }

    public void start() {
        log.debug("Starting");
        sceneManager.initialize();
        while (isRunning) {
            // TODO: make a real update loop
            double delta = 0;
            sceneManager.update(delta);
        }
        sceneManager.shutdown();
    }

    private class QuitGameListener {
        @Subscribe
        public void listen(QuitEvent event) {
            log.debug("QuitEvent received");
            isRunning = false;
        }
    }
}
