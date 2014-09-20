/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1;

import attractors1.math.ArrayParams;
import attractors1.math.AttractorFunction;
import attractors1.math.Point3d;
import attractors1.math.cubes.Tesselator;
import java.awt.Dimension;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 *
 * @author ashmore
 */
public class LargeRenderSaverPanel extends JPanel implements Tesselator.ProgressListener {

  private final JLabel status;
  private final JLabel triangleLabel;
  private final JLabel countLabel;
  private final JProgressBar progressBar;

  private final LargeRenderSaver renderSaver;

  public LargeRenderSaverPanel(AttractorFunction<Point3d, ArrayParams> fn, File destination) {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    add(status = new JLabel());
    add(triangleLabel = new JLabel());
    add(countLabel = new JLabel());
    add(progressBar = new JProgressBar());

    setPreferredSize(new Dimension(300, 100));

    LargeRenderSaver.Logger logger = new LargeRenderSaver.Logger() {
      @Override
      public void setText(String text) {
        status.setText(text);
      }
    };

    renderSaver = new LargeRenderSaver(this, logger, destination, fn, new RenderSaverParameters());
  }

  public void start() {
    renderSaver.start();
  }

  public void stop() {
    renderSaver.stop();
  }

  @Override
  public void progress(int line, int totalLines, int triangles) {
    progressBar.setMinimum(0);
    progressBar.setMaximum(totalLines-1);
    progressBar.setValue(line);
    triangleLabel.setText("triangles: "+triangles);
    countLabel.setText(String.format("%d out of %d chunks",line,totalLines));
  }

}
