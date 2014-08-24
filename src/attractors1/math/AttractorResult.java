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
public class AttractorResult<T extends Linear<T>, P extends Linear<P>> {

  // the maximum size of a cycle we're willing to detect
  public static final int MAX_CYCLE_SIZE = 100;
  // how close points need to get before we think there's a cycle going on.
  private static final double CYCLE_CLOSENESS_THRESHOLD = .000001;

  private final int pointLength;

  private final AttractorFunction<T,P> function;
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

  public String getStats() {
    return "type:       " + type + "\n"
         + "dimension:  " + dimension + "\n"
         + "lyapunov:   " + lyapunov + "\n"
         + "partitions: " + partitions + "\n"
         + "divergence: " + divergenceIteration + "\n"
         + "cycleSize:  " + cycleSize + "\n";
//         + "params:     " + function.getParameters() + "\n";
  }


  @Override
  public String toString() {
    return getStats();
  }

  public float getCycleRatio() {
    return (float)(cycleSize-1) / MAX_CYCLE_SIZE;
  }

  public float getDivergenceRatio() {
    return (float)divergenceIteration / pointLength;
  }

  public enum Type {
    CHAOTIC, DIVERGENT, CYCLIC,
  }

  private AttractorResult(int pointLength, AttractorFunction<T, P> function, double lyapunov, Type type, double dimension, int partitions, int divergenceIteration, int cycleSize) {
    this.pointLength = pointLength;
    this.function = function;
    this.lyapunov = lyapunov;
    this.type = type;
    this.dimension = dimension;
    this.partitions = partitions;
    this.divergenceIteration = divergenceIteration;
    this.cycleSize = cycleSize;
  }



  public static <T extends Linear<T>, P extends Linear<P>> AttractorResult<T,P>
        calculate(AttractorFunction<T,P> function, List<T> points, DimensionCalculator<T> dimensionCalculator) {
    int pointLength = points.size();
    Type type;
    double dimension;
    int cycleSize;
    int divergenceIteration = 0;
    int partitions = 0;

    double lyapunov = function.calculateLyapunov(points.get(0));
    if(!function.isBounded(points.get(points.size()-1))) {
      type = Type.DIVERGENT;
      dimension = 0;
      cycleSize = 0;

      for(int i=0;i<points.size();i++) {
        if(!function.isBounded(points.get(i))) {
          divergenceIteration = i;
          break;
        }
      }

    } else {

      cycleSize = calculateCycleSize(points);
      if(cycleSize == 0) {
        type = Type.CHAOTIC;
      } else {
        type = Type.CYCLIC;
      }

      dimension = dimensionCalculator.calculateDimension(points);
      partitions = dimensionCalculator.calculatePartitions(points);
    }

    return new AttractorResult<>(pointLength, function, lyapunov, type, dimension, partitions, divergenceIteration, cycleSize);
  }

  private static <T extends Linear<T>> int calculateCycleSize(List<T> points) {
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

  public interface DimensionCalculator<T extends Linear<T>> {
    double calculateDimension(List<T> points);
    int calculatePartitions(List<T> points);
  }

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

  public Type getType() {
    return type;
  }

  public int getPartitions() {
    return partitions;
  }

  public static final DimensionCalculator<Point3d> OCTREE_DIMENSION_CALCULATOR
          = new DimensionCalculator<Point3d>() {
    @Override
    public double calculateDimension(List<Point3d> points) {
      return new Octree(points, 10).fractalDimension();
    }

    @Override
    public int calculatePartitions(List<Point3d> points) {
      return new Octree(points, 5).countPartitions(5);
    }
  };
}
