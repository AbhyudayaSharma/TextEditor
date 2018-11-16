package drawing;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

class CirclePanel extends AbstractShapePanel {
    private final Circle circle = new Circle(100, new Point(200, 200));
    private ClosestPoint closestPoint = null;

    CirclePanel() {
        super();
        Point initialPoint = new Point();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (circle.containsPoint(e.getPoint())) {
                    initialPoint.x = e.getX();
                    initialPoint.y = e.getY();

                    if (circle.distanceFromCenter(initialPoint) < circle.distanceFromCircumference(initialPoint)) {
                        closestPoint = ClosestPoint.CENTER;
                    } else {
//                        closestPoint = ClosestPoint.CIRCUMFERENCE;
                        var angle = circle.getRelativeAngle(e.getPoint());
                        if (angle >= 0 && angle <= 90) {
                            closestPoint = ClosestPoint.TOP_RIGHT;
                        } else if (angle >= 90 && angle <= 180) {
                            closestPoint = ClosestPoint.TOP_LEFT;
                        } else if (angle >= 180 && angle <= 270) {
                            closestPoint = ClosestPoint.BOTTOM_LEFT;
                        } else {
                            closestPoint = ClosestPoint.BOTTOM_RIGHT;
                        }
                    }
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
                    circle.center.x += dx;
                    circle.center.y += dy;
                } else {
                    double dr = Math.hypot(dx, dy);
                    if (closestPoint == ClosestPoint.TOP_RIGHT || closestPoint == ClosestPoint.BOTTOM_LEFT) {
                        dr = Math.copySign(dr, dx);
                    } else {
                        dr = Math.copySign(dr, dy);
                    }

                    if (closestPoint == ClosestPoint.BOTTOM_RIGHT || closestPoint == ClosestPoint.TOP_RIGHT) {
                        circle.grow(dr);
                    } else {
                        circle.grow(-dr);
                    }
                }

                repaint();
            }
        });
    }

    @Override
    protected void draw(Graphics g) {
        g.drawOval(circle.center.x - circle.radius, circle.center.y - circle.radius,
                circle.diameter, circle.diameter);
    }

    private static class Circle {
        private int radius;
        private int diameter;
        private Point center;

        private Circle(int diameter, Point center) {
            this.diameter = diameter;
            this.radius = diameter / 2;
            this.center = center;
        }

        private boolean containsPoint(Point p) {
            var distance = Point.distance(p.x, p.y, center.x, center.y);
            return distance <= radius;
        }

        private double distanceFromCenter(Point p) {
            return Point.distance(center.x, center.y, p.x, p.y);
        }

        private double distanceFromCircumference(Point p) {
            double ret;
            if (containsPoint(p)) {
                ret = radius - distanceFromCenter(p);
            } else {
                ret = Point.distance(center.x, center.y, p.x, p.y) - radius;
            }
            return ret;
        }

        private void grow(double x) {
            diameter += x;
            radius = diameter / 2;
        }

        /**
         * Calculates the angle (with respect to the +x axis) of the line
         * joining the centre of the circle to the specified point.
         *
         * @param p the point
         * @return the angle of p with respect to +x axis in degrees between 0 and 360 degrees
         */
        private double getRelativeAngle(Point p) {
            double px = p.x;
            // division by zero
            if (p.x == center.x) {
                px += 0.001;
            }
            double py = p.y;
            double cx = center.x;
            double cy = center.y;

            var slope = -(py - cy) / (px - cx); // negative due to inverted y in Swing
            var degrees = Math.abs(Math.toDegrees(Math.atan(slope))); // 0 -> 90 degrees
            if (px >= cx && py <= cy) { // first quadrant
                return degrees;
            } else if (px <= cx && py <= cy) { // second quadrant
                return 180 - degrees;
            } else if (px <= cx && py >= cy) { // third quadrant
                return 180 + degrees;
            } else { // fourth quadrant
                return 360 - degrees;
            }
        }
    }
}
