/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1;

import attractors1.math.Point3d;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author ashmore
 */
public abstract class RendererPanel extends JPanel implements Renderer {
  private List<Point3d> points;
  private BufferedImage image;

  public RendererPanel() {
    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
      }
    });
  }

  @Override
  public void setPoints(List<Point3d> points) {
    this.points = points;
    repaint();
  }

  protected abstract void paintPoints(Graphics2D g, List<Point3d> points);

  @Override
  protected void paintComponent(Graphics g) {
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, getWidth(), getHeight());

    if(points == null) {
      return;
    }

    g.drawImage(image, 0, 0, this);
    paintPoints((Graphics2D)image.getGraphics(), points);
  }
}
