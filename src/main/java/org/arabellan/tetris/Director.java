package org.arabellan.tetris;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.events.ChangeSceneEvent;
import org.arabellan.tetris.scenes.MainMenuScene;
import org.arabellan.tetris.scenes.Scene;
import org.arabellan.tetris.scenes.SceneFactory;

/**
 * This class is responsible for the current scene.
 */
@Slf4j
public class Director {

    @Inject
    private SceneFactory factory;

    @Getter
    private Scene scene;

    @Setter
    private Class queuedScene;

    @Inject
    public Director(EventBus eventBus) {
        log.debug("Constructing");
        eventBus.register(new ChangeSceneListener());
    }

    public void initialize() {
        log.debug("Initializing");
        setQueuedScene(MainMenuScene.class);
    }

    public void update() {
        enableQueuedSceneIfAvailable();
        scene.update();
    }

    private void enableQueuedSceneIfAvailable() {
        if (queuedScene != null) {
            setScene(queuedScene);
            queuedScene = null;
        }
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
            setQueuedScene(event.getSceneClass());
        }
    }
}
