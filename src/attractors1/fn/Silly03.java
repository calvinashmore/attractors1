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
public class Silly03 extends AbstractFn {

  public Silly03(ArrayParams parameters) {
    super(parameters);
  }

  private double component(Point3d v, int index) {
    double[] p = getParameters().getData();
    int o = index * 7;
    return v.getX()*p[0+o] + v.getY()*p[1+o] + v.getZ()*p[2+o]
         + v.getX()*Math.abs(v.getY())*p[3+o]
         + v.getY()*Math.abs(v.getZ())*p[4+o]
         + v.getZ()*Math.abs(v.getX())*p[5+o]
         + p[6+o];
  }

  @Override
  public Point3d apply(Point3d v) {
    return new Point3d(component(v, 0), component(v, 1), component(v, 2));
  }

  @Override
  public int paramSize() {
    return 7*3;
  }

  @Override
  public double paramScale() {
    return 1.5;
  }
}
