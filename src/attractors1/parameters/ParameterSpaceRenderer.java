/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.parameters;

import attractors1.math.ArrayParams;
import attractors1.math.AttractorFunction;
import attractors1.math.AttractorResult;
import attractors1.math.AttractorResult.AttractorResult3d;
import attractors1.math.Generator;
import attractors1.math.Point3d;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Logic class that calculates how points should be displayed based on an AttractorResult.
 */
public class ParameterSpaceRenderer {

  // maxima/minima for ArrayParams.
  // final?
  private double minXParam = -1;
  private double maxXParam = 1;
  private double minYParam = -1;
  private double maxYParam = 1;

  // indices of the ArrayParams
  private int indexXParam = 0;
  private int indexYParam = 1;

  private final Generator<Point3d, ArrayParams> generator;
  private final ArrayParams baseParams;

  private static final int ITERATIONS = 1000;
  private static final int FLUSH = 100;

  private static final int RENDER_THREADS = 10;

  private final ExecutorService executor = Executors.newFixedThreadPool(RENDER_THREADS);

  public static interface Listener {
    void displayUpdated(Quadtree qt);
  }

  public ParameterSpaceRenderer(Generator<Point3d, ArrayParams> generator, ArrayParams basePrams) {
    this.generator = generator;
    this.baseParams = basePrams;
  }

  /**
   * Shutdown calculation after calculate() has been called.
   */
  public void shutdown() {
    executor.shutdownNow();
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

        double xParam = ((double) x / resolution) * (maxXParam - minXParam) + minXParam;
        double yParam = ((double) y / resolution) * (maxYParam - minYParam) + minYParam;

        ArrayParams newParams = new ArrayParams(baseParams.getData())
                .withValue(indexXParam, xParam)
                .withValue(indexYParam, yParam);

        Pixel pixel = new Pixel();
        pixel.params = newParams;
        pixel.x = x;
        pixel.y = y;
        pixels.add(pixel);

//        executor.submit(new GenerateResultCallable(newParams, x, y, quadtree, listener));
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

//  public BufferedImage render(int xSize, int ySize) {
//    BufferedImage image = new BufferedImage(xSize, ySize, BufferedImage.TYPE_INT_RGB);
//
//    for(int x=0;x<xSize;x++) {
//      System.out.println("Calculating param space line "+x+" of "+xSize);
//      for (int y = 0; y < ySize; y++) {
//
//        double xParam = ((double) x / xSize) * (maxXParam - minXParam) + minXParam;
//        double yParam = ((double) y / ySize) * (maxYParam - minYParam) + minYParam;
//
//        ArrayParams newParams = new ArrayParams(baseParams.getData())
//                .withValue(indexXParam, xParam)
//                .withValue(indexYParam, yParam);
//
////        executor.submit(new )
//        AttractorResult3d result = generateResult(newParams);
//        Color color = calculateColor(result);
//
//        image.setRGB(x, y, color.getRGB());
//      }
//    }
//
//    return image;
//  }

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
      AttractorResult3d result = generateResult(newParams);
      quadtree.setResult(x, y, result);
      listener.displayUpdated(quadtree);
    }
  }

  int generated = 0;

  private AttractorResult3d generateResult(ArrayParams newParams) {
    AttractorFunction<Point3d, ArrayParams> function = generator.newFunction(newParams);
    List<Point3d> points = function.iterate(Point3d.ZERO, ITERATIONS, FLUSH);
    return new AttractorResult3d(newParams, function, points);
  }
}
