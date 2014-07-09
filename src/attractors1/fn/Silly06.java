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
public class Silly06 extends AbstractFn {

  public Silly06(ArrayParams parameters) {
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

    Point3d a = new Point3d(v.getX(), v.getY(), 0);
    double r = a.norm();

    double x = v.getX()*p[0] + v.getY()*p[1] + v.getZ()*p[2] + h(r)*p[9] + p[12];
    double y = v.getX()*p[3] + v.getY()*p[4] + v.getZ()*p[5] + h(r)*p[10] + p[13];
    double z = v.getX()*p[6] + v.getY()*p[7] + v.getZ()*p[8] + h(r)*p[11] + p[14];

    return new Point3d(x, y, z);
  }

  @Override
  public int paramSize() {
    return 15;
  }

  @Override
  public double paramScale() {
    return 1.5;
  }
}
