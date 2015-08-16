package org.arabellan.tetris;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.arabellan.lwjgl.LWJGLWrapper;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;

@Slf4j
public class Main {

    public static void main(String[] args) {
        log.debug("Main entered");
        LWJGLWrapper lwjgl = new LWJGLWrapper();
        lwjgl.initialize();
        Injector injector = Guice.createInjector(new GuiceModule());
        injector.getInstance(Game.class).run();
        lwjgl.shutdown();
        log.debug("Main exiting");
    }
}
