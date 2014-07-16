/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math;

/**
 * matrix for handling linear transformations for Point3d.
 */
public class Matrix3d {
  private final double m[][] = new double[3][3];

  public Matrix3d(
          double m00, double m01, double m02,
          double m10, double m11, double m12,
          double m20, double m21, double m22) {
    m[0][0] = m00;
    m[1][0] = m01;
    m[2][0] = m02;
    m[1][0] = m10;
    m[1][1] = m11;
    m[1][2] = m12;
    m[2][0] = m20;
    m[2][1] = m21;
    m[2][2] = m22;
  }

  public Point3d apply(Point3d in) {
    return new Point3d(
        in.getX()*m[0][0] + in.getY()*m[0][1] + in.getZ()*m[0][2],
        in.getX()*m[1][0] + in.getY()*m[1][1] + in.getZ()*m[1][2],
        in.getX()*m[2][0] + in.getY()*m[2][1] + in.getZ()*m[2][2]);
  }

  /**
   * Rotate angle radians across axis.
   */
  public static Matrix3d rotation(Point3d axis, double angle) {
    axis = axis.normalize();
    double x = axis.getX();
    double y = axis.getY();
    double z = axis.getZ();
    double c = Math.cos(angle);
    double c1 = 1 - Math.cos(angle);
    double s = Math.sin(angle);
    return new Matrix3d(
            c + x*x*c1, x*y*c1 - z*s, x*z*c1 + y*s,
            y*x*c1 + z*s, c + y*y*c1, y*z*c1 - x*s,
            z*x*c1 - y*s, z*y*c1 + x*s, c + z*z*c1);
  }
}
