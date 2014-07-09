/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.fn;

import attractors1.math.ArrayParams;
import attractors1.math.AttractorFunction;
import attractors1.math.Point3d;

/**
 *
 * @author ashmore
 */
public class Quadratic extends AbstractFn {

  public Quadratic(ArrayParams parameters) {
    super(parameters);
  }

  private double component(Point3d v, int index) {
    double[] p = getParameters().getData();
    int o = index * 10;
    return v.getX()*p[0+o] + v.getY()*p[1+o] + v.getZ()*p[2+o]
         + v.getX()*v.getX()*p[3+o] + v.getY()*v.getY()*p[4+o] + v.getZ()*v.getZ()*p[5+o]
         + v.getX()*v.getY()*p[6+o] + v.getY()*v.getZ()*p[7+o] + v.getZ()*v.getX()*p[8+o]
         + p[9+o];
  }

  @Override
  public Point3d apply(Point3d v) {
    return new Point3d(component(v, 0), component(v, 1), component(v, 2));
  }

  @Override
  public int paramSize() {
    return 30;
  }

  @Override
  public double paramScale() {
    return 1.5;
  }
}
