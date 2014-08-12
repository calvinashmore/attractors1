/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.parameters;

import attractors1.math.ArrayParams;
import attractors1.math.AttractorFunction;
import attractors1.math.AttractorResult;
import attractors1.math.Generator;
import attractors1.math.Point3d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Logic class that calculates how points should be displayed based on an AttractorResult.
 */
public class ParameterSpaceRenderer {

  // maxima/minima for ArrayParams.
  private final ParameterViewParameters viewParams;

  private final Generator<Point3d, ArrayParams> generator;
  private final ArrayParams baseParams;

  private static final int ITERATIONS = 1000;
  private static final int FLUSH = 100;

  private static final int RENDER_THREADS = 10;

  private final ExecutorService executor = Executors.newFixedThreadPool(RENDER_THREADS);

  public static interface Listener {
    void displayUpdated(Quadtree qt);
  }

  public ParameterSpaceRenderer(Generator<Point3d, ArrayParams> generator, ArrayParams basePrams, ParameterViewParameters viewParams) {
    this.generator = generator;
    this.baseParams = basePrams;
    this.viewParams = viewParams;
  }

  /**
   * Shutdown calculation after calculate() has been called.
   */
  public void shutdown() {
    executor.shutdownNow();
  }

  /**
   * x and y in 0-1 space from min to max.
   */
  public ArrayParams getParams(double x, double y) {
    double xParam = x * (viewParams.maxXParam - viewParams.minXParam) + viewParams.minXParam;
    double yParam = y * (viewParams.maxYParam - viewParams.minYParam) + viewParams.minYParam;

    return new ArrayParams(baseParams.getData())
            .withValue(viewParams.indexXParam, xParam)
            .withValue(viewParams.indexYParam, yParam);
  }

  /**
   * calculates the parameter space for the given values.
   */
  public Quadtree calculate(int size, Listener listener) {
    // size is the power of 2
    int resolution = (int) Math.pow(2, size);

    Quadtree quadtree = new Quadtree(size);

    // do we have a way of randomizing/shuffling???
    List<Pixel> pixels = new ArrayList<>();

    for(int x=0;x<resolution;x++) {
      for (int y = 0; y < resolution; y++) {
        Pixel pixel = new Pixel();
        pixel.params = getParams((double) x / resolution, (double) y / resolution);
        pixel.x = x;
        pixel.y = y;
        pixels.add(pixel);
      }
    }

    // shuffle
    Collections.shuffle(pixels);

    for(Pixel pixel : pixels) {
      executor.submit(new GenerateResultCallable(pixel.params, pixel.x, pixel.y, quadtree, listener));
    }

    return quadtree;
  }

  private class Pixel {
    ArrayParams params;
    int x,y;
  }

  private class GenerateResultCallable implements Runnable {
    private final ArrayParams newParams;
    private final Quadtree quadtree;
    private final int x,y;
    private final Listener listener;

    public GenerateResultCallable(ArrayParams newParams, int x, int y, Quadtree quadtree, Listener listener) {
      this.newParams = newParams;
      this.quadtree = quadtree;
      this.x = x;
      this.y = y;
      this.listener = listener;
    }

    @Override
    public void run() {
      AttractorResult<Point3d, ArrayParams> result = generateResult(newParams);
      quadtree.setResult(x, y, result);
      listener.displayUpdated(quadtree);
    }
  }

  int generated = 0;

  private AttractorResult<Point3d, ArrayParams> generateResult(ArrayParams newParams) {
    AttractorFunction<Point3d, ArrayParams> function = generator.newFunction(newParams);
    List<Point3d> points = function.iterate(Point3d.ZERO, ITERATIONS, FLUSH);
    return AttractorResult.calculate(function, points, AttractorResult.OCTREE_DIMENSION_CALCULATOR);
  }
}
