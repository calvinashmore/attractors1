/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math.cubes;

import attractors1.math.Point3d;

/**
 *
 * @author ashmore
 */
public class Triangle {
  private Point3d a,b,c;

  public Triangle(Point3d a, Point3d b, Point3d c) {
    this.a = a;
    this.b = b;
    this.c = c;
  }

  public Point3d[] getPoints() {
    return new Point3d[] {a,b,c};
  }
}
