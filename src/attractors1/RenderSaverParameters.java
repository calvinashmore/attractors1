/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1;

import attractors1.math.octree.DensityFunction;
import attractors1.math.octree.DensityFunctions;
import com.beust.jcommander.Parameter;

/**
 *
 * @author ashmore
 */
public class RenderSaverParameters {

  @Parameter(names="--density")
  public int density = 10;

  @Parameter(names="--iteration_multiplier")
  public int iterationMultiplier = 100000;

  @Parameter(names="--flush")
  public int flush = iterationMultiplier / 10;

  @Parameter(names="--size_mm")
  public double sizeMillimeters = 180;

  @Parameter(names="--slices")
  public int slices = 500;

  @Parameter(names="--metaball_size")
  public double metaballSize = 0.01;

  @Parameter(names="--density_function_exponent")
  public double densityFunctionExponent = 5.0;

  @Parameter(names="--iso_multiplier")
  public double isoMultiplier = 5;

  @Parameter(names="--chunks")
  public int chunks = 10;

  @Parameter(names="--threads")
  public int threads = 10;

  public DensityFunction getDensityFunction() {
    return DensityFunctions.sumPow(metaballSize, 1.0 / density, densityFunctionExponent);
  }
}
