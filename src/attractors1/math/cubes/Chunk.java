/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math.cubes;

import attractors1.math.Point3d;
import attractors1.math.octree.IsoField;

/**
 *
 * @author ashmore
 */
class Chunk {
  private final int size; // size of a chunk edge
  private final Point3d min, max; // note: max will be value at size (not size-1)
  private final double[][][] values;

  public Chunk(int size, Point3d min, Point3d max) {
    this.size = size;
    this.min = min;
    this.max = max;
    // we want this to include the far edge, so if size is 3, we get [0, 1, 2, 3]
    // so when cells are calculated, they can include the full edges.
    this.values = new double[size+1][size+1][size+1];
  }

  private Point3d getPoint(int x, int y, int z) {
    return new Point3d(
            min.getX() + ((double)x/size) * (max.getX() - min.getX()),
            min.getY() + ((double)y/size) * (max.getY() - min.getY()),
            min.getZ() + ((double)z/size) * (max.getZ() - min.getZ()));
  }

  public void populate(IsoField iso) {
    for(int x=0;x<=size;x++)
    for(int y=0;y<=size;y++)
    for(int z=0;z<=size;z++) {
      values[x][y][z] = iso.getValue(getPoint(x, y, z));
    }
  }

  /**
   * we expect x,y,z &lt; size.
   */
  public GridCell getGridCell(int x, int y, int z) {
    GridCell cell = new GridCell();
    cell.p[0] = getPoint(x+0, y+0, z+0);
    cell.p[1] = getPoint(x+1, y+0, z+0);
    cell.p[2] = getPoint(x+1, y+1, z+0);
    cell.p[3] = getPoint(x+0, y+1, z+0);
    cell.p[4] = getPoint(x+0, y+0, z+1);
    cell.p[5] = getPoint(x+1, y+0, z+1);
    cell.p[6] = getPoint(x+1, y+1, z+1);
    cell.p[7] = getPoint(x+0, y+1, z+1);
    cell.val[0] = values[x+0][y+0][z+0];
    cell.val[1] = values[x+1][y+0][z+0];
    cell.val[2] = values[x+1][y+1][z+0];
    cell.val[3] = values[x+0][y+1][z+0];
    cell.val[4] = values[x+0][y+0][z+1];
    cell.val[5] = values[x+1][y+0][z+1];
    cell.val[6] = values[x+1][y+1][z+1];
    cell.val[7] = values[x+0][y+1][z+1];
    return cell;
  }
}
