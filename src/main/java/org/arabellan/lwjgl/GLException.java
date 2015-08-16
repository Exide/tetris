package org.arabellan.lwjgl;

import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GLContext.translateGLErrorString;

public class GLException extends RuntimeException {
    GLException(int errorCode) {
        super(String.format("(%s) %s", errorCode, translateGLErrorString(errorCode)));
    }

    public static void throwIfError() {
        int errorCode = glGetError();
        if (errorCode != 0) {
            throw new GLException(errorCode);
        }
    }
}
