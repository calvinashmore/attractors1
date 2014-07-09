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
import attractors1.math.cubes.MarchingCubes;
import attractors1.math.cubes.Triangle;
import attractors1.math.octree.IsoField;
import attractors1.math.octree.Octree;
import attractors1.math.octree.OctreeIsoField;
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

  private static final int WINDOW_SIZE = 800;

  private static final int ITERATIONS = 500000;
  private static final int FLUSH = 10000;

  // number of slices for the marching cubes
  private static final int SLICES = 500;

  // the size of the metaballs in generating an isosurface
  private static final double METABALL_SIZE = .005;

  // the radius to search for points in building the isofield
  private static final double ISO_RADIUS = METABALL_SIZE * 5;

  private static double[] PARAMS = new double[] {-0.3445906491718246, 0.29179394718363905, -0.0906524271518605, 1.1528487387968882, -0.31102968316133894, -0.716046655973521, 0.23946134437748134, 0.8869913700304676, -1.2408912366042095, -0.9902676363499413, -0.0017429971624999263, 0.1466763031617715, -1.2691647023120216, -0.9649674317465828, -0.7111543334150491};


  public static void main(String args[]) throws Exception {
    AbstractFn fn = new Silly14(new ArrayParams(PARAMS));

    List<Point3d> points = fn.iterate(Point3d.ZERO, ITERATIONS, FLUSH);
    points = Point3d.normalize(points);
    //render(points);
    saveToObj(points);
  }

  private static void saveToObj(List<Point3d> points) {
    IsoField iso = new OctreeIsoField(new Octree(points), ISO_RADIUS, METABALL_SIZE);

    Point3d unitCube = new Point3d(1,1,1);
    List<Triangle> tris = MarchingCubes.tesselate(iso, SLICES,
            unitCube.multiply(-1-2*ISO_RADIUS), unitCube.multiply(1+2*ISO_RADIUS));
    System.out.println("Created "+tris.size()+" triangles");
  }

  private static void render(List<Point3d> points) throws HeadlessException {
    JFrame frame = new JFrame("meef");
    final RendererPanel renderer = new Renderer3d();
    renderer.setPreferredSize(new Dimension(WINDOW_SIZE, WINDOW_SIZE));
    frame.add(renderer);
    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    renderer.setPoints(points);
  }
}
