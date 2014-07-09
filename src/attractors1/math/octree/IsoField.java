/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math.octree;

import attractors1.math.Point3d;

/**
 * A scalar field of point3d
 */
public interface IsoField {

  double getValue(Point3d point);
}
