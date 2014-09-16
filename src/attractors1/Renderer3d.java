/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1;

import attractors1.math.Point3d;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author ashmore
 */
public class Renderer3d extends RendererPanel {
  private static final double MAX_DISTANCE = 12;
  private static final double MIN_DISTANCE = 4;

  private static final int DOT_SIZE = 5;
  private static final int POINTS_TO_RENDER_PER_FRAME = 1000;

  private double yaw = 0;
  private double pitch = 0;
  private double distance = 8;
  private double fov = Math.PI/6;

  private int renderedPoints = 0;

  public Renderer3d() {
    MouseControl controller = new MouseControl();
    addMouseListener(controller);
    addMouseMotionListener(controller);
    addMouseWheelListener(controller);
  }

  @Override
  public void setPoints(List<Point3d> points) {
    super.setPoints(points);
    renderedPoints = 0;
  }

  private class MouseControl extends MouseAdapter {
    private int lastX, lastY;
    private float sensitivity = .01f;

    @Override
    public void mousePressed(MouseEvent e) {
      lastX = e.getX();
      lastY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      int currentX = e.getX();
      int currentY = e.getY();

      float dx = -sensitivity*(currentX - lastX);
      float dy = -sensitivity*(currentY - lastY);

      yaw += dx;
      pitch = Math.max(-Math.PI/2, Math.min(Math.PI/2, pitch+dy));
      repaint();
      renderedPoints = 0;

      lastX = currentX;
      lastY = currentY;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
      double rotation = e.getPreciseWheelRotation();
      double newDistance = distance + rotation * .5;
      newDistance = Math.max(Math.min(newDistance, MAX_DISTANCE), MIN_DISTANCE);

      if(newDistance != distance) {
        distance = newDistance;
        repaint();
        renderedPoints = 0;
      }
    }
  }

  @Override
  protected void paintPoints(Graphics2D g, List<Point3d> points) {

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    if(renderedPoints == 0) {
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, getWidth(), getHeight());
    }

    // camera position
    Point3d c = new Point3d(
            Math.cos(yaw)*Math.cos(pitch),
            Math.sin(pitch),
            Math.sin(yaw)*Math.cos(pitch)).multiply(-distance);

    // todo: make color scheme gray out slightly based on distance

    // points at 0
    Point3d delta = c.multiply(-1).normalize();
    Point3d right = delta.cross(Point3d.UNIT_Y).normalize();
    Point3d up = right.cross(delta).normalize();

    for(int i=renderedPoints; i<renderedPoints+POINTS_TO_RENDER_PER_FRAME;i++) {
//    for(int i=0;i<points.size();i++) {
      if(i>=points.size()) {
        // return and don't re-render
        break;
      }
      Point3d point = points.get(i);
      float progress = ((float)i) / points.size();

      Point3d difference = point.subtract(c);
      float d = (float) difference.dot(delta);

      // x and y in projected space. 0,0 represents center of view
      float x = (float) -difference.dot(right) / d;
      float y = (float) -difference.dot(up) / d;

      // apply fov
      x /= Math.sin(fov/2);
      y /= Math.sin(fov/2);

      // move to center of viewport
      x += .5f;
      y += .5f;

      // with a distance of 8, d tends to range between 6 and 9.
      float blue = 1 - Math.min(1,Math.max(0,(d - 6)/3));
      g.setColor(new Color(1, progress, blue));
      g.fillOval((int) (getWidth()*x - DOT_SIZE/2),
              (int) (getHeight()*y - DOT_SIZE/2),
              DOT_SIZE, DOT_SIZE);
    }

    renderedPoints += POINTS_TO_RENDER_PER_FRAME;
    if(renderedPoints < points.size()) {
      repaint();
    }
  }
}
