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
public class Silly14 extends AbstractFn {

  public Silly14(ArrayParams parameters) {
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

    double x = Math.cosh(v.getX()*xf*p[3] + v.getY()*p[6]) + Math.sinh(xf*v.getZ())*p[9] + p[12];
    double y = Math.cosh(v.getY()*yf*p[4] + v.getZ()*p[7]) + Math.sinh(yf*v.getX())*p[10] + p[13];
    double z = Math.sin(v.getZ()*zf*p[5] + v.getX()*p[8]) + Math.sinh(zf*v.getY())*p[11] + p[14];

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
