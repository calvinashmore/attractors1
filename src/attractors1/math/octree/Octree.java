/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math.octree;

import attractors1.math.Point3d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author ashmore
 */
public class Octree {

  /**
   * Maximum allowable depth of a octree. A note about this term: We use the
   * ordinal values of the lowest level of a octree to perform spatial
   * partitioning. This means that a depth of 8 means that the total number of
   * unwrapped cells is 8^10, i.e. 1073741824.
   */
  private static final int MAX_MAX_DEPTH = 10;
  private final int maxDepth;

  private TreeCell root;
  private double minX;
  private double maxX;
  private double minY;
  private double maxY;
  private double minZ;
  private double maxZ;

  private final List<Point3d> points;
  private int numberSmallCells = 0;


  public Octree(List<Point3d> points) {
    this(points, 8);
  }

  public Octree(List<Point3d> points, int maxDepth) {
    assert maxDepth > 0 && maxDepth <= MAX_MAX_DEPTH;
    this.maxDepth = maxDepth;

    calculateBounds(points);

    root = new TreeCell(0, minX, maxX, minY, maxY, minZ, maxZ);

    for (Point3d point : points) {
      root.addPoint(point);
    }

    this.points = new ArrayList<>(points);
    Collections.sort(this.points, new PointComparator());

    root.makeIndexBounds(0);
  }

  /**
   * Returns the approximate fractal dimension calculated using box-counting.
   * Because the maximum depth of the quadtree is relatively low, this will be
   * an overestimate.
   */
  public double fractalDimension() {
    long boxSize = longPow(2, maxDepth);
    return Math.log(numberSmallCells) / Math.log(boxSize);
  }

  private void calculateBounds(List<Point3d> points) {
    minX = Double.POSITIVE_INFINITY;
    minY = Double.POSITIVE_INFINITY;
    minZ = Double.POSITIVE_INFINITY;
    maxX = Double.NEGATIVE_INFINITY;
    maxY = Double.NEGATIVE_INFINITY;
    maxZ = Double.NEGATIVE_INFINITY;

    for (Point3d point : points) {
      if(minX > point.getX()) minX = point.getX();
      if(minY > point.getY()) minY = point.getY();
      if(minZ > point.getZ()) minZ = point.getZ();
      if(maxX < point.getX()) maxX = point.getX();
      if(maxY < point.getY()) maxY = point.getY();
      if(maxZ < point.getZ()) maxZ = point.getZ();
    }
  }

  public double getMinX() {return minX;}
  public double getMinY() {return minY;}
  public double getMinZ() {return minZ;}
  public double getMaxX() {return maxX;}
  public double getMaxY() {return maxY;}
  public double getMaxZ() {return maxZ;}

  public List<Point3d> getPoints() {
    return Collections.unmodifiableList(points);
  }

  public int getNumberSmallCells() {
    return numberSmallCells;
  }

  public List<Point3d> getNearbyPoints(Point3d point, double radius) {
    if (root == null) {
      throw new IllegalStateException();
    }

    List<Point3d> r = new ArrayList();
    root.getPoints(r, point, radius);
    return r;
  }

  private class PointComparator implements Comparator<Point3d> {

    @Override
    public int compare(Point3d o1, Point3d o2) {
      return (int) (root.getOrdinal(o1) - root.getOrdinal(o2));
    }
  }

  private class TreeCell implements java.io.Serializable {

    // 8 children for the octree
    TreeCell[] children = new TreeCell[8];
    int depth;
    int count;
    int minIndex;
    double minRangeX;
    double midRangeX;
    double maxRangeX;
    double minRangeY;
    double midRangeY;
    double maxRangeY;
    double minRangeZ;
    double midRangeZ;
    double maxRangeZ;

    public int getDepth() {
      return depth;
    }

    public int getCount() {
      return count;
    }

    TreeCell(int depth, double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
      this.depth = depth;
      this.minRangeX = minX;
      this.maxRangeX = maxX;
      this.minRangeY = minY;
      this.maxRangeY = maxY;
      this.minRangeZ = minZ;
      this.maxRangeZ = maxZ;
      this.midRangeX = (minX + maxX) / 2;
      this.midRangeY = (minY + maxY) / 2;
      this.midRangeZ = (minZ + maxZ) / 2;

      if (depth == maxDepth) {
        numberSmallCells++;
      }
    }

