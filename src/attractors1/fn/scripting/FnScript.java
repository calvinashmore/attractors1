/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.fn.scripting;

import attractors1.math.Point3d;

/**
 * Interface for creating scripted functions.
 */
public interface FnScript {

  public int paramSize();

  public double paramScale();

  public Point3d apply(Point3d input, double[] data);

}
