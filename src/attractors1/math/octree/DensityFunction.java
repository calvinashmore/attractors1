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
public interface DensityFunction {
  /**
   * Returns the density due to neighbors at the given point.
   */
  public double getValue(List<Point3d> neighbors, Point3d point);
}
