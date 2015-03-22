package org.arabellan.tetris;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) {
        log.debug("Main entered");
        Injector injector = Guice.createInjector(new GuiceModule());
        injector.getInstance(Game.class).start();
        log.debug("Main exiting");
    }
}
