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
import attractors1.math.AttractorResult;
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
  private final AttractorResult<Point3d, ArrayParams> result;

  private GenerationResult(ArrayParams params, List<Point3d> points, AttractorResult<Point3d, ArrayParams> result) {
    this.params = params;
    this.points = points;
    this.result = result;
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
    AttractorResult<Point3d, ArrayParams> result = AttractorResult.calculate(fn, points, AttractorResult.OCTREE_DIMENSION_CALCULATOR);
    System.out.println(result.getStats());
    return new GenerationResult(fn.getParameters(), points, result);
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

    AttractorResult<Point3d, ArrayParams> result = AttractorResult.calculate(fn, points, AttractorResult.OCTREE_DIMENSION_CALCULATOR);
      System.out.println(result.getStats());
      return new GenerationResult(fn.getParameters(), points, result);
    }
    System.out.println("failed to find function!");
    return null;
  }
}
