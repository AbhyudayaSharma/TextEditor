import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

class DrawingPanel extends JPanel {
    private final Rectangle rectangle = new Rectangle(50, 50, 50, 50);
    DrawingPanel() {
        super();
        setLayout(null);
        setBorder(BorderFactory.createEtchedBorder());
        Point initialPoint = new Point();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (rectangle.contains(e.getPoint())) {
                    initialPoint.x = e.getX();
                    initialPoint.y = e.getY();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                var dx = e.getX() - initialPoint.getX();
                var dy = e.getY() - initialPoint.getY();
                initialPoint.x = e.getX();
                initialPoint.y = e.getY();
                rectangle.width += dx;
                rectangle.height += dy;
                repaint();
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 400);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
}
