package org.arabellan.lwjgl;

import org.lwjgl.opengl.GLUtil;

import static org.lwjgl.opengl.GL11.glGetError;

public class GLException extends RuntimeException {
    GLException(int errorCode) {
        super(String.format("(%s) %s", errorCode, GLUtil.getErrorString(errorCode)));
    }

    public static void throwIfError() {
        int errorCode = glGetError();
        if (errorCode != 0) {
            throw new GLException(errorCode);
        }
    }
}
