/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math.octree;

import attractors1.math.Point3d;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author ashmore
 */
public class DensityFunctionVisualizer extends JPanel {

  public static void main(String args[]) {
    JFrame frame = new JFrame();
    frame.add(new DensityFunctionVisualizer());
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

  private static final int DENSITY = 100;
  private static final Random random = new Random();
  private static final double BALL_SIZE = .02;
  private DensityFunction densityFunction = DensityFunctions.sumPow(BALL_SIZE, 10*100.0/DENSITY, 2);
//  private DensityFunction densityFunction = DensityFunctions.nThMaxPlusMax(BALL_SIZE*3, 10);
//  private DensityFunction densityFunction = DensityFunctions.add(
//          DensityFunctions.multiply(DensityFunctions.nThMax(BALL_SIZE*3, 10), 2.0),
//          DensityFunctions.maximum(BALL_SIZE*2));
//  private DensityFunction densityFunction = DensityFunctions.sum(BALL_SIZE, .5*100.0/DENSITY);
  private List<Point3d> allPoints = new ArrayList<>();
  private double y;

  public DensityFunctionVisualizer() {
    setPreferredSize(new Dimension(500, 500));

    addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        int iy = e.getY();
        y = ((double)iy)/getHeight() * 2 - 1;
//        repaint();
      }
    });

    for(int i=0;i<DENSITY;i++) {
      allPoints.add(new Point3d(r(), r()/100, r()/100));
    }

    for(int i=0;i<DENSITY;i++) {
//      allPoints.add(new Point3d(r()/20, r()/20, r()/20));
      allPoints.add(new Point3d(r()/20, r()/100, r()/100));
    }
  }

  static double r() {
    return random.nextDouble()*2 - 1;
  }

  private void plot(Graphics g, double y) {
    int vprev=0;
    for(int ix=0;ix<getWidth();ix++) {

      double x = 2*((double)ix)/getWidth()-1;

      for(int iy=0;iy<getHeight();iy++) {
        double yy = 2*((double)iy)/getHeight()-1;
        Point3d p = new Point3d(x, yy, 0);
        double v = densityFunction.getValue(allPoints, p);
        g.setColor(Color.LIGHT_GRAY);
        if(v>1)
          g.fillOval(ix, iy, 2, 2);
      }

//      Point3d p = new Point3d(x, y, 0);
//      double v = 1-densityFunction.getValue(allPoints, p);
//      int iv = (int) (getHeight()*v);
//
//      if(ix>0){
//        g.setColor(Color.BLACK);
//        g.drawLine(ix-1, vprev, ix, iv);
//      }
//      vprev = iv;
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());

    plot(g,y);

    // draw points
    for(Point3d point : allPoints) {

      int ix = (int) (getWidth() * (point.getX()+1)/2);
      int iy = (int) (getHeight() * (point.getY()+1)/2);
      g.setColor(Color.RED);
      g.fillOval(ix, iy, 2, 2);

      g.setColor(Color.BLUE);
      int rx = (int) (getWidth()*BALL_SIZE/2);
      int ry = (int) (getHeight()*BALL_SIZE/2);
      g.drawOval(ix-rx, iy-ry, rx*2, ry*2);
    }
  }



}
