/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.parameters;

import attractors1.math.AttractorResult;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 * Quadtree to represent partial rendering of parameter space view.
 * Notes: Mutable; we expect to grow this quadtree as more points are rendered.
 */
public class Quadtree {

  private final int maxLevel;
  private final Cell root = new Cell();

  // for square resolution of 2^maxLevel
  public Quadtree(int maxLevel) {
    this.maxLevel = maxLevel;
  }

  public void setResult(int x, int y, AttractorResult result) {
    // assume 0 <= {x,y} < 2^maxLevel
    root.setResult(x,y,result);
  }

  public void render(Graphics2D g, ResultPixelator pixelator) {
    root.render(g, pixelator, resolution(), resolution());
  }

  public int resolution() {
    return (int) Math.pow(2, maxLevel);
  }

  public AttractorResult getResult(int x, int y) {
    return root.getResult(x,y);
  }


  private class Cell {
    private int level;
    // children have indices as follow:
    // 0 1
    // 2 3
    private Cell[] children = new Cell[4];
    // this will be non null when level == maxLevel
    private AttractorResult result;
    // # of children that are not null
    private int filledChildren;
    // sum(i in 0..3; 2^i if children[i] != null
    // can have possible values of [0,16)
    private int childrenSignature;

    public void setResult(int x, int y, AttractorResult result) {

      if (level == maxLevel) {
        // maxed out
        this.result = result;
        return;
      }

      // child index
      int index = 0;
      int halfSize = 1 << (maxLevel - level - 1);
      if(x >= halfSize) {
        index += 1;
        x -= halfSize;
      }
      if(y >= halfSize) {
        index += 2;
        y -= halfSize;
      }

      if(children[index] == null) {
        children[index] = new Cell();
        children[index].level = level+1;
        filledChildren++;
        childrenSignature += 1 << index;
      }

      children[index].setResult(x, y, result);
    }

    // x and y are in full coordinates with respect to here
    public AttractorResult getResult(int x, int y) {
      if (level == maxLevel)
        return result;
      if (filledChildren == 0)
        return null; // hopefully won't happen

      int halfSize = 1 << (maxLevel - level - 1);
      int ix = 0;
      int iy = 0;
      if (x >= halfSize) {
        ix = 1;
        x -= halfSize;
      }
      if (y >= halfSize) {
        iy = 1;
        y -= halfSize;
      }

      if(children[ix + 2*iy] != null)
        return children[ix + 2*iy].getResult(x, y);

      // that didn't work, try another one
      ix = ix == 0 ? 1 : 0;

      if(children[ix + 2*iy] != null)
        return children[ix + 2*iy].getResult(x, y);

      // again
      ix = ix == 0 ? 1 : 0;
      iy = iy == 0 ? 1 : 0;

      if(children[ix + 2*iy] != null)
        return children[ix + 2*iy].getResult(x, y);

      // and again
      ix = ix == 0 ? 1 : 0;

      if(children[ix + 2*iy] != null)
        return children[ix + 2*iy].getResult(x, y);

      // if we're here, we didn't find anything.
      return null;
    }

    public void render(Graphics2D g, ResultPixelator pixelator, int xRes, int yRes) {
      if(filledChildren == 0 || level == maxLevel) {
        g.setColor(pixelator.pixelate(result));
        g.fillRect(0, 0, xRes, yRes);
      } else if(filledChildren == 1) {
        // render at full resolution
        Cell onlyChild = firstNotNull(
                firstNotNull(children[0], children[1]),
                firstNotNull(children[2], children[3]));
        onlyChild.render(g, pixelator, xRes, yRes);
      } else if(filledChildren == 2) {
        if(childrenSignature == 3 || childrenSignature == 12) {
          // use vertical columns
          AffineTransform transform = g.getTransform();
          firstNotNull(children[0], children[2]).render(g, pixelator, xRes/2, yRes);
          g.translate(xRes/2, 0);
          firstNotNull(children[1], children[3]).render(g, pixelator, xRes/2, yRes);
          g.setTransform(transform);
        } else {
          // use horizontal things
          AffineTransform transform = g.getTransform();
          firstNotNull(children[0], children[1]).render(g, pixelator, xRes, yRes/2);
          g.translate(0, yRes/2);
          firstNotNull(children[2], children[3]).render(g, pixelator, xRes, yRes/2);
          g.setTransform(transform);
        }
      } else if(filledChildren == 3) {
        if(childrenSignature == 14 || childrenSignature == 13) {
          // fill top
          firstNotNull(children[0], children[1]).render(g, pixelator, xRes, yRes/2);
          renderChild(2, g, pixelator, xRes, yRes);
          renderChild(3, g, pixelator, xRes, yRes);
        } else {
          // fill bottom
          AffineTransform transform = g.getTransform();
          g.translate(0,yRes/2);
          firstNotNull(children[0], children[1]).render(g, pixelator, xRes, yRes/2);
          g.setTransform(transform);
          renderChild(0, g, pixelator, xRes, yRes);
          renderChild(1, g, pixelator, xRes, yRes);
        }
      } else if(filledChildren == 4) {
        for(int i=0; i<4; i++) {
          renderChild(i, g, pixelator, xRes, yRes);
        }
      }
    }

    private void renderChild(int i, Graphics2D g, ResultPixelator pixelator, int xRes, int yRes) {
      int xOffset = i%2;
      int yOffset = i/2;
      AffineTransform transform = g.getTransform();
      g.translate(xOffset*xRes/2, yOffset*yRes/2);
      children[i].render(g, pixelator, xRes/2, yRes/2);
      g.setTransform(transform);
    }
  }

  private static <T> T firstNotNull(T a, T b) {
    if (a != null)
      return a;
    else return b;
  }
}
