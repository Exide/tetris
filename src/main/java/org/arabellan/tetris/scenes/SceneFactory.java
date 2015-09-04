package org.arabellan.tetris.scenes;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.tetris.GuiceModule.InGame;
import org.arabellan.tetris.GuiceModule.MainMenu;

/**
 * This class is responsible for creating Scenes
 */
@Slf4j
public class SceneFactory {

    @Inject @MainMenu Scene mainMenu;
    @Inject @InGame Scene inGame;

    public Scene get(Class sceneClass) {
        log.debug("Getting scene: " + sceneClass.getSimpleName());
        if (sceneClass.equals(mainMenu.getClass())) {
            return mainMenu;
        } else if (sceneClass.equals(inGame.getClass())) {
            return inGame;
        } else {
            throw new IllegalArgumentException("Unknown scene: " + sceneClass.getSimpleName());
        }
    }
}
