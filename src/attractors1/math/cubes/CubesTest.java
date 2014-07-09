/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math.cubes;

import attractors1.fn.AbstractFn;
import attractors1.fn.Silly14;
import attractors1.math.ArrayParams;
import attractors1.math.Point3d;
import attractors1.math.octree.IsoField;
import attractors1.math.octree.Octree;
import attractors1.math.octree.OctreeIsoField;
import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ashmore
 */
public class CubesTest {
  private static final double[] PARAMS = new double[] {-0.15787566387202112, 0.895177557569111, 0.16625235513190317, 1.454949356353741, 0.8140974227422564, -1.2838850699523217, 1.3562231622992713, -0.8983063842643229, -1.0504824423398598, 0.6835245545239804, 0.025091990272138598, -0.0969394718095592, -1.3730454913732344, -1.3621655300069895, 0.250821740405735};

  private static final int ITERATIONS = 500000;
  private static final int FLUSH = 1000;

  private static final int SLICES = 500;
  private static final double BALL_SIZE = .005;
  private static final double RADIUS = BALL_SIZE * 5;

  public static void main(String args[]) throws Exception {

//    IsoField iso = new IsoField() {
//      @Override
//      public double getValue(Point3d point) {
//        return 1.0 / point.norm();
//      }
//    };
    AbstractFn fn = new Silly14(new ArrayParams(PARAMS));
    List<Point3d> pp = fn.iterate(Point3d.ZERO, ITERATIONS, FLUSH);
    IsoField iso = new OctreeIsoField(new Octree(pp), RADIUS, BALL_SIZE);

    MarchingCubes m = new MarchingCubes();

    Point3d unitCube = new Point3d(1,1,1);
    List<Triangle> tris = m.tesselate(iso, SLICES,
            unitCube.multiply(-1-2*RADIUS), unitCube.multiply(1+2*RADIUS));
    System.out.println("Created "+tris.size()+" triangles");
    Map<Point3d, Integer> points = new HashMap<>();
    int pointCount = 1;

    PrintStream outStream = new PrintStream(new File("out.obj"));

    for(Triangle t : tris) {
      for(Point3d p : t.getPoints()) {
        if(!points.containsKey(p)) {
          points.put(p, pointCount++);
          outStream.printf("v %f %f %f\n", p.getX(), p.getY(), p.getZ());
        }
      }

      outStream.printf("f %d %d %d\n",
              points.get(t.getPoints()[0]),
              points.get(t.getPoints()[1]),
              points.get(t.getPoints()[2]));
    }
  }
}
