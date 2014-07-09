/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1;

import attractors1.fn.AbstractFn;
import attractors1.fn.Quadratic;
import attractors1.fn.Silly05;
import attractors1.fn.Silly06;
import attractors1.fn.Silly08;
import attractors1.fn.Silly09;
import attractors1.fn.Silly14;
import attractors1.math.ArrayParams;
import attractors1.math.Point3d;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintStream;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author ashmore
 */
public class Loader {

  private static final int SIZE = 800;
  private static final int DOT_SIZE = 10;

  private static final int ITERATIONS = 500000;
  private static final int FLUSH = 10000;

  private static double[] PARAMS = new double[] {-0.15787566387202112, 0.895177557569111, 0.16625235513190317, 1.454949356353741, 0.8140974227422564, -1.2838850699523217, 1.3562231622992713, -0.8983063842643229, -1.0504824423398598, 0.6835245545239804, 0.025091990272138598, -0.0969394718095592, -1.3730454913732344, -1.3621655300069895, 0.250821740405735};


  public static void main(String args[]) throws Exception {
    AbstractFn fn = new Silly14(new ArrayParams(PARAMS));

    List<Point3d> points = fn.iterate(Point3d.ZERO, ITERATIONS, FLUSH);
    points = Point3d.normalize(points);
    //saveToObj(points);
    render(points);
  }

  private static void saveToScad(List<Point3d> points) throws Exception {
    try (PrintStream outStream = new PrintStream(new File("out.scad"))) {
      for(Point3d point : points) {
        Point3d scalePoint = point.multiply(10);
        outStream.println(String.format("translate([%f,%f,%f]) sphere(1);",
                scalePoint.getX(), scalePoint.getY(), scalePoint.getZ()));
      }
    }
  }

  private static void saveToObj(List<Point3d> points) throws Exception {

    try (PrintStream outStream = new PrintStream(new File("out.obj"))) {
      int index = 0;
      for(Point3d point : points) {
        Point3d scalePoint = point.multiply(100);
        objDot(scalePoint, outStream, index);
        index++;
      }
    }
  }

  private static void objDot(Point3d dot, PrintStream out, int index) {
    double dotSize = 2;
    double x = dot.getX();
    double y = dot.getY();
    double z = dot.getZ();
    double xx = x + dotSize;
    double yy = y + dotSize;
    double zz = z + dotSize;
    out.printf("v %f %f %f\n", x, y, z);
    out.printf("v %f %f %f\n", x, y, zz);
    out.printf("v %f %f %f\n", x, yy, z);
    out.printf("v %f %f %f\n", x, yy, zz);
    out.printf("v %f %f %f\n", xx, y, z);
    out.printf("v %f %f %f\n", xx, y, zz);
    out.printf("v %f %f %f\n", xx, yy, z);
    out.printf("v %f %f %f\n", xx, yy, zz);
    int f = 8*index;
    out.printf("f %d %d %d\n", f+1, f+7, f+5);
    out.printf("f %d %d %d\n", f+1, f+3, f+7);
    out.printf("f %d %d %d\n", f+1, f+4, f+3);
    out.printf("f %d %d %d\n", f+1, f+2, f+4);
    out.printf("f %d %d %d\n", f+3, f+8, f+7);
    out.printf("f %d %d %d\n", f+3, f+4, f+8);
    out.printf("f %d %d %d\n", f+5, f+7, f+8);
    out.printf("f %d %d %d\n", f+5, f+8, f+6);
    out.printf("f %d %d %d\n", f+1, f+5, f+6);
    out.printf("f %d %d %d\n", f+1, f+6, f+2);
    out.printf("f %d %d %d\n", f+2, f+6, f+8);
    out.printf("f %d %d %d\n", f+2, f+8, f+4);
  }

  private static void render(List<Point3d> points) throws HeadlessException {
    JFrame frame = new JFrame("meef");
    final RendererPanel renderer = new Renderer3d();
    renderer.setPreferredSize(new Dimension(SIZE, SIZE));
    frame.add(renderer);
    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    renderer.setPoints(points);
  }
}
