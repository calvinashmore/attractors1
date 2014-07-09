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
public class Silly13 extends AbstractFn {

  public Silly13(ArrayParams parameters) {
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

    double xf = Math.cos(v.getX() * 5*p[0]);
    double yf = Math.sin(v.getY() * 5*p[1]);
    double zf = Math.cosh(v.getZ() * 5*p[2]);

    double x = v.getX()*xf*p[3] + v.getY()*p[6] + zf*p[9] + p[12];
    double y = v.getY()*yf*p[4] + v.getZ()*p[7] + yf*p[10] + p[13];
    double z = v.getZ()*zf*p[5] + v.getY()*p[8] + xf*p[11] + p[14];

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
