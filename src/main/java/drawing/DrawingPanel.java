package drawing;

import javax.swing.*;
import java.awt.*;

public class DrawingPanel extends JPanel {
    public DrawingPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        var radioPanel = new JPanel();
        radioPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JComboBox<String> comboBox = new JComboBox<>(new String[]{
                "Rectangle",
                "Circle",
                "Triangle",
                "Square",
                "Oval",
        });

        radioPanel.add(new JLabel("Select a shape: "));
        radioPanel.add(comboBox);

        add(radioPanel);
        add(new RectanglePanel());
    }
}