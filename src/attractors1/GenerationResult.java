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
  private final ArrayParams params;
  private final List<Point3d> points;
  private final AttractorResult<Point3d, ArrayParams> result;

  GenerationResult(ArrayParams params, List<Point3d> points, AttractorResult<Point3d, ArrayParams> result) {
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

  public AttractorResult<Point3d, ArrayParams> getResult() {
    return result;
  }
}
