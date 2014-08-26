/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1;

import attractors1.math.Point3d;
import attractors1.math.cubes.MarchingCubes;
import attractors1.math.cubes.Triangle;
import attractors1.math.octree.IsoField;
import attractors1.math.octree.Octree;
import attractors1.math.octree.OctreeIsoField;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.SetMultimap;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author ashmore
 */
public class Tesselator {
  private final List<Point3d> points;
  private final MarchingCubes.ProgressListener listener;

  public Tesselator(List<Point3d> points, MarchingCubes.ProgressListener listener) {
    this.points = points;
    this.listener = listener;
  }

  public List<Triangle> saveToObj(double isoRadius, double metaballSize, int slices) throws FileNotFoundException {
    IsoField iso = new OctreeIsoField(new Octree(points), isoRadius, metaballSize);

    Point3d unitCube = new Point3d(1,1,1).multiply(1+2*isoRadius);
    List<Triangle> tris = MarchingCubes.tesselate(iso, listener, slices, unitCube.multiply(-1), unitCube);
    System.out.println("Created "+tris.size()+" triangles");
    return tris;
  }

  void saveToObj(File destination, double ISO_RADIUS, double METABALL_SIZE, int SLICES) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  public static List<Triangle> averageVertices(List<Triangle> tris) {
    SetMultimap<Point3d, Point3d> edges = HashMultimap.create();

    // doesn't seem the best way of doing this, but oh well.
    for(Triangle triangle : tris) {
      for(Point3d point1 : triangle.getPoints()) {
        for(Point3d point2 : triangle.getPoints()) {
          if(point1 == point2)
            continue;
          edges.put(point1, point2);
        }
      }
    }

    List<Triangle> averagedTriangles = new ArrayList<>(tris.size());
    for(Triangle triangle : tris) {
      averagedTriangles.add(new Triangle(
              average(triangle.getPoints()[0], edges),
              average(triangle.getPoints()[1], edges),
              average(triangle.getPoints()[2], edges)));
    }
    return averagedTriangles;
  }

  private static Point3d average(Point3d point, SetMultimap<Point3d, Point3d> edges) {
    double centerWeight = 2;
    double nextNeighborWeight = .25;

    double totalWeight = centerWeight;

    Set<Point3d> neighbors = edges.get(point);
    Set<Point3d> nextNeighbors = new HashSet<>();
    Point3d average = point.multiply(centerWeight);
    for(Point3d neighbor : neighbors) {
      average = average.add(neighbor);
      totalWeight += 1;

      nextNeighbors.addAll(edges.get(neighbor));
    }

    nextNeighbors.remove(point);
    nextNeighbors.removeAll(neighbors);
    for(Point3d neighbor : nextNeighbors) {
      average = average.add(neighbor.multiply(nextNeighborWeight));
      totalWeight += nextNeighborWeight;
    }

    return average.multiply(1.0 / totalWeight);
  }
}
