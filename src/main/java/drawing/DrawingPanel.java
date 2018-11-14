package drawing;

import javax.swing.*;

public class DrawingPanel extends JPanel {
    public DrawingPanel() {
        add(new RectanglePanel());
    }
}