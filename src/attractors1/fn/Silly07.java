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
public class Silly07 extends AbstractFn {

  public Silly07(ArrayParams parameters) {
    super(parameters);
  }

  private double h(double x) {
    double h = 1.0/x;
    if(Double.isInfinite(h) || Double.isNaN(h))
      return 0;
    return h;
  }

  @Override
  public Point3d apply(Point3d v) {
    double[] p = getParameters().getData();

    double x = v.getX()*Math.max(v.getY(), v.getZ())*p[0] + v.getY()*p[3] + p[6];
    double y = v.getY()*Math.max(v.getX(), v.getZ())*p[1] + v.getZ()*p[4] + p[7];
    double z = v.getZ()*Math.max(v.getX(), v.getY())*p[2] + v.getX()*p[5] + p[8];

    return new Point3d(x, y, z);
  }

  @Override
  public int paramSize() {
    return 9;
  }

  @Override
  public double paramScale() {
    return 1.5;
  }
}
