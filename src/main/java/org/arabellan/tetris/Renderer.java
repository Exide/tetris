package org.arabellan.tetris;

public interface Renderer {
    void initialize(int width, int height);
    void draw(Scene scene);
}
