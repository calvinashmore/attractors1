/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1;

import attractors1.fn.scripting.ScriptLoader;
import attractors1.fn.scripting.ScriptedFn;
import attractors1.math.ArrayParams;
import attractors1.math.AttractorFunction;
import attractors1.math.Point3d;
import attractors1.math.cubes.Tesselator;
import com.google.common.base.Joiner;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.script.ScriptException;
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

  public static void main(String args[]) throws Exception {
    if(args.length > 0) {
      // render a file
      new CommandLineRenderSaver(args).render();
      return;
    }

    JFrame frame = new JFrame("meef");
    frame.setPreferredSize(new Dimension(700, 500));
    frame.add(new ScriptedDisplay());
    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}
