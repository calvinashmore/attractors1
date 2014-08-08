/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1;

import attractors1.fn.scripting.ScriptedFn;
import attractors1.fn.scripting.ScriptedGenerator;
import attractors1.math.ArrayParams;
import attractors1.math.AttractorFunction;
import attractors1.math.Point3d;
import attractors1.math.octree.Octree;
import java.util.List;

/**
 *
 * @author ashmore
 */
class GenerationResult {
  private static final int ATTEMPTS = 10;
  private static final int ITERATIONS = 50000;
  private static final int FLUSH = 10000;
  private final ArrayParams params;
  private final List<Point3d> points;

  public GenerationResult(ArrayParams params, List<Point3d> points) {
    this.params = params;
    this.points = points;
  }

  public ArrayParams getParams() {
    return params;
  }

  public List<Point3d> getPoints() {
    return points;
  }

  static GenerationResult generatePoints(ScriptedGenerator generator, ArrayParams params) {
    AttractorFunction<Point3d, ArrayParams> fn = generator.newFunction(params);
    List<Point3d> points = fn.iterate(Point3d.ZERO, ITERATIONS, FLUSH);
    points = Point3d.normalize(points);
    try {
      System.out.println("lyapunov:  " + fn.calculateLyapunov(Point3d.ZERO));
      System.out.println("params: " + fn.getParameters());
      Octree octree = new Octree(points, 10);
      System.out.println("dimension: " + octree.fractalDimension());
      System.out.println("partitions: " + octree.countPartitions(5));
    } catch (IllegalArgumentException ex) {
      // sometimes the quadtree can fail to calculate.
      // Don't explode.
    }
    return new GenerationResult(fn.getParameters(), points);
  }

  static GenerationResult generatePoints(ScriptedGenerator generator) {
    for (int i = 0; i < ATTEMPTS; i++) {
      ScriptedFn fn = generator.generate();
      if (fn == null) {
        continue;
      }
      List<Point3d> points = fn.iterate(Point3d.ZERO, ITERATIONS, FLUSH);
      if (!fn.isBounded(points.get(points.size() - 1))) {
        continue;
      }
      points = Point3d.normalize(points);
      Octree octree = new Octree(points, 10);
      double dimension = octree.fractalDimension();
      if (dimension < .8) {
        continue;
      }
      System.out.println("lyapunov:  " + fn.calculateLyapunov(Point3d.ZERO));
      System.out.println("params: " + fn.getParameters());
      System.out.println("dimension: " + dimension);
      System.out.println("partitions: " + octree.countPartitions(5));
      return new GenerationResult(fn.getParameters(), points);
    }
    System.out.println("failed to find function!");
    return null;
  }

}
