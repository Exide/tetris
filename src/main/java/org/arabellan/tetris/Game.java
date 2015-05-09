package org.arabellan.tetris;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.events.QuitEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class is responsible for initializing and updating management objects.
 */
@Slf4j
public class Game {

    private static final long TIME_STEP_IN_MS = 1750;
    private static final int width = 12; // super small
    private static final int height = 6; // debug display

    private boolean isRunning = true;

    @Inject
    private Director director;

    @Inject
    private Renderer renderer;
    private Instant lastUpdate = Instant.now();

    @Inject
    public Game(EventBus eventBus) {
        log.debug("Constructing");
        eventBus.register(new QuitGameListener());
    }

    public void run() {
        log.debug("Starting");
        initialize();
        loopUntilStopped();
        shutdown();
    }

    private void initialize() {
        director.initialize();
        renderer.initialize(width, height);
    }

    private void loopUntilStopped() {
        renderer.draw(director.getScene());
        while (isRunning) {
            long delta = Duration.between(lastUpdate, Instant.now()).toMillis();
            if (delta >= TIME_STEP_IN_MS) {
                director.update();
                renderer.draw(director.getScene());
                lastUpdate = Instant.now();
            }
        }
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
