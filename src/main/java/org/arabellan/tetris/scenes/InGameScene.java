package org.arabellan.tetris.scenes;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.events.QuitEvent;

@Slf4j
public class InGameScene implements Scene {

    @Inject
    private EventBus eventBus;

    @Override
    public void initialize() {
    }

    @Override
    public void update(double delta) {
        log.info("Updating!");
        eventBus.post(new QuitEvent());
    }
}
