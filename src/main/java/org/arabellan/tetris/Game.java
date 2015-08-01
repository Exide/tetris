package org.arabellan.tetris;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.events.QuitEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.function.LongConsumer;

/**
 * This class is responsible for initializing and updating management objects.
 */
@Slf4j
public class Game {

    private static final long TIME_STEP_IN_MS = 1750;
    private static final int WIDTH = 25; // super small
    private static final int HEIGHT = 10; // debug display

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
        renderer.initialize(WIDTH, HEIGHT);
    }

    private void loopUntilStopped() {
        renderer.draw(director.getScene());
        doAtTimeStep(delta -> {
            director.update();
            renderer.draw(director.getScene());
        });
    }

    private void doAtTimeStep(LongConsumer consumer) {
        while (isRunning) {
            long delta = Duration.between(lastUpdate, Instant.now()).toMillis();
            if (delta >= TIME_STEP_IN_MS) {
                consumer.accept(delta);
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
