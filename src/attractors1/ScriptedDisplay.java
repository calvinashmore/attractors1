/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 *
 * @author ashmore
 */
public class ScriptedDisplay extends JPanel {

  public ScriptedDisplay() {
    super(new BorderLayout());

    RendererPanel renderer = new Renderer3d();
    renderer.setPreferredSize(new Dimension(-1, -1));

    JPanel editor = new ScriptedEditor(renderer);

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, renderer, editor);
    splitPane.setResizeWeight(0.75);
    add(splitPane, BorderLayout.CENTER);
  }

  public static void main(String args[]) {
    JFrame frame = new JFrame("meef");
    frame.setPreferredSize(new Dimension(700, 500));
    frame.add(new ScriptedDisplay());
    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}
