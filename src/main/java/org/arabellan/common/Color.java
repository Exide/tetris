package org.arabellan.common;

import lombok.Value;

@Value
public class Color {
    float r, g, b, a;

    public Color() {
        r = 0;
        g = 1;
        b = 0;
        a = 1;
    }
}
