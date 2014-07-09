/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1;

import attractors1.math.Point3d;
import attractors1.math.octree.OctreeIsoField;
import attractors1.math.octree.Octree;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author ashmore
 */
public class Graph {

  public static void main(String args[]) {

    JFrame frame = new JFrame("meef") {
      @Override
      public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.white);
        for(int i=0;i<getWidth();i++) {
          float x = ((float)i)/getWidth();
          float y = plot(x);
          int yy = (int) (y*getHeight());
          g.fillOval(i, yy, 2, 2);
        }
      }
    };
    frame.setPreferredSize(new Dimension(500, 500));
    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  public static float plot(float x) {
//    return (float) ISO.getValue(new Point3d(x,0,0));
    return .1f*(2*(((int) (x*5)) % 2)-1);
  }
}
