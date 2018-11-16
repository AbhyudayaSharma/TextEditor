package drawing;

import javax.swing.*;
import java.awt.*;

/**
 * A common base for all the panels which draw a user-movable shape
 * specifying common properties.
 */
abstract class AbstractShapePanel extends JPanel {
    AbstractShapePanel() {
        super();
        setBorder(BorderFactory.createEtchedBorder());
    }

    @Override
    final public LayoutManager getLayout() {
        return null; // null layout for manual drawing
    }

    /**
     * Override to perform the drawing on the panel
     *
     * @param g the graphics
     */
    protected abstract void draw(Graphics g);

    @Override
    final protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    @Override
    final public Dimension getPreferredSize() {
        return new Dimension(400, 400);
    }
}
