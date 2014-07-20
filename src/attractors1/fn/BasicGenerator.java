/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.fn;

import attractors1.math.ArrayParams;
import attractors1.math.AttractorFunction;
import attractors1.math.Generator;
import attractors1.math.Point3d;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ashmore
 */
public class BasicGenerator<F extends AbstractFn> extends Generator<Point3d, ArrayParams> {

  private final Constructor<F> constructor;

  public BasicGenerator(Class<F> functionClass) {
    try {
      constructor = functionClass.getConstructor(ArrayParams.class);
    } catch(Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public BasicGenerator(Class<F> functionClass, int seed) {
    super(seed);
    try {
      constructor = functionClass.getConstructor(ArrayParams.class);
    } catch(Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public AbstractFn generate() {
    return (AbstractFn) super.generate();
  }

  @Override
  public AttractorFunction<Point3d, ArrayParams> newFunction(ArrayParams params) {
    return build(params);
  }


  private F build(ArrayParams params) {
    try {
      return constructor.newInstance(params);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public F newFunction(Random random) {
    F temp = build(new ArrayParams(new double[] {}));
    return build(ArrayParams.newParams(temp.paramSize(), random).multiply(temp.paramScale()));
  }

  @Override
  public Point3d initialValue() {
    return Point3d.ZERO;
  }

  @Override
  public double goodLyapunov() {
    return 1;
  }
}
