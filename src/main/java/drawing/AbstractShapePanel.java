package drawing;

import javax.swing.*;
import java.awt.*;

abstract class AbstractShapePanel extends JPanel {
    AbstractShapePanel() {
        super();
        setBorder(BorderFactory.createEtchedBorder());
    }

    @Override
    final public LayoutManager getLayout() {
        return null;
    }

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
