/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ashmore
 */
abstract public class AttractorFunction<T extends Linear<T>, P extends Linear<P>> {

  private static final double BOUND_THRESHOLD = 20;
  private static final double CONVERGENCE_THRESHOLD = .001;

  private static final int LYAPUNOV_ITERATIONS = 1000;
  private static final int LYAPUNOV_FLUSH_ITERATIONS = 100;

  private final P parameters;

  public AttractorFunction(P parameters) {
    this.parameters = parameters;
  }

  public P getParameters() {
    return parameters;
  }

  public boolean isBounded(T point) {
    return point.norm() < BOUND_THRESHOLD;
  }

  /** Applies the function at input. */
  abstract protected T apply(T input);

  /** The magnitude of the derivative of this function at input. */
  abstract protected double derivativeMagnitude(T input);

  /** Returns #iterations points of iterations */
  public List<T> iterate(T start, int iterations, int flush) {
    List<T> result = new ArrayList<>();

    T x = start;
    for(int i=0;i<iterations+flush;i++) {
      x = apply(x);
      if(i >= flush) {
        result.add(x);
      }
    }
    return result;
  }

  /**
   * Calculates lyapunov exponent for this function. Returns Double.NEGATIVE_INFINITY
   * if unbounded or convergent.
   */
  public double calculateLyapunov(T start) {
    double sum = 0;
    T x = start;
    for(int i=0;i<LYAPUNOV_ITERATIONS + LYAPUNOV_FLUSH_ITERATIONS;i++) {
      if(i >= LYAPUNOV_FLUSH_ITERATIONS) {
        sum += Math.log(derivativeMagnitude(x));
      }

      T next = apply(x);

      if(!isBounded(next) || x.subtract(next).norm() < CONVERGENCE_THRESHOLD) {
        return Double.NEGATIVE_INFINITY;
      }

      x = next;
    }
    return sum / LYAPUNOV_ITERATIONS;
  }
}
