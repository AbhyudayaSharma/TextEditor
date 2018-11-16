package drawing;

import java.util.Locale;

enum Shape {
    RECTANGLE,
    CIRCLE,
    OVAL,
    TRIANGLE,
    SQUARE;

    @Override
    final public String toString() {
        var name = super.toString();
        return name.charAt(0) + name.substring(1).toLowerCase(Locale.US);
    }
}
