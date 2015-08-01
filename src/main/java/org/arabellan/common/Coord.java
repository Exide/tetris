package org.arabellan.common;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Coord {
    double x, y, z;

    public Coord translate(double x, double y, double z) {
        return Coord.builder()
                .x(this.x + x)
                .y(this.y + y)
                .z(this.z + z)
                .build();
    }

    public Coord translate(double x, double y) {
        return translate(x, y, 0);
    }

    public Coord translate(Coord coord) {
        return translate(coord.getX(), coord.getY());
    }
}