    private String cellBounds() {
      return String.format("<[%f, %f],[%f, %f],[%f, %f]>",
              minRangeX, maxRangeX, minRangeY, maxRangeY, minRangeZ, maxRangeZ);
    }

    /**
     * Returns the partition index to which this point belongs, if it is inside
     * the cell. If it is not inside, throws an IllegalArgumentException.
     */
    int getPartition(Point3d point) {
      if(!isInsideCell(point)) throw new IllegalArgumentException(
              "point "+point+" is not inside cell: "+cellBounds());

      int partition = 0;
      if(point.getX() > midRangeX) partition += 1;
      if(point.getY() > midRangeY) partition += 2;
      if(point.getZ() > midRangeZ) partition += 4;
      return partition;
    }

    /**
     * Returns true if the point is inside the cell, or on its boundary.
     */
    boolean isInsideCell(Point3d point) {
      return point.getX() >= minRangeX && point.getX() <= maxRangeX
          && point.getY() >= minRangeY && point.getY() <= maxRangeY
          && point.getZ() >= minRangeZ && point.getZ() <= maxRangeZ;
    }

    private long getOrdinal(Point3d point) {
      if (depth == maxDepth) {
        return 0;
      }

      int relativeDepth = maxDepth - depth - 1;
      long level = longPow(8, relativeDepth);
      // 8 ^ relativeDepth

      int partition = getPartition(point);
      // should not be null when points are added
      return partition * level + children[partition].getOrdinal(point) ;
    }

    /**
     * Adds the given point to the cell.
     */
    void addPoint(Point3d point) {
      count++;
      if (depth == maxDepth) {
        return;
      }

      int partition = getPartition(point);
      if(children[partition] == null) {
        children[partition] = new TreeCell(depth+1,
          lower(point.getX(), minRangeX, midRangeX), upper(point.getX(), midRangeX, maxRangeX),
          lower(point.getY(), minRangeY, midRangeY), upper(point.getY(), midRangeY, maxRangeY),
          lower(point.getZ(), minRangeZ, midRangeZ), upper(point.getZ(), midRangeZ, maxRangeZ));
      }
      children[partition].addPoint(point);
    }

    private double lower(double x, double a, double b) {
      if(x<b) return a;
      else return b;
    }

    private double upper(double x, double a, double b) {
      if(x>a) return b;
      else return a;
    }

    /**
     * Calculates the bounds of the cell for building a sublist of the main
     * point list.
     */
    int makeIndexBounds(int start) {
      minIndex = start;

      if (depth < maxDepth) {
        int lastEnd = start;
        for(TreeCell child : children) {
          if(child != null) {
            lastEnd = child.makeIndexBounds(lastEnd);
          }
        }
        assert lastEnd == minIndex+count;
      }

      return minIndex+count;
    }

    /**
     * Returns all of the points that are in this cell
     */
    public List<Point3d> getContents() {
      return points.subList(minIndex, minIndex+count);
    }

    /**
     * Return all of the points that are within a certain radius of this point.
     * This fills the list provided with the contents of this cell which are
     * around the point. It does not actually affect the contents of the cell
     * itself!
     */
    public void getPoints(List<Point3d> currentList, Point3d point, double radius) {

      if (depth == maxDepth || isFullyInBubble(point, radius)) {
        currentList.addAll(getContents());
      } else {
        for(TreeCell child : children) {
          if(child != null && child.isInBubble(point, radius)) {
            child.getPoints(currentList, point, radius);
          }
        }
      }
    }

    /**
     * Returns true if this cell is at least partially in the bubble given by
     * the radius.
     */
    public boolean isInBubble(Point3d point, double radius) {
      return minRangeX - radius <= point.getX() && maxRangeX + radius >= point.getX()
          && minRangeY - radius <= point.getY() && maxRangeY + radius >= point.getY()
          && minRangeZ - radius <= point.getZ() && maxRangeZ + radius >= point.getZ();
    }

    /**
     * returns true if this cell is entirely in the bubble given by the radius.
     */
    public boolean isFullyInBubble(Point3d point, double radius) {
      return point.getX() - radius <= minRangeX && point.getX() + radius >= maxRangeX
          && point.getY() - radius <= minRangeY && point.getY() + radius >= maxRangeY
          && point.getZ() - radius <= minRangeZ && point.getZ() + radius >= maxRangeZ;
    }
  }

  private static long longPow(long a, long b) {
    assert(b >= 0);
    if(b == 0) return 1;
    return a*longPow(a,b-1);
  }
}
