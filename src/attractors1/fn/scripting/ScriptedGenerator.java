/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.fn.scripting;

import attractors1.math.ArrayParams;
import attractors1.math.AttractorFunction;
import attractors1.math.Generator;
import attractors1.math.Linear;
import attractors1.math.Point3d;
import java.util.Random;

/**
 *
 * @author ashmore
 */
public class ScriptedGenerator extends Generator<Point3d, ArrayParams>{

  private final FnScript script;

  public ScriptedGenerator(FnScript script) {
    super();
    this.script = script;
  }

  public ScriptedGenerator(FnScript script, int randomSeed) {
    super(randomSeed);
    this.script = script;
  }

  @Override
  public ScriptedFn generate() {
    return (ScriptedFn) super.generate();
  }

  @Override
  protected ScriptedFn newFunction(Random random) {
    double paramScale = script.paramScale();
    int paramSize = script.paramSize();
    return new ScriptedFn(ArrayParams.newParams(paramSize, random).multiply(paramScale), script);
  }

  @Override
  protected Point3d initialValue() {
    return Point3d.ZERO;
  }

  @Override
  protected double goodLyapunov() {
    return 1;
  }
}
