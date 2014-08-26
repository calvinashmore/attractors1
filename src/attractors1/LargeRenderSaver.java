/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1;

import attractors1.math.ArrayParams;
import attractors1.math.AttractorFunction;
import attractors1.math.Point3d;
import attractors1.math.cubes.MarchingCubes;
import attractors1.math.cubes.Triangle;
import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.Executors;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 *
 * @author ashmore
 */
class LargeRenderSaver extends JPanel implements MarchingCubes.ProgressListener{

  private static final int ITERATIONS = 500000;
  private static final int FLUSH = 10000;

  // number of slices for the marching cubes
  private static final int SLICES = 500;
//  private static final int SLICES = 100;

  // the size of the metaballs in generating an isosurface
  private static final double METABALL_SIZE = .005;
//  private static final double METABALL_SIZE = .02;

  // the radius to search for points in building the isofield
  private static final double ISO_RADIUS = METABALL_SIZE * 5;

  private final AttractorFunction<Point3d, ArrayParams> fn;
  private final File destination;
  private final JLabel status;
  private final JLabel triangleLabel;
  private final JLabel countLabel;
  private final JProgressBar progressBar;

  public LargeRenderSaver(AttractorFunction<Point3d, ArrayParams> fn, File destination) {
    this.destination = destination;
    this.fn = fn;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    add(status = new JLabel());
    add(triangleLabel = new JLabel());
    add(countLabel = new JLabel());
    add(progressBar = new JProgressBar());

    setPreferredSize(new Dimension(300, 100));
  }

  @Override
  public void progress(int line, int totalLines, int triangles) {
    progressBar.setMinimum(0);
    progressBar.setMaximum(totalLines-1);
    progressBar.setValue(line);
    triangleLabel.setText("triangles: "+triangles);
    countLabel.setText(String.format("%d out of %d lines",line,totalLines));
  }

  public void start() {

    Executors.newSingleThreadExecutor().execute(new Runnable() {

      @Override
      public void run() {
        status.setText("Calculating points...");
        List<Point3d> points = fn.iterate(Point3d.ZERO, ITERATIONS, FLUSH);
        points = Point3d.normalize(points);

        try {
          status.setText("Tesselating...");
          List<Triangle> tris = new Tesselator(points, LargeRenderSaver.this)
                  .saveToObj(ISO_RADIUS, METABALL_SIZE, SLICES);
          status.setText("Smoothing...");
          tris = Tesselator.averageVertices(tris);
          status.setText("Saving...");
          MarchingCubes.saveTriangles(tris, destination);
          status.setText("Done!");
        } catch (FileNotFoundException ex) {
          throw new RuntimeException(ex);
        }
      }
    });
  }

  public void stop() {
    // not yet
  }
}
