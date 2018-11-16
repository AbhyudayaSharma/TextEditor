package drawing;

import javax.swing.*;
import java.awt.*;

public class DrawingPanel extends JPanel {
    private AbstractShapePanel shapePanel;

    public DrawingPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        var radioPanel = new JPanel();
        radioPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        shapePanel = new RectanglePanel();

        var comboBox = new JComboBox<>(new Shape[]{
                Shape.RECTANGLE,
                Shape.SQUARE,
                Shape.TRIANGLE,
                Shape.OVAL,
                Shape.CIRCLE,
        });

        comboBox.addActionListener(e -> {
            Shape item = (Shape) comboBox.getSelectedItem();
            if (item == null) return;

            switch (item) {
                case RECTANGLE:
                    if (!(shapePanel instanceof RectanglePanel)) changeShapePanel(new RectanglePanel());
                    break;
                case CIRCLE:
                    if (!(shapePanel instanceof CirclePanel)) changeShapePanel(new CirclePanel());
                    break;
                default:
                    System.out.println("Not yet supported");
            }
        });

        radioPanel.add(new JLabel("Select a shape: "));
        radioPanel.add(comboBox);

        add(radioPanel);
        add(shapePanel);
    }

    private void changeShapePanel(AbstractShapePanel shape) {
        remove(shapePanel);
        shapePanel = shape;
        add(shapePanel);
        revalidate();
        repaint();
    }
}