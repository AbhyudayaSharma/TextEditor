package drawing;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.TreeMap;

/**
 * A panel which draws a rectangle. The size and position of the rectangle can be
 * modified by using the mouse.
 */
class RectanglePanel extends AbstractShapePanel {
    private final Rectangle rectangle = new Rectangle(120, 150, 160, 90); // 16:9 ftw
    private ClosestPoint closestPoint = null;

    RectanglePanel() {
        super();
        Point initialPoint = new Point();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (rectangle.contains(e.getPoint())) {
                    initialPoint.x = e.getX();
                    initialPoint.y = e.getY();
                    var topLeftDistance = Point.distance(initialPoint.x, initialPoint.y, rectangle.x, rectangle.y);
                    var topRightDistance = Point.distance(initialPoint.x, initialPoint.y,
                            rectangle.x + rectangle.width, rectangle.y);
                    var bottomLeftDistance = Point.distance(initialPoint.x, initialPoint.y,
                            rectangle.x, rectangle.y + rectangle.height);
                    var bottomRightDistance = Point.distance(initialPoint.x, initialPoint.y,
                            rectangle.x + rectangle.width, rectangle.y + rectangle.height);
                    var centerDistance = Point.distance(initialPoint.x, initialPoint.y,
                            rectangle.getCenterX(), rectangle.getCenterY());

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
    protected void draw(Graphics g) {
        g.drawString("Start dragging near the red circle to move the rectangle", 10, 20);
        g.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        g.setColor(Color.RED);
        g.fillOval((int) rectangle.getCenterX(), (int) rectangle.getCenterY(), 5, 5);
        g.setColor(Color.BLACK);
    }

    /**
     * Returns the rectangle currently being drawn on the panel
     *
     * @return the rectangle being drawn currently
     */
    Rectangle getRectangle() {
        return rectangle;
    }
}