/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1;

import attractors1.fn.AbstractFn;
import attractors1.fn.BasicGenerator;
import attractors1.fn.Quadratic;
import attractors1.fn.Silly01;
import attractors1.fn.Silly02;
import attractors1.fn.Silly03;
import attractors1.fn.Silly04;
import attractors1.fn.Silly05;
import attractors1.fn.Silly06;
import attractors1.fn.Silly07;
import attractors1.fn.Silly08;
import attractors1.fn.Silly09;
import attractors1.fn.Silly10;
import attractors1.fn.Silly11;
import attractors1.fn.Silly12;
import attractors1.fn.Silly13;
import attractors1.fn.Silly14;
import attractors1.math.AttractorFunction;
import attractors1.math.Point3d;
import attractors1.math.octree.Octree;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author ashmore
 */
public class Attractors1 {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
//    final BasicGenerator generator = new BasicGenerator(Silly04.class, 665275420);
    final BasicGenerator generator = new BasicGenerator(Silly11.class);


    JFrame frame = new JFrame("meef");
    final RendererPanel renderer = new Renderer3d();
    renderer.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        renderer.setPoints(generatePoints(generator));
      }
    });

    renderer.setPreferredSize(new Dimension(SIZE, SIZE));
    frame.add(renderer);
    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  private static final int SIZE = 800;
//  public static final int DOT_SIZE = 5;
  private static final int ATTEMPTS = 10;

  private static final int ITERATIONS = 50000;
  private static final int FLUSH = 10000;

  private static List<Point3d> generatePoints(BasicGenerator generator) {
    for (int i = 0; i < ATTEMPTS; i++) {
      AbstractFn fn = generator.generate();
      if (fn == null) {
        continue;
      }
      List<Point3d> points = Point3d.normalize(fn.iterate(Point3d.ZERO, ITERATIONS, FLUSH));
      if(!fn.isBounded(points.get(points.size()-1))) {
        continue;
      }

      double dimension = new Octree(points,10).fractalDimension();
      if (dimension < 1.0) {
        continue;
      }

      System.out.println("lyapunov:  " + fn.calculateLyapunov(Point3d.ZERO));
      System.out.println("params: " + fn.getParameters());
      System.out.println("dimension: " + dimension);
      return points;
    }

    System.out.println("failed to find function!");
    return null;
  }

//  static BufferedImage buildImage(List<Point3d> points, int size, int dotSize) {
//    BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
//    Graphics2D g = img.createGraphics();
//    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//    g.setColor(Color.BLACK);
//    g.fillRect(0, 0, size, size);
//    for(int i=0;i<points.size();i++) {
//      Point3d point = points.get(i);
//      float progress = ((float)i) / points.size();
//      float x = (float) (point.getX()+1)/2;
//      float y = (float) (point.getY()+1)/2;
//      float z = (float) (point.getZ()+1)/2;
//
//      //g.setColor(Color.WHITE);
//      g.setColor(new Color(1, progress, z));
//      g.fillOval((int) (size*x - dotSize/2),
//              (int) (size*y - dotSize/2),
//              dotSize, dotSize);
//    }
//    return img;
//  }
}
