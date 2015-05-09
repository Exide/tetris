package org.arabellan.tetris;

import java.util.List;

public interface Scene {
    void initialize();

    void update();

    void cleanup();

    List<Renderable> getRenderables();
}
