/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1;

import attractors1.math.Point3d;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author ashmore
 */
public abstract class RendererPanel extends JPanel {
  private List<Point3d> points;

  public void setPoints(List<Point3d> points) {
    this.points = points;
    repaint();
  }

  protected abstract void paintPoints(Graphics2D g, List<Point3d> points);

  @Override
  protected void paintComponent(Graphics g) {
    if (points == null)
      return;
    paintPoints((Graphics2D)g, points);
  }
}
