package org.arabellan.tetris;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Singleton;
import org.arabellan.tetris.scenes.InGameScene;
import org.arabellan.tetris.scenes.MainMenuScene;
import org.arabellan.tetris.scenes.Scene;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class GuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(EventBus.class).in(Singleton.class);
        bind(Scene.class).annotatedWith(MainMenu.class).to(MainMenuScene.class);
        bind(Scene.class).annotatedWith(InGame.class).to(InGameScene.class);
    }

    @BindingAnnotation
    @Retention(RUNTIME)
    public @interface MainMenu {}

    @BindingAnnotation
    @Retention(RUNTIME)
    public @interface InGame {}
}
