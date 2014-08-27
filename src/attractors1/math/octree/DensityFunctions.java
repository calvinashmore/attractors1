/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math.octree;

import attractors1.math.Point3d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ashmore
 */
public class DensityFunctions {

  private DensityFunctions() {}

  public static final DensityFunction add(final DensityFunction df1, final DensityFunction df2) {
    return new DensityFunction() {

      @Override
      public double getValue(List<Point3d> neighbors, Point3d point) {
        return df1.getValue(neighbors, point) + df2.getValue(neighbors, point);
      }
    };
  }

  public static final DensityFunction multiply(final DensityFunction df1, final double c) {
    return new DensityFunction() {

      @Override
      public double getValue(List<Point3d> neighbors, Point3d point) {
        return df1.getValue(neighbors, point)*c;
      }
    };
  }

  public static final DensityFunction maximum(double ballSize) {
    final double inverseBallSize = 1.0 / ballSize;
    return new DensityFunction() {
      @Override
      public double getValue(List<Point3d> neighbors, Point3d point) {
        double max = 0;
        for(Point3d p : neighbors) {
          double c = getContribution(point, p, inverseBallSize);
          max = Math.max(max, c);
        }
        return max;
      }
    };
  }

  public static final DensityFunction nThMax(double ballSize, final int n) {
    final double inverseBallSize = 1.0 / ballSize;
    return new DensityFunction() {
      @Override
      public double getValue(List<Point3d> neighbors, Point3d point) {
        if(neighbors.size() <= n)
          return 0;

        List<Double> contributions = new ArrayList<>();
        for(Point3d p : neighbors) {
          double c = getContribution(point, p, inverseBallSize);
          contributions.add(c);
        }
        Collections.sort(contributions);
        // return the nth maximum
        return contributions.get(contributions.size() - n - 1);
      }
    };
  }
  public static final DensityFunction nThMaxPlusMax(double ballSize, final int n) {
    final double inverseBallSize = 1.0 / ballSize;
    return new DensityFunction() {
      @Override
      public double getValue(List<Point3d> neighbors, Point3d point) {
        if(neighbors.size() <= n)
          return 0;

        List<Double> contributions = new ArrayList<>();
        for(Point3d p : neighbors) {
          double c = getContribution(point, p, inverseBallSize);
          contributions.add(c);
        }
        Collections.sort(contributions);
        // return the nth maximum
        return contributions.get(contributions.size() - n - 1)
                + contributions.get(contributions.size() - 1);
      }
    };
  }

  public static final DensityFunction sum(double ballSize, final double expectedDensity) {
    final double inverseBallSize = 1.0 / ballSize;
    return new DensityFunction() {
      @Override
      public double getValue(List<Point3d> neighbors, Point3d point) {
        double sum = 0;
        for(Point3d p : neighbors) {
          double c = getContribution(point, p, inverseBallSize);
          sum += c;
        }
        return sum * expectedDensity;
      }
    };
  }

  public static final DensityFunction sumPow(double ballSize, final double expectedDensity, final double pow) {
    final double inverseBallSize = 1.0 / ballSize;
    return new DensityFunction() {
      @Override
      public double getValue(List<Point3d> neighbors, Point3d point) {
        double sum = 0;
        for(Point3d p : neighbors) {
          double c = getContribution(point, p, inverseBallSize);
          sum += Math.pow(c, pow);
        }
        return sum * expectedDensity;
      }
    };
  }

  public static final DensityFunction maxDensity(double ballSize) {
    final double inverseBallSize = 1.0 / ballSize;
    return new DensityFunction() {
      @Override public double getValue(List<Point3d> neighbors, Point3d point) {
        double sum = 0;
        double max = 0;
        for (Point3d p : neighbors) {
          double c = getContribution(point, p, inverseBallSize);
          sum += c;
          max = Math.max(max, c);
        }

        if (max == 0) {
          return 0;
        }

        int n = neighbors.size();

        // shows how dense the points are
        double scale = sum / max - 1;

        return (max + scale / n) / 2;
      }
    };
  }

  private static double getContribution(Point3d point, Point3d other, double inverseBallSize) {
    double dx = (point.getX() - other.getX()) * inverseBallSize;
    double dy = (point.getY() - other.getY()) * inverseBallSize;
    double dz = (point.getZ() - other.getZ()) * inverseBallSize;
    double a = (dx*dx + dy*dy + dz*dz);

    // avoid NaN
    if (a==0) return Double.POSITIVE_INFINITY;
    return 1.0 / a;
  }
}
