/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math;

/**
 *
 * @author ashmore
 */
public class SplineEval {
  
  /**
   * Does a cubic spline interpolation between the input points.
   * https://en.wikipedia.org/wiki/Cubic_Hermite_spline
   *
   * t=0 will be p2 and t=1 will be p3.
   */
  public static ArrayParams eval(ArrayParams p1, ArrayParams p2, ArrayParams p3, ArrayParams p4,
      double t) {
    // NOTE: this will be generating a new array for each multiply and add. May not be the best way to do this.
    return p1.multiply(splineEval(t, -1)).add(
        p2.multiply(splineEval(t, 0))).add(
        p3.multiply(splineEval(t, 1))).add(
        p4.multiply(splineEval(t, 2)));
  }
  
  private static double splineEval(double t, int lev) {
    switch(lev) {
      case -1: return (1/6.0)*(1 - 3*t + 3*t*t - t*t*t);
      case  0: return (1/6.0)*(4 - 6*t*t + 3*t*t*t);
      case  1: return (1/6.0)*(1 + 3*t + 3*t*t - 3*t*t*t);
      case  2: return (1/6.0)*(t*t*t);
    }
    return 0;
  }
}
