package org.arabellan.common.exceptions;

public class NotImplementedException extends RuntimeException {
    public NotImplementedException() {
        super("This functionality is not implemented!");
    }
}
