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
import attractors1.math.AttractorFunction;
import attractors1.math.Point3d;
import attractors1.parameters.ParameterSpaceRendererPanel;
import attractors1.parameters.ParameterSpaceView;
import com.google.common.base.Joiner;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.script.ScriptException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
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
  private final JButton largeRender;
  private final JPanel errorContainer;
  private final Renderer renderer;
  private final ExecutorService executor = Executors.newCachedThreadPool();
  private ArrayParams currentParams;

  public ScriptedEditor(Renderer renderer) {
    this.renderer = renderer;
    setLayout(new BorderLayout());
    scriptArea = new JTextArea();
    scriptArea.setText(INITIAL_SCRIPT);
    JScrollPane textAreaScrollPane = new JScrollPane(scriptArea);
    add(textAreaScrollPane, BorderLayout.CENTER);

    JPanel buttonsAndError = new JPanel(new BorderLayout());
    add(buttonsAndError, BorderLayout.SOUTH);

    errorContainer = new JPanel(new BorderLayout());
    buttonsAndError.add(errorContainer, BorderLayout.CENTER);

    JPanel buttons = new JPanel();
    buttonsAndError.add(buttons, BorderLayout.NORTH);
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
    buttons.add(largeRender = new JButton("save obj"));
    largeRender.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent e) {
        showLargeRender(generator.newFunction(currentParams));
      }
    });
    largeRender.setEnabled(false);

    JButton saveButton;
    buttons.add(saveButton = new JButton("save"));
    saveButton.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent e) {
        try {
          saveScriptAndParams();
        } catch (IOException ex) {
          throw new RuntimeException(ex);
        }
      }
    });

    JButton loadButton;
    buttons.add(loadButton = new JButton("load"));
    loadButton.addActionListener(new ActionListener() {
      @Override public void actionPerformed(ActionEvent e) {
        try {
          loadScriptAndParams();
        } catch (IOException ex) {
          throw new RuntimeException(ex);
        }
      }
    });
  }

  private static final String PARAM_SAVE_PREFIX = "#*** ";

  void saveScriptAndParams() throws IOException {
    JFileChooser fc = new JFileChooser();
    if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File outFile = fc.getSelectedFile();

    String textToSave = scriptArea.getText();
    if(currentParams != null) {
      String paramString = currentParams.toString();
      paramString = PARAM_SAVE_PREFIX + paramString + "\n";
      textToSave = paramString + textToSave;
    }

    try(FileOutputStream outStream = new FileOutputStream(outFile)) {
      outStream.write(textToSave.getBytes());
    }
  }

  void loadScriptAndParams() throws IOException {
    JFileChooser fc = new JFileChooser();
    if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File inFile = fc.getSelectedFile();

    try(FileInputStream inStream = new FileInputStream(inFile)) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

      List<String> lines = new ArrayList<>();
      String line;
      while ( (line = reader.readLine()) != null ) {
        lines.add(line);
      }

      if(lines.isEmpty())
        return;

      boolean foundParams = false;
      if(lines.get(0).startsWith(PARAM_SAVE_PREFIX)) {
        String paramLine = lines.get(0);
        paramLine = paramLine.substring(PARAM_SAVE_PREFIX.length());
        currentParams = ArrayParams.parse(paramLine);
        lines.remove(0);
        foundParams = true;
      }

      String joinedLines = Joiner.on("\n").join(lines);
      scriptArea.setText(joinedLines);

      if(foundParams) {
        generator = createGenerator(joinedLines);
        GenerationResult result = GenerationResult.generatePoints(generator, currentParams);
        renderer.setPoints(result.getPoints());
        largeRender.setEnabled(true);
        renderButton.setEnabled(true);
        lastContents = joinedLines;
      }
    }
  }

  void showLargeRender(AttractorFunction<Point3d, ArrayParams> fn) {
    final JFileChooser fc = new JFileChooser();
    if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File outFile = fc.getSelectedFile();

    JFrame frame = new JFrame(outFile.getName());
    final LargeRenderSaver renderSaver = new LargeRenderSaver(fn, outFile);
    frame.add(renderSaver);
    frame.pack();
    frame.addWindowListener(new WindowAdapter() {
      @Override public void windowClosing(WindowEvent e) {
        renderSaver.stop();
      }
    });
    renderSaver.start();
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setVisible(true);
  }

  /**
   * Does work of generating new parameters. Compilation part is called within UI thread.
   */
  private void generateNewParams() {
    renderButton.setEnabled(false);
    if (!scriptArea.getText().equals(lastContents)) {
      ScriptedGenerator scriptedGenerator = createGenerator(scriptArea.getText());
      if (scriptedGenerator == null) {
        return;
      }
      generator = scriptedGenerator;
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
            largeRender.setEnabled(true);
          }
        } finally {
          renderButton.setEnabled(true);
        }
      }
    });
  }

  private ScriptedGenerator createGenerator(String scriptText) {
    // attempt to load a script.
    FnScript script;
    try {
      script = new ScriptLoader().loadScript(scriptText);
      errorContainer.removeAll();
      validate();
    } catch (ScriptException ex) {
      errorContainer.add(new JScrollPane(new JTextArea(ex.getMessage(), 3, 40)), BorderLayout.CENTER);
      validate();
      renderButton.setEnabled(true);
      largeRender.setEnabled(false);
      return null;
    }
    lastContents = scriptArea.getText();
    return new ScriptedGenerator(script);
  }

  private void showParameterNavigator() throws HeadlessException {
    final JFrame frame = new JFrame("omg");
    final ParameterSpaceView paramRenderer = new ParameterSpaceView(new ParameterSpaceRendererPanel.ParamListener() {
      @Override
      public void onParams(ArrayParams params) {
        GenerationResult result = GenerationResult.generatePoints(generator, params);
        renderer.setPoints(result.getPoints());
        currentParams = params;
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
