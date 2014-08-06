/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math.octree;

import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author ashmore
 */
public class UnionFindTest {

  @Test
  public void testUnionFind() {
    boolean[][][] data = new boolean[1][1][3];
    data[0][0][0] = true;
    data[0][0][2] = true;
    UnionFind uf = new UnionFind(data);
    Assert.assertEquals(2, uf.getPartitions());
  }
}
