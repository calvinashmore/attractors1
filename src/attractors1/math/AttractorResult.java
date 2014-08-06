/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math;

import attractors1.math.octree.Octree;
import java.util.List;

/**
 * Data type representing some miscellaneous facts about an attractor's behavior
 * at a point in parameter space.
 */
abstract public class AttractorResult<T extends Linear<T>, P extends Linear<P>> {

  // the maximum size of a cycle we're willing to detect
  public static final int MAX_CYCLE_SIZE = 100;
  // how close points need to get before we think there's a cycle going on.
  private static final double CYCLE_CLOSENESS_THRESHOLD = .000001;

  private final int pointLength;

  private final AttractorFunction<T,P> function;
  private final P params;
  private final double lyapunov;
  private final Type type;

  // only for chaotic
  private final double dimension;
  private final int partitions;

  // only for divergent
  private final int divergenceIteration;

  // only for cyclic
  // value of zero implies chaotic
  private final int cycleSize;

  public float getCycleRatio() {
    return (float)(cycleSize-1) / MAX_CYCLE_SIZE;
  }

  public float getDivergenceRatio() {
    return (float)divergenceIteration / pointLength;
  }

  public enum Type {
    CHAOTIC, DIVERGENT, CYCLIC,
  }

  public AttractorResult(P params, AttractorFunction<T,P> function, List<T> points) {
    this.params = params;
    this.function = function;
    this.pointLength = points.size();

    lyapunov = function.calculateLyapunov(points.get(0));
    if(!function.isBounded(points.get(points.size()-1))) {
      type = Type.DIVERGENT;
      dimension = 0;
      cycleSize = 0;
      int divergenceIteration = 0;
      for(int i=0;i<points.size();i++) {
        if(!function.isBounded(points.get(i))) {
          divergenceIteration = i;
          break;
        }
      }
      this.divergenceIteration = divergenceIteration;
      this.partitions = 0;

    } else {
      divergenceIteration = 0;

      cycleSize = calculateCycleSize(points);
      if(cycleSize == 0) {
        type = Type.CHAOTIC;
      } else {
        type = Type.CYCLIC;
      }

      dimension = calculateDimension(points);
      partitions = calculatePartitions(points);
    }

  }

  private int calculateCycleSize(List<T> points) {
    // we assume that if it's cyclic, it's reached its cycle at the end of the point list.
    int lastIndex = points.size()-1;
    T point = points.get(lastIndex);
    for(int j=1;j<MAX_CYCLE_SIZE;j++) {
      T backtrackPoint = points.get(lastIndex-j);
      if(point.subtract(backtrackPoint).norm() <= CYCLE_CLOSENESS_THRESHOLD)
        return j;
    }

    return 0;
  }

  /**
   * Note: this is called from constructor.
   */
  abstract protected double calculateDimension(List<T> points);

  /**
   * Note: this is called from constructor.
   */
  abstract protected int calculatePartitions(List<T> points);

  public int getCycleSize() {
    return cycleSize;
  }

  public double getDimension() {
    return dimension;
  }

  public int getDivergenceIteration() {
    return divergenceIteration;
  }

  public AttractorFunction<T, P> getFunction() {
    return function;
  }

  public double getLyapunov() {
    return lyapunov;
  }

  public P getParams() {
    return params;
  }

  public Type getType() {
    return type;
  }

  public int getPartitions() {
    return partitions;
  }

  public static class AttractorResult3d extends AttractorResult<Point3d, ArrayParams> {

    public AttractorResult3d(ArrayParams params,
            AttractorFunction<Point3d, ArrayParams> function,List<Point3d> points) {
      super(params, function, points);
    }

    @Override
    protected double calculateDimension(List<Point3d> points) {
      return new Octree(points, 10).fractalDimension();
    }

    @Override
    protected int calculatePartitions(List<Point3d> points) {
      return new Octree(points, 5).countPartitions(5);
    }
  }
}
