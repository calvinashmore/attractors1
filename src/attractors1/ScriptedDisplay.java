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

    if(args.length == 1) {
      // render a file
      AttractorFunction fn = readFn(args[0]);
      String outFilename = args[0] + ".obj";
      LargeRenderSaver saver = new LargeRenderSaver(fn, new File(outFilename));
      saver.startBlocking();
      return;
    }

    JFrame frame = new JFrame("meef");
    frame.setPreferredSize(new Dimension(700, 500));
    frame.add(new ScriptedDisplay());
    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }


  private static AttractorFunction readFn(String filename) throws IOException {
    File inFile = new File(filename);

    try (FileInputStream inStream = new FileInputStream(inFile)) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

      List<String> lines = new ArrayList<>();
      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }

      if (lines.isEmpty()) {
        throw new IllegalArgumentException("no contents");
      }

      if (!lines.get(0).startsWith(ScriptedEditor.PARAM_SAVE_PREFIX)) {
        throw new IllegalArgumentException("no parameters");
      }
      String paramLine = lines.get(0);
      paramLine = paramLine.substring(ScriptedEditor.PARAM_SAVE_PREFIX.length());

      ArrayParams params = ArrayParams.parse(paramLine);
      lines.remove(0);

      String joinedLines = Joiner.on("\n").join(lines);

      try {
        return new ScriptedFn(params, new ScriptLoader().loadScript(joinedLines));
      } catch (ScriptException ex) {
        throw new IllegalArgumentException("problem loading script", ex);
      }
    }
  }
}
