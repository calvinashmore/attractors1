/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1;

import attractors1.fn.scripting.FnScript;
import attractors1.fn.scripting.ScriptLoader;
import attractors1.fn.scripting.ScriptedGenerator;
import attractors1.math.ArrayParams;
import attractors1.parameters.ParameterSpaceRendererPanel;
import attractors1.parameters.ParameterSpaceView;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.script.ScriptException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author ashmore
 */
class ScriptedEditor extends JPanel {
  private static final String INITIAL_SCRIPT =
          "from java.lang import Math\n" +
          "from attractors1.math import Point3d\n" +
          "\n" +
          "def paramSize():\n" +
          "  return 6\n" +
          "\n" +
          "def paramScale():\n" +
          "  return 1.5\n" +
          "\n" +
          "def apply(v, p):\n" +
          "  return Point3d(\n" +
          "      v.x*v.x + p[0]*v.y + p[3],\n" +
          "      v.y*v.y + p[1]*v.z + p[4],\n" +
          "      v.z*v.z + p[2]*v.x + p[5])";

  // nullable
  private ScriptedGenerator generator = null;
  private String lastContents = null;
  private final JTextArea scriptArea;
  private final JButton renderButton;
  private final RendererPanel renderer;
  private final ExecutorService executor = Executors.newCachedThreadPool();
  private ArrayParams currentParams;

  public ScriptedEditor(RendererPanel renderer) {
    this.renderer = renderer;
    setLayout(new BorderLayout());
    scriptArea = new JTextArea();
    scriptArea.setText(INITIAL_SCRIPT);
    JScrollPane textAreaScrollPane = new JScrollPane(scriptArea);
    add(textAreaScrollPane, BorderLayout.CENTER);
    JPanel buttons = new JPanel();
    add(buttons, BorderLayout.SOUTH);
    renderButton = new JButton("new params");
    buttons.add(renderButton);
    renderButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        generateNewParams();
      }
    });
    JButton paramButton = new JButton("param navigator");
    buttons.add(paramButton);
    paramButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        showParameterNavigator();
      }
    });
  }

  /**
   * Does work of generating new parameters. Compilation part is called within UI thread.
   */
  private void generateNewParams() {
    renderButton.setEnabled(false);
    if (!scriptArea.getText().equals(lastContents)) {
      // attempt to load a script.
      FnScript script;
      try {
        script = new ScriptLoader().loadScript(scriptArea.getText());
      } catch (ScriptException ex) {
        // todo: show message
        ex.printStackTrace();
        renderButton.setEnabled(true);
        return;
      }
      lastContents = scriptArea.getText();
      generator = new ScriptedGenerator(script);
    }
    // generator should not be null
    executor.submit(new Runnable() {
      @Override
      public void run() {
        try {
          GenerationResult result = GenerationResult.generatePoints(generator);
          if (result != null) {
            currentParams = result.getParams();
            renderer.setPoints(result.getPoints());
          }
        } finally {
          renderButton.setEnabled(true);
        }
      }
    });
  }

  private void showParameterNavigator() throws HeadlessException {
    final JFrame frame = new JFrame("omg");
    final ParameterSpaceView paramRenderer = new ParameterSpaceView(new ParameterSpaceRendererPanel.ParamListener() {
      @Override
      public void onParams(ArrayParams params) {
        GenerationResult result = GenerationResult.generatePoints(generator, params);
        renderer.setPoints(result.getPoints());
      }
    });
    if (currentParams != null) {
      paramRenderer.setDisplay(generator, currentParams);
    }
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        paramRenderer.stopCalculation();
        frame.dispose();
      }
    });
    frame.add(paramRenderer);
    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  }

}
