package drawing;

import java.awt.*;

/**
 * A panel which draws an oval. The size and position of the oval can be
 * modified by using the mouse.
 */
public class OvalPanel extends RectanglePanel {
    /**
     * Creates an {@link OvalPanel}. Since an oval is drawn just like a {@link Rectangle} in AWT,
     * we can reuse the mouse motions used in {@link RectanglePanel} to draw the oval.
     */
    OvalPanel() {
        super();
    }

    @Override
    protected void draw(Graphics g) {
        g.drawString("Use the red circle to move the oval.\n", 10, 20);
        g.drawString("Use the blue circles to change its dimensions.", 10, 35);
        var rect = super.getRectangle();
        g.fillOval(rect.x, rect.y, rect.width, rect.height);

        // draw corner hints
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(rect.x, rect.y, rect.width, rect.height);
        g.setColor(Color.BLUE);
        g.drawOval(rect.x, rect.y, 5, 5); // remember something? Hamming codes?
        g.drawOval(rect.x, rect.y + rect.height - 5, 5, 5);
        g.drawOval(rect.x + rect.width - 5, rect.y + rect.height - 5, 5, 5);
        g.drawOval(rect.x + rect.width - 5, rect.y, 5, 5);

        // draw center hints
        g.setColor(Color.RED);
        g.fillOval((int) rect.getCenterX(), (int) rect.getCenterY(), 5, 5);
        g.setColor(Color.BLACK);

    }
}
