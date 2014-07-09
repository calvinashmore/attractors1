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
public class Silly05 extends AbstractFn {

  public Silly05(ArrayParams parameters) {
    super(parameters);
  }

  @Override
  public Point3d apply(Point3d v) {
    double[] p = getParameters().getData();

    double a = (p[0]+1)/2;

    double x = v.getX()*(a + p[1]*v.getY()*Math.sin(2*Math.PI*(p[2]*v.getZ()+p[3]))) + p[4];
    double y = v.getY()*(a + p[5]*v.getX()*Math.cos(2*Math.PI*(p[2]*v.getZ()+p[6]))) + p[7];
    double z = v.getX();

    return new Point3d(x,y,z);
  }

  @Override
  public int paramSize() {
    return 8;
  }

  @Override
  public double paramScale() {
    return 1.0;
  }
}
