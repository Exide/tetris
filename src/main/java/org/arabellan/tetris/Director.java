package org.arabellan.tetris;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.Scene;
import org.arabellan.tetris.SceneFactory;
import org.arabellan.tetris.events.ChangeSceneEvent;
import org.arabellan.tetris.scenes.MainMenuScene;

/**
 * This class is responsible for the current scene.
 */
@Slf4j
public class Director {

    @Inject
    private SceneFactory factory;

    @Getter
    private Scene scene;

    @Inject
    public Director(EventBus eventBus) {
        log.debug("Constructing");
        eventBus.register(new ChangeSceneListener());
    }

    public void initialize() {
        log.debug("Initializing");
        setScene(MainMenuScene.class);
    }

    public void update() {
        scene.update();
    }

    private void setScene(Class sceneClass) {
        if (scene != null) scene.cleanup();
        scene = factory.get(sceneClass);
        scene.initialize();
    }

    public void shutdown() {
        if (scene != null) scene.cleanup();
    }

    private class ChangeSceneListener {
        @Subscribe
        public void listen(ChangeSceneEvent event) {
            log.debug("ChangeSceneEvent received");
            setScene(event.getSceneClass());
        }
    }
}
