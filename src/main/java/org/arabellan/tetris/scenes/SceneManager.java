package org.arabellan.tetris.scenes;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.events.ChangeSceneEvent;

/**
 * This class is responsible for the current scene.
 */
@Slf4j
public class SceneManager {

    @Inject
    private SceneFactory factory;
    private Scene scene;

    @Inject
    public SceneManager(EventBus eventBus) {
        log.debug("Constructing SceneManager");
        eventBus.register(new ChangeSceneListener());
    }

    public void initialize() {
        log.debug("Initializing SceneManager");
        setScene(MainMenuScene.class);
    }

    public void update(double delta) {
        scene.update(delta);
    }

    private void setScene(Class sceneClass) {
        scene = factory.get(sceneClass);
        scene.initialize();
    }

    private class ChangeSceneListener {
        @Subscribe
        public void listen(ChangeSceneEvent event) {
            log.debug("ChangeSceneEvent received");
            setScene(event.getSceneClass());
        }
    }
}
