package org.arabellan.common;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Color {
    float r, g, b, a;
}
