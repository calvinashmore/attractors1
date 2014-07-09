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
public class Silly12 extends AbstractFn {

  public Silly12(ArrayParams parameters) {
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

    // .5 1 step wave
    double minSquare = .5;
    double maxSquare = 1;
    int steps = 100;
    double xf = (maxSquare-minSquare)*(1.0/(steps-1))*((int)(v.getX()*10*p[0])%steps) + minSquare;
    double yf = (maxSquare-minSquare)*(1.0/(steps-1))*((int)(v.getY()*10*p[1])%steps) + minSquare;
    double zf = (maxSquare-minSquare)*(1.0/(steps-1))*((int)(v.getZ()*10*p[2])%steps) + minSquare;

    double x = v.getX()*(xf*v.getX()*p[3] + v.getY()*p[6]) + zf*p[9] + p[12];
    double y = v.getY()*(yf*v.getY()*p[4] + v.getZ()*p[7]) + yf*p[10] + p[13];
    double z = v.getZ()*(zf*v.getZ()*p[5] + v.getY()*p[8]) + xf*p[11] + p[14];

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
