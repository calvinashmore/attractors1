/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.fn;

import attractors1.math.ArrayParams;
import attractors1.math.AttractorFunction;
import attractors1.math.Point3d;
import java.util.List;

/**
 *
 * @author ashmore
 */
abstract public class AbstractFn extends AttractorFunction<Point3d, ArrayParams>{

  public AbstractFn(ArrayParams params) {
    super(params);
  }

  /** size of the array in ArrayParams. */
  abstract public int paramSize();

  /** multiply randomly generated parameters by this scale. */
  abstract public double paramScale();

  private static final double EPSILON = .01;

  /** numeric approximation of derivative */
  @Override
  public double derivativeMagnitude(Point3d input) {
    Point3d dx = measureDelta(input, Point3d.UNIT_X);
    Point3d dy = measureDelta(input, Point3d.UNIT_Y);
    Point3d dz = measureDelta(input, Point3d.UNIT_Z);

    return Math.max(dx.norm(), Math.max(dy.norm(), dz.norm()));
  }

  private Point3d measureDelta(Point3d base, Point3d axis) {
    Point3d v1 = apply(base.add(axis.multiply(EPSILON)));
    Point3d v2 = apply(base.add(axis.multiply(-EPSILON)));

    return v2.subtract(v1).multiply(2.0 / EPSILON);
  }

  private static final int DEFAULT_ITERATIONS = 20000;
  private static final int DEFAULT_FLUSH = 1000;

  public List<Point3d> iterate() {
    return iterate(Point3d.ZERO, DEFAULT_ITERATIONS, DEFAULT_FLUSH);
  }
}
