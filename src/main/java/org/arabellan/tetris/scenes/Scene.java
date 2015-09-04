package org.arabellan.tetris.scenes;

import org.arabellan.tetris.Renderable;
import java.util.List;

public interface Scene {
    void initialize();
    void update();
    void cleanup();
    List<Renderable> getRenderables();
}
