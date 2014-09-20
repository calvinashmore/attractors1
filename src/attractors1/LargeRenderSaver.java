/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1;

import attractors1.math.ArrayParams;
import attractors1.math.AttractorFunction;
import attractors1.math.Point3d;
import attractors1.math.cubes.Tesselator;
import attractors1.math.cubes.Tesselator.ProgressListener;
import attractors1.math.cubes.Triangle;
import attractors1.math.octree.DensityFunction;
import attractors1.math.octree.DensityFunctions;
import attractors1.math.octree.IsoField;
import attractors1.math.octree.Octree;
import attractors1.math.octree.OctreeIsoField;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 *
 * @author ashmore
 */
class LargeRenderSaver {

  public interface Logger {
    public void setText(String text);
  }


//  private static final int DENSITY = 10;
//  private static final int ITERATIONS = 100000 * DENSITY;
//  private static final int FLUSH = 10000;
//
//  // box size is from -1 to +1
//  // upload as mm, so total size will be ~300mm cubed
//  private static final double SIZE_MILLIMETERS = 180;
//
//  // number of slices for the marching cubes
//  private static final int SLICES = 500;
////  private static final int SLICES = 100;
//
//  // the size of the metaballs in generating an isosurface
////  private static final double METABALL_SIZE = .005;
//  private static final double METABALL_SIZE = .010;
//
//  private static final DensityFunction DENSITY_FUNCTION = DensityFunctions.sumPow(
//          METABALL_SIZE, 1.0/DENSITY, 5.0);
//
//  // the radius to search for points in building the isofield
//  private static final double ISO_RADIUS = METABALL_SIZE * 5;

  private final AttractorFunction<Point3d, ArrayParams> fn;
  private final File destination;
  private final ProgressListener progressListener;
  private final Logger logger;
  private final RenderSaverParameters renderParams;

  public LargeRenderSaver(ProgressListener progressListener, Logger logger, File destination, AttractorFunction<Point3d, ArrayParams> fn, RenderSaverParameters renderParams) {
    this.progressListener = progressListener;
    this.logger = logger;
    this.destination = destination;
    this.fn = fn;
    this.renderParams = renderParams;
  }

  public void start() {
    Executors.newSingleThreadExecutor().execute(createRunnable());
  }

  public void startBlocking() {
    createRunnable().run();
  }

  private Runnable createRunnable() {
    return new Runnable() {
      @Override
      public void run() {
        logger.setText("Calculating points...");
        int iterations = renderParams.iterationMultiplier * renderParams.density;
        List<Point3d> points = fn.iterate(Point3d.ZERO, iterations, renderParams.flush);
        points = Point3d.normalize(points);

        try {
          logger.setText("Tesselating...");
          double isoRadius = renderParams.isoMultiplier * renderParams.metaballSize;
          IsoField iso = new OctreeIsoField(new Octree(points), isoRadius, renderParams.metaballSize, renderParams.densityFunction);
          List<Triangle> tris = new Tesselator(iso, progressListener, renderParams.slices,
                  new Point3d(1,1,1).multiply(-1-2*isoRadius), new Point3d(1,1,1).multiply(1+2*isoRadius),
                  renderParams.threads, renderParams.chunks).tesselate();
          logger.setText("Smoothing...");
          tris = Tesselator.averageVertices(tris);
          logger.setText("Saving...");

          tris = Lists.transform(tris, new Function<Triangle, Triangle>() {
            @Override public Triangle apply(Triangle f) {
              return new Triangle(
                      f.getPoints()[0].multiply(renderParams.sizeMillimeters/2),
                      f.getPoints()[1].multiply(renderParams.sizeMillimeters/2),
                      f.getPoints()[2].multiply(renderParams.sizeMillimeters/2));
            }
          });

          saveTriangles(tris, destination);
          logger.setText("Done!");
        } catch (FileNotFoundException ex) {
          throw new RuntimeException(ex);
        }
      }
    };
  }

  public void stop() {
    // not yet
  }

  /**
   * Save triangles in wavefront obj format.
   */
  public static void saveTriangles(List<Triangle> tris, File file)
          throws FileNotFoundException {
    Map<Point3d, Integer> points = new HashMap<>();
    int pointCount = 1;

    PrintStream outStream = new PrintStream(file);

    int count = 0;
    for(Triangle t : tris) {

      for(Point3d p : t.getPoints()) {
        if(!points.containsKey(p)) {
          points.put(p, pointCount++);
          outStream.printf("v %f %f %f\n", p.getX(), p.getY(), p.getZ());
        }
      }

      outStream.printf("f %d %d %d\n",
              points.get(t.getPoints()[0]),
              points.get(t.getPoints()[1]),
              points.get(t.getPoints()[2]));

      count++;
      if(count % 10000 == 0) {
        System.out.println("Saved "+count+" triangles of "+tris.size());
      }
    }
  }
}
