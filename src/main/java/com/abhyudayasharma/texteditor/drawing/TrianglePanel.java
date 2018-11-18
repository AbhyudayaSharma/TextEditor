package com.abhyudayasharma.texteditor.drawing;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * A panel which draws a triangle. The size and position of the triangle can be
 * modified by using the mouse.
 */
class TrianglePanel extends AbstractShapePanel {
    private final Polygon triangle = new Polygon(new int[]{100, 200, 300}, new int[]{300, 100, 250}, 3);

    /**
     * We use TOP_LEFT, TOP_RIGHT, and BOTTOM_LEFT for the three points, irrespective of their
     * positions for easy indexing of the arrays. CENTER is used if closest to the center of the bounding box.
     * In order to maintain consistency, we keep the length of the array equal to the number of
     * constants in {@link ClosestPoint}
     */
    private ClosestPoint closestPoint = null;

    TrianglePanel() {
        super();
        Point initialPoint = new Point();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (triangle.contains(e.getPoint())) {
                    initialPoint.x = e.getX();
                    initialPoint.y = e.getY();

                    // array indexed on values of the ClosestPoint
                    var pointDistances = new double[ClosestPoint.values().length];

                    // set all to max value
                    for (int i = 0; i < pointDistances.length; i++) {
                        pointDistances[i] = Double.MAX_VALUE;
                    }

                    // distance from each point
                    var bounds = triangle.getBounds();
                    pointDistances[ClosestPoint.CENTER.getValue()] = Point.distance(e.getX(), e.getY(),
                            bounds.getCenterX(), bounds.getCenterY());

                    pointDistances[ClosestPoint.TOP_LEFT.getValue()] = Point.distance(e.getX(), e.getY(),
                            triangle.xpoints[0], triangle.ypoints[0]);

                    pointDistances[ClosestPoint.TOP_RIGHT.getValue()] = Point.distance(e.getX(), e.getY(),
                            triangle.xpoints[1], triangle.ypoints[1]);

                    pointDistances[ClosestPoint.BOTTOM_LEFT.getValue()] = Point.distance(e.getX(), e.getY(),
                            triangle.xpoints[2], triangle.ypoints[2]);

                    // get minimum value
                    int minimumIndex = Integer.MAX_VALUE;
                    double minimumValue = Double.MAX_VALUE;
                    for (int i = 0; i < pointDistances.length; i++) {
                        if (pointDistances[i] < minimumValue) {
                            minimumIndex = i;
                            minimumValue = pointDistances[i];
                        }
                    }

                    closestPoint = ClosestPoint.valueOf(minimumIndex);
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
                if (closestPoint == null) return;

                var dx = e.getX() - initialPoint.getX();
                var dy = e.getY() - initialPoint.getY();

                initialPoint.x = e.getX();
                initialPoint.y = e.getY();

                if (closestPoint == ClosestPoint.CENTER) {
                    for (int i = 0; i < triangle.npoints; i++) {
                        triangle.xpoints[i] += dx;
                        triangle.ypoints[i] += dy;
                    }
                } else {
                    int value = closestPoint.getValue();
                    if (value > 3 - 1) return; // triangle has three points
                    triangle.xpoints[value] += dx;
                    triangle.ypoints[value] += dy;
                }

                triangle.invalidate();
                repaint();
            }
        });
    }

    @Override
    protected void draw(Graphics g) {
        g.drawString(polygonMovementInstructions, 10, 20);
        g.fillPolygon(triangle);
        var box = triangle.getBounds();
        g.setColor(Color.RED);
        g.fillOval((int) box.getCenterX(), (int) box.getCenterY(), 5, 5);
        g.setColor(Color.BLACK);
    }
}
