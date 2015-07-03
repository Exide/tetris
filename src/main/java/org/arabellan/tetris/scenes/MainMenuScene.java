package org.arabellan.tetris.scenes;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.Controller;
import org.arabellan.tetris.Controller.Key;
import org.arabellan.tetris.Renderable;
import org.arabellan.tetris.Scene;
import org.arabellan.ui.Label;
import org.arabellan.tetris.events.ChangeSceneEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
public class MainMenuScene implements Scene {

    private Label titleLabel;

    @Inject
    private Controller input;

    @Override
    public void initialize() {
        log.debug("Initializing");
        initializeInput();
        initializeGameObjects();
    }

    private void initializeGameObjects() {
        titleLabel = new Label("Main Menu");
    }

    private void initializeInput() {
        input.bind(Key.SPACE, new ChangeSceneEvent(InGameScene.class));
    }

    @Override
    public void update() {
        log.info("Updating!");
        input.trigger(Key.SPACE);
    }

    @Override
    public void cleanup() {
        log.debug("Cleaning up");
        input.clearBindings();
    }

    @Override
    public List<Renderable> getRenderables() {
        return Arrays.asList(titleLabel);
    }
}
