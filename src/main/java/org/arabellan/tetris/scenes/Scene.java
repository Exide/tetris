package org.arabellan.tetris.scenes;

public interface Scene {
    void initialize();
    void update(double delta);
    void cleanup();
}
