package com.abhyudayasharma.texteditor.drawing;

import java.util.Locale;

/**
 * Shapes supported by {@link DrawingPanel}
 */
enum Shape {
    RECTANGLE,
    CIRCLE,
    OVAL,
    TRIANGLE,
    HEXAGON;

    @Override
    final public String toString() {
        var name = super.toString();
        return name.charAt(0) + name.substring(1).toLowerCase(Locale.US);
    }
}
