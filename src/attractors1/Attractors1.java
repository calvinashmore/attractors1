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
}
