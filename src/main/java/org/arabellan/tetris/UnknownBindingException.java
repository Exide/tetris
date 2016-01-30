package org.arabellan.tetris;

public class UnknownBindingException extends RuntimeException {

    public UnknownBindingException(int glfwKeyConstant) {
        super(String.valueOf(glfwKeyConstant));
    }
}
