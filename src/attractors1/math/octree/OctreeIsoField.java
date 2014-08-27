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

  private final Octree octree;
  private final DensityFunction densityFunction;

  public OctreeIsoField(Octree octree, double radius, double ballSize, DensityFunction densityFunction) {
    this.octree = octree;
    this.radius = radius;

    this.densityFunction = densityFunction;
//    densityFunction = DensityFunctions.maximum(radius);
//    densityFunction = DensityFunctions.sumPow(radius, 1, 2);
  }

  @Override
  public double getValue(Point3d point) {
    List<Point3d> nearbyPoints = octree.getNearbyPoints(point, radius);
    return densityFunction.getValue(nearbyPoints, point);
  }
}
