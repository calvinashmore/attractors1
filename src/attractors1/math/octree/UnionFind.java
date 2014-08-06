/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math.octree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author ashmore
 */
public class UnionFind {

  private Map<Point3D, Point3D> partitionMap = new HashMap<>();

  public UnionFind(boolean[][][] data) {
    int cells = 0;
    for(int x=0;x<data.length;x++)
    for(int y=0;y<data[0].length;y++)
    for(int z=0;z<data[0][0].length;z++) {
      if(!data[x][y][z])
        continue;
      addCell(x,y,z);
      cells++;
    }

    for(Point3D point : partitionMap.keySet()) {
      // reparent to root parent.
      partitionMap.put(point, getUltimateParent(point));
    }
  }

  private Point3D getUltimateParent(Point3D p) {
    Point3D parent = partitionMap.get(p);
    if(parent.equals(p))
      return parent;
    else return getUltimateParent(parent);
  }

  private void addCell(int x, int y, int z) {

    // this is our point
    Point3D point = new Point3D(x,y,z);

    // go through the neighbors, and collect the parents of all of them.
    Set<Point3D> parents = new HashSet<>();
    for(int i=-1;i<=1;i++)
    for(int j=-1;j<=1;j++)
    for(int k=-1;k<=1;k++) {
      if(i==0 && j==0 && k==0)
        continue;

      Point3D neighbor = new Point3D(x+i, y+j, z+k);
      Point3D parent = partitionMap.get(neighbor);
      if(parent != null) {
        parents.add(getUltimateParent(parent));
      }
    }

    if(parents.size() == 1) {
      // there's only one parent, so add this point to that parent.
      Point3D parent = parents.iterator().next();
      partitionMap.put(point, parent);
    } else {
      // this is where things get weird. There are zero or multiple parents.
      // Instead of joining the partitions immediately, reparent them to this node.
      partitionMap.put(point, point);
      for(Point3D parent : parents) {
        partitionMap.put(parent, point);
      }
    }
  }

  public int getPartitions() {
    return new HashSet(partitionMap.values()).size();
  }

  private class Point3D {
    int x, y, z;

    public Point3D(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final Point3D other = (Point3D) obj;
      if (this.x != other.x) {
        return false;
      }
      if (this.y != other.y) {
        return false;
      }
      if (this.z != other.z) {
        return false;
      }
      return true;
    }
  }

}
