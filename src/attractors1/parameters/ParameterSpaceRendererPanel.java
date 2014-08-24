/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.parameters;

import attractors1.math.ArrayParams;
import attractors1.math.AttractorResult;
import attractors1.math.Generator;
import attractors1.math.Point3d;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 * View that uses a ParameterSpaceRenderer.
 */
public class ParameterSpaceRendererPanel extends JPanel implements ParameterSpaceRenderer.Listener {

  // actual max resolution is 2^ this value
  private static final int RESOLUTION_POWER = 7; // 2^7 = 128

  private ParameterSpaceRenderer renderer;
  private volatile Quadtree quadtree;

  public ParameterSpaceRendererPanel(final ParamListener paramListener, final HoverListener hoverListener) {
    MouseAdapter mouseAdapter = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        float x = (float) e.getX() / getWidth();
        float y = (float) e.getY() / getHeight();
        if(renderer != null)
          paramListener.onParams(renderer.getParams(x, y));
      }

      @Override
      public void mouseMoved(MouseEvent e) {
        float x = (float) e.getX() / getWidth();
        float y = (float) e.getY() / getHeight();
        if(quadtree != null) {
          int res = quadtree.resolution();
          int ix = (int) (x * res);
          int iy = (int) (y * res);

          AttractorResult result = quadtree.getResult(ix, iy);
//          System.out.println(result);
          hoverListener.onHover(result);
        }
      }
    };

    addMouseListener(mouseAdapter);
    addMouseMotionListener(mouseAdapter);
  }

  public interface ParamListener {
    void onParams(ArrayParams params);
  }

  public interface HoverListener {
    void onHover(AttractorResult result);
  }

  public void stopCalculation() {
    if(renderer != null) {
      renderer.shutdown();
    }
  }

  public void setDisplay(Generator<Point3d, ArrayParams> generator, ArrayParams basePrams, ParameterViewParameters viewParams) {
    stopCalculation();
    renderer = new ParameterSpaceRenderer(generator, basePrams, viewParams);
    this.quadtree = renderer.calculate(RESOLUTION_POWER, this);
  }

  @Override
  public Dimension getPreferredSize() {
    int res = (int) (4 * Math.pow(2, RESOLUTION_POWER));
    return new Dimension(res, res);
  }

  @Override
  public void displayUpdated(Quadtree qt) {
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if(quadtree != null) {
      Graphics2D g2 = (Graphics2D) g;
      g2.scale((float)getWidth()/quadtree.resolution(), (float)getHeight()/quadtree.resolution());
      quadtree.render(g2, PIXELATOR);
    }
  }

  private static final ResultPixelator<Point3d, ArrayParams> PIXELATOR = new ResultPixelator<Point3d, ArrayParams>() {
    @Override
    public Color pixelate(AttractorResult<Point3d, ArrayParams> result) {
      return calculateColor(result);
    }
  };

  private static Color calculateColor(AttractorResult result) {
    if (result == null) return Color.WHITE;

    double lyapunov = result.getLyapunov()-1;
    lyapunov = Math.max(0, Math.min(1, lyapunov));

    double dimension = result.getDimension()-1;
    dimension = Math.max(0, Math.min(1, dimension));

    if (result.getType() == AttractorResult.Type.CHAOTIC) {

      int maxPartitions = 8;
      double v = 1 - ((double)result.getPartitions() - 1) / maxPartitions;
      double partitions = Math.max(0, Math.min(1, v));

      return new Color((float) dimension, (float) lyapunov, (float) partitions);
    }
    else if (result.getType() == AttractorResult.Type.DIVERGENT) {
      float divergenceRatio = result.getDivergenceRatio();
      return new Color(divergenceRatio, 0, 0);
    }
    else if (result.getType() == AttractorResult.Type.CYCLIC) {
      // sqrt will mean that low cycle values will be darker.
      float cycleRatio = (float) Math.sqrt(result.getCycleRatio());
      return new Color(1-cycleRatio, 1-cycleRatio, 1);
    }
    else return Color.WHITE;
  }
}
