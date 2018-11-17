package drawing;

import javax.swing.*;
import java.awt.*;

/**
 * Panel which supports mouse-driven change in position and size of various shapes
 *
 * @see Shape for supported shapes.
 */
public class DrawingPanel extends JPanel {
    private AbstractShapePanel shapePanel;

    public DrawingPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        var radioPanel = new JPanel();
        radioPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        shapePanel = new OvalPanel();

        var comboBox = new JComboBox<>(new Shape[]{
                Shape.OVAL,
                Shape.RECTANGLE,
                Shape.CIRCLE,
                Shape.TRIANGLE,
                Shape.HEXAGON,
        });

        comboBox.addActionListener(e -> {
            Shape item = (Shape) comboBox.getSelectedItem();
            if (item == null) return;

            switch (item) {
                case RECTANGLE:
                    // OvalPanel is a RectanglePanel
                    if (!(shapePanel instanceof RectanglePanel) || shapePanel instanceof OvalPanel) {
                        changeShapePanel(new RectanglePanel());
                    }
                    break;
                case CIRCLE:
                    if (!(shapePanel instanceof CirclePanel)) changeShapePanel(new CirclePanel());
                    break;
                case TRIANGLE:
                    if (!(shapePanel instanceof TrianglePanel)) changeShapePanel(new TrianglePanel());
                    break;
                case HEXAGON:
                    if (!(shapePanel instanceof HexagonPanel)) changeShapePanel(new HexagonPanel());
                    break;
                case OVAL:
                    if (!(shapePanel instanceof OvalPanel)) changeShapePanel(new OvalPanel());
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

    /**
     * Changes the currently drawn shapePanel
     *
     * @param shape the new {@link AbstractShapePanel} to draw
     */
    private void changeShapePanel(AbstractShapePanel shape) {
        remove(shapePanel);
        shapePanel = shape;
        add(shapePanel);
        revalidate();
        repaint();
    }
}