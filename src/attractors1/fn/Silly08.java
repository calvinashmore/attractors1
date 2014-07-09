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
public class Silly08 extends AbstractFn {

  public Silly08(ArrayParams parameters) {
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

    double xf = Math.floor(5*v.getX()*p[0]) / (5*p[0]);
    double yf = Math.floor(5*v.getY()*p[1]) / (5*p[1]);
    double zf = Math.floor(5*v.getZ()*p[2]) / (5*p[2]);

    double x = v.getX()*v.getX()*p[3] + v.getY()*p[6] + zf*p[9] + p[12];
    double y = v.getY()*v.getY()*p[4] + v.getZ()*p[7] + yf*p[10] + p[13];
    double z = v.getZ()*v.getZ()*p[5] + v.getY()*p[8] + xf*p[11] + p[14];

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
