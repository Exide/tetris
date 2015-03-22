package org.arabellan.tetris.scenes;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.InputManager;
import org.arabellan.tetris.InputManager.Key;
import org.arabellan.tetris.events.ChangeSceneEvent;

@Slf4j
public class MainMenuScene implements Scene {

    @Inject
    private InputManager input;

    @Override
    public void initialize() {
        log.debug("Initializing MainMenuScene");
        input.bind(Key.SPACE, new ChangeSceneEvent(InGameScene.class));
    }

    @Override
    public void update(double delta) {
        log.info("Updating!");
        input.trigger(Key.SPACE);
    }
}
