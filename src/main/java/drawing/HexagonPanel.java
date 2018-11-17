package drawing;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Collections;

class HexagonPanel extends AbstractShapePanel {
    private final Polygon hexagon = new Polygon(new int[]{125, 175, 225, 275, 225, 175},
            new int[]{200, 150, 150, 200, 250, 250, 200}, 6);

    /**
     * Just like in {@link TrianglePanel}, other than {@code ClosestPoint.CENTER}, the names of the {@link ClosestPoint}
     * have no meaning. They are just enumerated constants with integral values
     */
    private ClosestPoint closestPoint = null;

    HexagonPanel() {
        super();
        Point initialPoint = new Point();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (hexagon.getBounds().contains(e.getPoint())) {
                    initialPoint.x = e.getX();
                    initialPoint.y = e.getY();
                    closestPoint = ClosestPoint.CENTER;

                    var pointDistances = new ArrayList<Double>(ClosestPoint.values().length);

                    // set all to maximum value
                    for (int i = 0; i < ClosestPoint.values().length; i++) {
                        pointDistances.add(Double.MAX_VALUE);
                    }

                    // assign values
                    for (int i = 0; i < pointDistances.size(); i++) {
                        if (i == ClosestPoint.CENTER.getValue()) {
                            var box = hexagon.getBounds();
                            pointDistances.set(i,
                                    Point.distance(e.getX(), e.getY(), box.getCenterX(), box.getCenterY()));
                            continue;
                        }
                        pointDistances.set(i,
                                Point.distance(e.getX(), e.getY(), hexagon.xpoints[i], hexagon.ypoints[i]));
                    }

                    // get minimum value
                    closestPoint = ClosestPoint.valueOf((pointDistances.indexOf(Collections.min(pointDistances))));
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

                var dx = e.getX() - initialPoint.x;
                var dy = e.getY() - initialPoint.y;

                initialPoint.x = e.getX();
                initialPoint.y = e.getY();

                if (closestPoint == ClosestPoint.CENTER) {
                    for (int i = 0; i < hexagon.npoints; i++) {
                        hexagon.xpoints[i] += dx;
                        hexagon.ypoints[i] += dy;
                    }
                } else {
                    var value = closestPoint.getValue();
                    if (value > 6 - 1) return; // hexagon has 6 points

                    hexagon.xpoints[value] += dx;
                    hexagon.ypoints[value] += dy;
                }

                hexagon.invalidate();
                repaint();
            }
        });
    }

    @Override
    protected void draw(Graphics g) {
        g.drawString(polygonMovementInstructions, 10, 20);
        g.fillPolygon(hexagon);
        var box = hexagon.getBounds();
        g.setColor(Color.RED);
        g.fillOval((int) box.getCenterX(), (int) box.getCenterY(), 5, 5);
        g.setColor(Color.BLACK);
    }
}
