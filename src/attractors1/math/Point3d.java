/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author ashmore
 */
public class Point3d extends Linear<Point3d> {

  // tolerance for equals and hashCode
  private static final double TOLERANCE = .00001;

  public static final Point3d ZERO = new Point3d(0, 0, 0);
  public static final Point3d UNIT_X = new Point3d(1, 0, 0);
  public static final Point3d UNIT_Y = new Point3d(0, 1, 0);
  public static final Point3d UNIT_Z = new Point3d(0, 0, 1);

  private final double x, y, z;

  public Point3d(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }

  @Override
  public Point3d add(Point3d other) {
    return new Point3d(this.x+other.x, this.y+other.y, this.z+other.z);
  }

  @Override
  public Point3d multiply(double other) {
    return new Point3d(this.x*other, this.y*other, this.z*other);
  }

  @Override
  public double norm() {
    // L2 norm
    return Math.sqrt(x*x + y*y + z*z);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Point3d) {
      Point3d that = (Point3d) obj;
      return equalsWithinTolerance(this.x, that.x)
              && equalsWithinTolerance(this.y, that.y)
              && equalsWithinTolerance(this.z, that.z);
    } else {
      return false;
    }
  }

  private static boolean equalsWithinTolerance(double a, double b) {
    return Math.abs(a-b) < TOLERANCE;
  }

  @Override
  public int hashCode() {
    return Objects.hash(toleranceHash(x), toleranceHash(y), toleranceHash(z));
  }

  private static int toleranceHash(double a) {
    return (int) (a/TOLERANCE);
  }

  public double dot(Point3d point) {
    return x*point.x + y*point.y + z*point.z;
  }

  public Point3d cross(Point3d point) {
    return new Point3d(
            y*point.z - z*point.y,
            z*point.x - x*point.z,
            x*point.y - y*point.x);
  }

  @Override
  public String toString() {
    return String.format("<%f, %f, %f>", x, y, z);
  }

  /** Normalizes to -1 +1 */
  public static List<Point3d> normalize(List<Point3d> points) {
    double minx = Double.POSITIVE_INFINITY;
    double miny = Double.POSITIVE_INFINITY;
    double minz = Double.POSITIVE_INFINITY;
    double maxx = Double.NEGATIVE_INFINITY;
    double maxy = Double.NEGATIVE_INFINITY;
    double maxz = Double.NEGATIVE_INFINITY;

    for(Point3d point : points) {
      if(minx > point.x) minx = point.x;
      if(miny > point.y) miny = point.y;
      if(minz > point.z) minz = point.z;
      if(maxx < point.x) maxx = point.x;
      if(maxy < point.y) maxy = point.y;
      if(maxz < point.z) maxz = point.z;
    }

    System.out.println(String.format("x: [%f %f]", minx, maxx));
    System.out.println(String.format("y: [%f %f]", miny, maxy));
    System.out.println(String.format("z: [%f %f]", minz, maxz));

    List<Point3d> normalized = new ArrayList<>();
    for(Point3d point : points) {
      normalized.add(new Point3d(
              2*(point.x-minx)/(maxx-minx) - 1,
              2*(point.y-miny)/(maxy-miny) - 1,
              2*(point.z-minz)/(maxz-minz) - 1));
    }
    return normalized;
  }
}
