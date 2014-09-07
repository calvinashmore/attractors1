/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math.cubes;

import attractors1.math.Point3d;
import attractors1.math.octree.IsoField;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ashmore
 */
public class Tesselator {

  private static final double ISO_LEVEL = 1.0;
  private static final int NUMBER_THREADS = 10;
  private static final int CHUNKS = 10;

  private final ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);

  public interface ProgressListener {
    public void progress(int line, int totalLines, int triangles);
  }

  private final IsoField isoField;
  private final ProgressListener listener;
//  private final int size;
  private final Point3d min;
  private final Point3d max;
  private final int chunkResolution;

  /**
   * This will work best if size is a multiple of {@link #CHUNKS}.
   */
  public Tesselator(IsoField field, ProgressListener listener, int size, Point3d min, Point3d max) {
    this.isoField = field;
    this.listener = listener;
//    this.size = size;
    this.min = min;
    this.max = max;
    this.chunkResolution = size / CHUNKS;
  }

  /**
   * This is blocking, but launches new threads.
   */
  public List<Triangle> tesselate() {
    final List<Triangle> allTriangles = new ArrayList<>();
    final AtomicInteger totalProgress = new AtomicInteger();
    final AtomicInteger triangleCount = new AtomicInteger();
    final int progressSize = CHUNKS * CHUNKS * CHUNKS;

    for(int ix=0; ix<CHUNKS; ix++)
    for(int iy=0; iy<CHUNKS; iy++)
    for(int iz=0; iz<CHUNKS; iz++) {

      final int x = ix;
      final int y = iy;
      final int z = iz;
      executor.submit(new Runnable() {
        @Override
        public void run() {
          System.out.println("Processing chunk: "+x+" "+y+" "+z);
          List<Triangle> chunkTriangles = renderChunk(x, y, z);

          int currentProgress = totalProgress.addAndGet(1);
          int currentTriangles = triangleCount.addAndGet(chunkTriangles.size());
          listener.progress(currentProgress, progressSize, currentTriangles);

          synchronized(allTriangles) {
            allTriangles.addAll(chunkTriangles);
          }
        }
      });
    }

    try {
      executor.shutdown();
      executor.awaitTermination(10000, TimeUnit.DAYS);
    } catch (InterruptedException ex) {
      Logger.getLogger(Tesselator.class.getName()).log(Level.SEVERE, null, ex);
    }

    return allTriangles;
  }

  // to be called within a render thread.
  private List<Triangle> renderChunk(int cx, int cy, int cz) {
    // build chunk
    Point3d chunkDelta = max.subtract(min).multiply(1.0 / CHUNKS);
    double dx = chunkDelta.getX();
    double dy = chunkDelta.getY();
    double dz = chunkDelta.getZ();
    Point3d chunkMin = min.add(new Point3d(cx*dx, cy*dy, cz*dz));
    Point3d chunkMax = min.add(new Point3d((cx+1)*dx, (cy+1)*dy, (cz+1)*dz));
    Chunk chunk = new Chunk(chunkResolution, chunkMin, chunkMax);

    // this will take a while
    chunk.populate(isoField);

    ArrayList<Triangle> chunkTriangles = new ArrayList<>();

    for(int ix=0; ix<chunkResolution; ix++)
    for(int iy=0; iy<chunkResolution; iy++)
    for(int iz=0; iz<chunkResolution; iz++) {
      GridCell gridCell = chunk.getGridCell(ix, iy, iz);
      MarchingCubes.polygonise(gridCell, ISO_LEVEL, chunkTriangles);
    }

    return chunkTriangles;
  }
}
