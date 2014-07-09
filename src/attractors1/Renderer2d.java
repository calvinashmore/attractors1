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
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author ashmore
 */
public class Renderer2d extends RendererPanel {

  private static final int DOT_SIZE = 5;

  @Override
  protected void paintPoints(Graphics2D g, List<Point3d> points) {

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, getWidth(), getHeight());
    for(int i=0;i<points.size();i++) {
      Point3d point = points.get(i);
      float progress = ((float)i) / points.size();
      float x = (float) (point.getX()+1)/2;
      float y = (float) (point.getY()+1)/2;
      float z = (float) (point.getZ()+1)/2;

      //g.setColor(Color.WHITE);
      g.setColor(new Color(1, progress, z));
      g.fillOval((int) (getWidth()*x - DOT_SIZE/2),
              (int) (getHeight()*y - DOT_SIZE/2),
              DOT_SIZE, DOT_SIZE);
    }
  }
}
