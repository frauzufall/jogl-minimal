package bvv.core.render;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.nio.ByteBuffer;

public class TransferFunctionWidget extends JPanel {
    private TransferFunctionTexture transferFunction;
    private final int[] texture;
    private Point[] points;
    private int selectedPointIndex = -1;
    private static final int POINT_RADIUS = 8;
    private static final int POINT_DIAMETER = POINT_RADIUS * 2;

    public TransferFunctionWidget(TransferFunctionTexture texture) {
        super();
        this.transferFunction = texture;
        this.texture = new int[transferFunction.texWidth()];
        points = new Point[5];
        points[0] = new Point(0, transferFunction.texHeight() - 1);
        points[1] = new Point(transferFunction.texWidth() / 4, transferFunction.texHeight() / 4);
        points[2] = new Point(transferFunction.texWidth() / 2, 0);
        points[3] = new Point(3 * transferFunction.texWidth() / 4, transferFunction.texHeight() / 4);
        points[4] = new Point(transferFunction.texWidth() - 1, transferFunction.texHeight() - 1);
        setPreferredSize(new Dimension(transferFunction.texWidth(), transferFunction.texHeight()));
        setOpaque(false);
        updateTexture();
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                for (int i = 0; i < points.length; i++) {
                    if (contains(e.getX(), e.getY(), points[i])) {
                        selectedPointIndex = i;
                        return;
                    }
                }
//                addPoint(e.getX(), e.getY());
//                selectedPointIndex = points.length - 1;
            }

            public void mouseReleased(MouseEvent e) {
                selectedPointIndex = -1;
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (selectedPointIndex >= 0) {
                    int minx = 0;
                    int maxx = transferFunction.texWidth() - 1;
                    if(selectedPointIndex > 0) minx = points[selectedPointIndex-1].x;
                    if(selectedPointIndex < points.length-1) maxx = points[selectedPointIndex+1].x;
                    points[selectedPointIndex].x = Math.min(Math.max(minx, e.getX()), maxx);
                    points[selectedPointIndex].y = Math.min(Math.max(0, e.getY()), transferFunction.texHeight() - 1);
                    repaint();
                    updateTexture();
                }
            }
        });
    }

    private boolean contains(int x, int y, Point p) {
        return p.distance(x, y) <= POINT_RADIUS;
    }

//    private void addPoint(int x, int y) {
//        Point[] newPoints = new Point[points.length + 1];
//        for (int i = 0; i < points.length; i++) {
//            newPoints[i] = points[i];
//        }
//        newPoints[points.length] = new Point(x, y);
//        points = newPoints;
//    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw background with same brightness as transfer function
        for (int i = 0; i < transferFunction.texWidth(); i++) {
            float t = (float)i / (transferFunction.texWidth() - 1);
            int y = getY(t);
            int brightness = (int)(255-255 * (y / (float)(transferFunction.texHeight() - 1)));
            g2d.setColor(new Color(brightness, brightness, brightness));
            g2d.fillRect(i, 0, 1, transferFunction.texHeight());
        }

        // draw transfer function curve
        g2d.setColor(Color.BLACK);
        Path2D.Float curve = new Path2D.Float();
        curve.moveTo(0, points[0].y);
        for (int i = 0; i < points.length - 1; i++) {
            Point p1 = points[i];
            Point p2 = points[i + 1];
            float x1 = p1.x;
            float x2 = p2.x;
            float y1 = p1.y;
            float y2 = p2.y;
            float dx = x2 - x1;
            float dy = y2 - y1;
            curve.curveTo(x1 + dx / 3, y1 + dy / 3, x1 + 2 * dx / 3, y1 + 2 * dy / 3, x2, y2);
        }
        g2d.draw(curve);

        // draw control points
        g2d.setColor(Color.RED);
        for (int i = 0; i < points.length; i++) {
            Point p = points[i];
            g2d.fillOval(p.x - 4, p.y - 4, 8, 8);
        }
    }
    private int getY(float t) {
        if (points.length == 1) {
            return (int) (transferFunction.texHeight() * points[0].y / (float) (transferFunction.texHeight() - 1));
        }
        t = t * transferFunction.texWidth();
        for (int i = 0; i < points.length - 1; i++) {
            int p_x = points[i].x;
            if(i == 0 && t < p_x) {
                return points[i].y;
            } else if(t > p_x && i == points.length-1) {
                return points[i].y;
            } else if(t >= p_x && t <= points[i+1].x) {
                float a = (t - p_x) / (float)(points[i+1].x - p_x);
                int y1 = points[i].y;
                int y2 = points[i + 1].y;
                return (int) (y1 + (y2 - y1) * a);
            }
        }
        return 0;
    }

    private void updateTexture() {
        for (int i = 0; i < transferFunction.texWidth(); i++) {
            float t = (float) i / (transferFunction.texWidth() - 1);
            int y = getY(t);
            texture[i] = (int) (255 * (y / (float) (transferFunction.texHeight() - 1)));
        }
        ByteBuffer buffer = transferFunction.getData();
        for (int y = 0; y < transferFunction.texHeight(); y++) {
            for (int x = 0; x < transferFunction.texWidth(); x++) {
                float value = 255-texture[x];
                buffer.put((byte)(value));
                buffer.put((byte)(value));
                buffer.put((byte)(value));
                buffer.put((byte)255);
            }
        }
        buffer.flip();
        transferFunction.setPleaseUpdate(true);
    }
}