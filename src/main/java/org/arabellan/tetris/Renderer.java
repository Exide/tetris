package org.arabellan.tetris;

import lombok.extern.slf4j.Slf4j;
import org.arabellan.common.Matrix;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class Renderer {

    private Matrix<Character> display;
    private int width, height;

    public void initialize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void draw(Scene scene) {
        clearDisplay();
        addSceneObjectsToDisplay(scene);
        render();
    }

    private void clearDisplay() {
        Character[][] emptyMatrix = new Character[height][width];
        for (Character[] row : emptyMatrix) {
            Arrays.fill(row, null);
        }
        display = new Matrix<>(emptyMatrix);
    }

    private void addSceneObjectsToDisplay(Scene scene) {
        List<Renderable> objects = scene.getRenderables();
        objects.forEach((o) -> display.add(o.getRenderable(), o.getPosition()));
    }

    private void render() {
        log.debug("\n" + display.replace(null, ' ').toString());
    }
}
