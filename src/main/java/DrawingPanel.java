import javax.swing.*;
import java.awt.*;

class DrawingPanel extends JPanel {
    DrawingPanel() {
        super();
        setLayout(null);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 400);
    }
}
