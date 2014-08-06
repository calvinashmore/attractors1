/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math.octree;

import attractors1.math.Point3d;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author ashmore
 */
public class OctreeTest {
  @Test
  public void testThing() {
    List<Point3d> points = new ArrayList<>();
    points.add(Point3d.ZERO);
    points.add(Point3d.UNIT_X);
    points.add(Point3d.UNIT_Y);
    points.add(Point3d.UNIT_Z);
    points.add(new Point3d(0,0,.98));
    points.add(new Point3d(0,0.5,0));

    Octree octree = new Octree(points);

    Assert.assertEquals(0.0, octree.getMinX());
    Assert.assertEquals(0.0, octree.getMinY());
    Assert.assertEquals(0.0, octree.getMinZ());
    Assert.assertEquals(1.0, octree.getMaxX());
    Assert.assertEquals(1.0, octree.getMaxY());
    Assert.assertEquals(1.0, octree.getMaxZ());

    List<Point3d> nearPoints;

    nearPoints = octree.getNearbyPoints(Point3d.ZERO, .1);
    Assert.assertEquals(1, nearPoints.size());
    Assert.assertEquals(Point3d.ZERO, nearPoints.get(0));

    nearPoints = octree.getNearbyPoints(Point3d.UNIT_X, .1);
    Assert.assertEquals(1, nearPoints.size());
    Assert.assertEquals(Point3d.UNIT_X, nearPoints.get(0));

    nearPoints = octree.getNearbyPoints(new Point3d(1,1,1), .1);
    Assert.assertEquals(0, nearPoints.size());

    nearPoints = octree.getNearbyPoints(Point3d.UNIT_Z, .1);
    Assert.assertEquals(2, nearPoints.size());
  }

  @Test
  public void testPartitions1() {
    List<Point3d> points = new ArrayList<>();
    points.add(Point3d.ZERO);
    points.add(Point3d.UNIT_X);
    points.add(Point3d.UNIT_Y);
    points.add(Point3d.UNIT_Z);

    Octree octree = new Octree(points);

    Assert.assertEquals(1, octree.countPartitions(1));
    Assert.assertEquals(4, octree.countPartitions(2));
    Assert.assertEquals(4, octree.countPartitions(4));
  }

  @Test
  public void testPartitions2() {
    List<Point3d> points = new ArrayList<>();
    Point3d start = new Point3d(1, 0, .0);
    Point3d end = new Point3d(0, 1, .0);
    for(int i=0;i<100;i++) {
      double t = (double)i/100;
      // two parallel segments
      points.add(start.multiply(t).add(end.multiply(1-t)));
      points.add(start.multiply(t).add(end.multiply(1-t)).add(new Point3d(1,0,0)));
    }

    Octree octree = new Octree(points);

    Assert.assertEquals(2, octree.countPartitions(4));
  }
}
