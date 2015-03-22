package org.arabellan.tetris.scenes;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.managers.InputManager;
import org.arabellan.tetris.managers.InputManager.Key;
import org.arabellan.tetris.events.ChangeSceneEvent;

@Slf4j
public class MainMenuScene implements Scene {

    @Inject
    private InputManager input;

    @Override
    public void initialize() {
        log.debug("Initializing");
        input.bind(Key.SPACE, new ChangeSceneEvent(InGameScene.class));
    }

    @Override
    public void update(double delta) {
        log.info("Updating!");
        input.trigger(Key.SPACE);
    }

    @Override
    public void cleanup() {
        log.debug("Cleaning up");
        input.clearBindings();
    }
}
