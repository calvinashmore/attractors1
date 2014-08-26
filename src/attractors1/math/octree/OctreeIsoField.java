/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math.octree;

import attractors1.math.Point3d;
import java.util.List;

/**
 *
 * @author ashmore
 */
public class OctreeIsoField implements IsoField {
  private final double radius;
  private final double inverseBallSize;

  private final Octree octree;

  public OctreeIsoField(Octree octree, double radius, double ballSize) {
    this.octree = octree;
    this.radius = radius;
    this.inverseBallSize = 1.0 / ballSize;
  }


  @Override
  public double getValue(Point3d point) {
    List<Point3d> nearbyPoints = octree.getNearbyPoints(point, radius);

    double total = 0;
    double max = 0;
    for(Point3d p : nearbyPoints) {
      double x = getContribution(point, p);
      if(x < 1)
        x = x*x;

      max = Math.max(max, x);
      total += x;
    }

    if(max == 0)
      return 0;

    int n = nearbyPoints.size();

    // shows how dense the points are
    double scale = total/max - 1;
    return (max + scale/n)/2;
  }

  private double getContribution(Point3d point, Point3d other) {
    double dx = (point.getX() - other.getX()) * inverseBallSize;
    double dy = (point.getY() - other.getY()) * inverseBallSize;
    double dz = (point.getZ() - other.getZ()) * inverseBallSize;
    double a = (dx*dx + dy*dy + dz*dz);

    // avoid NaN
    if (a==0) return Double.POSITIVE_INFINITY;
    return 1.0 / a;
  }
}
