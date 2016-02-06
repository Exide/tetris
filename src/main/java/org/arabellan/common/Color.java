package org.arabellan.common;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Color {
    float r, g, b, a;

    public Color fromBytes(byte r, byte g, byte b, byte a) {
        return Color.builder()
                .r(mapFromByte(r))
                .g(mapFromByte(g))
                .b(mapFromByte(b))
                .a(mapFromByte(a))
                .build();
    }

    public byte[] toBytes() {
        return new byte[]{
                mapToByte(r),
                mapToByte(g),
                mapToByte(b),
                mapToByte(a)
        };
    }

    private float mapFromByte(byte value) {
        // uses a linear transform: Y = (X-A)/(B-A) * (D-C) + C
        return (float) ((value - -127) / (-127 - 127) * (1.0 - 0.0) + 0.0);
    }

    private byte mapToByte(float value) {
        // uses a linear transform: Y = (X-A)/(B-A) * (D-C) + C
        return (byte) ((value - 0.0) / (0.0 - 1.0) * (127 - -127) + -127);
    }
}
