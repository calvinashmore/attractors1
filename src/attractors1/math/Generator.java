/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math;

import java.util.Random;

/**
 *
 * @author ashmore
 */
abstract public class Generator<T extends Linear<T>, P extends Linear<P>> {
  private static final int ATTEMPTS = 10000;
  private final Random random;

  public Generator() {
    this(new Random().nextInt());
  }

  public Generator(int randomSeed) {
    System.out.println("*** RandomSeed: "+randomSeed);
    this.random = new Random(randomSeed);
  }

  /** Generate a new and random set of parameters. */
  abstract protected AttractorFunction<T, P> newFunction(Random random);
  abstract public AttractorFunction<T, P> newFunction(P params);

  /** The initial T to use, usually zero. */
  abstract protected T initialValue();

  /** Returns an acceptable lyapunov value. */
  abstract protected double goodLyapunov();

  /** return a good function if we can find one, null otherwise. */
  public AttractorFunction<T, P> generate() {
    for(int i=0;i<ATTEMPTS;i++) {
      AttractorFunction<T, P> f = newFunction(random);
      double lyapunov = f.calculateLyapunov(initialValue());
      if(lyapunov > goodLyapunov()) {
        return f;
      }
    }
    return null;
  }
}
