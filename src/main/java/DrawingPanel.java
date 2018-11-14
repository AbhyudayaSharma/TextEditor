import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.TreeMap;

enum ClosestPoint {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    CENTER
}

class DrawingPanel extends JPanel {
    private final Rectangle rectangle = new Rectangle(50, 50, 50, 50);
    private ClosestPoint closestPoint = null;

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
                    var topLeftDistance = Math.hypot(initialPoint.x - rectangle.x, initialPoint.y - rectangle.y);
                    var bottomLeftDistance = Math.hypot(initialPoint.x - rectangle.x, initialPoint.y -
                            (rectangle.y + rectangle.width));
                    var topRightDistance = Math.hypot(initialPoint.x - (rectangle.x + rectangle.height),
                            initialPoint.y - rectangle.y);
                    var bottomRightDistance = Math.hypot(initialPoint.x - (rectangle.x + rectangle.width),
                            initialPoint.y - (rectangle.y + rectangle.width));
                    var centerDistance = Math.hypot(initialPoint.x - rectangle.getCenterX(),
                            initialPoint.y - rectangle.getCenterY());
                    var distanceMap = new TreeMap<Double, ClosestPoint>();
                    distanceMap.put(topLeftDistance, ClosestPoint.TOP_LEFT);
                    distanceMap.put(topRightDistance, ClosestPoint.TOP_RIGHT);
                    distanceMap.put(bottomLeftDistance, ClosestPoint.BOTTOM_LEFT);
                    distanceMap.put(bottomRightDistance, ClosestPoint.BOTTOM_RIGHT);
                    distanceMap.put(centerDistance, ClosestPoint.CENTER);
                    closestPoint = distanceMap.firstEntry().getValue();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                closestPoint = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (closestPoint == null) {
                    return;
                }
                var dx = e.getX() - initialPoint.getX();
                var dy = e.getY() - initialPoint.getY();
                initialPoint.x = e.getX();
                initialPoint.y = e.getY();

                if (closestPoint == ClosestPoint.BOTTOM_RIGHT) {
                    rectangle.width += dx;
                    rectangle.height += dy;
                } else if (closestPoint == ClosestPoint.TOP_LEFT) {
                    rectangle.width -= dx;
                    rectangle.height -= dy;
                    rectangle.x += dx;
                    rectangle.y += dy;
                } else if (closestPoint == ClosestPoint.TOP_RIGHT) {
                    rectangle.width += dx;
                    rectangle.height -= dy;
                    rectangle.y += dy;
                } else if (closestPoint == ClosestPoint.BOTTOM_LEFT) {
                    rectangle.x += dx;
                    rectangle.width -= dx;
                    rectangle.height += dy;
                } else {
                    rectangle.x += dx;
                    rectangle.y += dy;
                }

                var newClosestPointName = new StringBuilder();

                if (rectangle.width <= 0 || rectangle.height <= 0) {
                    if (dy > 0) {
                        newClosestPointName.append("BOTTOM");
                    } else {
                        newClosestPointName.append("TOP");
                    }

                    newClosestPointName.append("_");

                    if (dx > 0) {
                        newClosestPointName.append("RIGHT");
                    } else {
                        newClosestPointName.append("LEFT");
                    }

                    closestPoint = ClosestPoint.valueOf(newClosestPointName.toString());
                }
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