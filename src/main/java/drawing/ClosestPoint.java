package drawing;

/**
 * Used to keep track of the point closest to the selected point of a figure
 * in an {@link AbstractShapePanel} when selected using a mouse.
 */
enum ClosestPoint {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    CENTER
}
