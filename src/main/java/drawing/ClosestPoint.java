package drawing;

/**
 * Used to keep track of the point closest to the selected point of a figure
 * in an {@link AbstractShapePanel} when selected using a mouse.
 */
enum ClosestPoint {
    TOP_LEFT(0),
    TOP_RIGHT(1),
    BOTTOM_LEFT(2),
    BOTTOM_RIGHT(3),
    // Extra points for drawing.HexagonPanel
    HEXAGON_POINT_1(4),
    HEXAGON_POINT_2(5),
    CENTER(6); // XXX: Always keep as the last value

    private int i;

    ClosestPoint(int i) {
        this.i = i;
    }

    /**
     * Get the enum from its value
     *
     * @param i the value to be checked
     * @return the {@link ClosestPoint} with the matching value. null if there is no such point.
     */
    static ClosestPoint valueOf(int i) {
        var values = values();
        for (ClosestPoint value : values) {
            if (value.getValue() == i) {
                return value;
            }
        }
        return null;
    }

    /**
     * Get the value of the enum
     *
     * @return the value of the enum
     */
    int getValue() {
        return i;
    }
}
