/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.parameters;

import attractors1.math.AttractorResult;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author ashmore
 */
public class ResultView extends JPanel implements ParameterSpaceRendererPanel.HoverListener {

  private final JLabel contents;

  public ResultView() {
    setLayout(new BorderLayout());
    setBorder(new EmptyBorder(5, 5, 5, 5));
    add(contents = new JLabel(), BorderLayout.WEST);
  }

  @Override
  public void onHover(AttractorResult result) {
    String statsText = result.getStats();
    statsText = "<html>"+statsText+"</html>";
    statsText = statsText.replaceAll("\n", "<br>");
    contents.setText(statsText);
  }

}
