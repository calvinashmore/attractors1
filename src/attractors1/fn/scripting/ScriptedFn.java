/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.fn.scripting;

import attractors1.fn.AbstractFn;
import attractors1.math.ArrayParams;
import attractors1.math.Point3d;

/**
 * Base scripted function that takes a FnScript.
 */
public class ScriptedFn extends AbstractFn {

  private final FnScript script;

  public ScriptedFn(ArrayParams params, FnScript script) {
    super(params);
    this.script = script;
  }

  @Override
  public int paramSize() {
    return script.paramSize();
  }

  @Override
  public double paramScale() {
    return script.paramScale();
  }

  @Override
  protected Point3d apply(Point3d input) {
    return script.apply(input, getParameters().getData());
  }
}
