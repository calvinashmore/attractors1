/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1;

import attractors1.parameters.ParameterSpaceRenderer;
import attractors1.fn.AbstractFn;
import attractors1.fn.BasicGenerator;
import attractors1.fn.scripting.FnScript;
import attractors1.fn.scripting.ScriptLoader;
import attractors1.fn.scripting.ScriptedFn;
import attractors1.fn.scripting.ScriptedGenerator;
import attractors1.math.ArrayParams;
import attractors1.math.AttractorFunction;
import attractors1.math.Point3d;
import attractors1.math.octree.Octree;
import attractors1.parameters.ParameterSpaceRendererPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.script.ScriptException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

/**
 *
 * @author ashmore
 */
public class ScriptedDisplay extends JPanel {

  private RendererPanel renderer;

  // nullable
  private ScriptedGenerator generator = null;
  private String lastContents = null;
  private JTextArea textArea;
  private JButton renderButton;

  private ExecutorService executor = Executors.newCachedThreadPool();

  private ArrayParams currentParams;

  public ScriptedDisplay() {
    super(new BorderLayout());

    renderer = new Renderer3d();
    renderer.setPreferredSize(new Dimension(-1, -1));


    JPanel editor = new JPanel(new BorderLayout());
    add(editor, BorderLayout.EAST);
    textArea = new JTextArea();
    textArea.setText("from java.lang import Math\n" +
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
        "      v.z*v.z + p[2]*v.x + p[5])");
    JScrollPane textAreaScrollPane = new JScrollPane(textArea);
    editor.add(textAreaScrollPane, BorderLayout.CENTER);

    JPanel buttons = new JPanel();
    editor.add(buttons, BorderLayout.SOUTH);

    renderButton = new JButton("render");
    buttons.add(renderButton);
    renderButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        generate();
      }
    });

    JButton paramButton = new JButton("params??");
    buttons.add(paramButton);
    paramButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

//        ParameterSpaceRenderer paramRenderer = new ParameterSpaceRenderer(generator, currentParams);

        final JFrame frame = new JFrame("omg");
        final ParameterSpaceRendererPanel paramRenderer = new ParameterSpaceRendererPanel(new ParameterSpaceRendererPanel.ParamListener() {

          @Override
          public void onParams(ArrayParams params) {
            GenerationResult result = generatePoints(generator, params);
            renderer.setPoints(result.getPoints());
          }
        });
        paramRenderer.setDisplay(generator, currentParams);
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
    });

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, renderer, editor);
    splitPane.setResizeWeight(0.75);
    add(splitPane, BorderLayout.CENTER);
  }

  /**
   * Called within UI thread, does work of generating new parameters.
   */
  private void generate() {
    renderButton.setEnabled(false);

    if (!textArea.getText().equals(lastContents)) {
      // attempt to load a script.
      FnScript script;
      try {
        script = new ScriptLoader().loadScript(textArea.getText());
      } catch (ScriptException ex) {
        // todo: show message
        ex.printStackTrace();
        renderButton.setEnabled(true);
        return;
      }

      lastContents = textArea.getText();
      generator = new ScriptedGenerator(script);
    }

    // generator should not be null
    executor.submit(new Runnable() {
      @Override
      public void run() {
        try {
          GenerationResult result = generatePoints(generator);
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

  private static final int ATTEMPTS = 10;

  private static final int ITERATIONS = 50000;
  private static final int FLUSH = 10000;

  private static class GenerationResult {
    final private ArrayParams params;
    final private List<Point3d> points;

    public GenerationResult(ArrayParams params, List<Point3d> points) {
      this.params = params;
      this.points = points;
    }

    public ArrayParams getParams() {
      return params;
    }

    public List<Point3d> getPoints() {
      return points;
    }
  }

  private static GenerationResult generatePoints(ScriptedGenerator generator, ArrayParams params) {
    AttractorFunction<Point3d, ArrayParams> fn = generator.newFunction(params);
    List<Point3d> points = fn.iterate(Point3d.ZERO, ITERATIONS, FLUSH);
    points = Point3d.normalize(points);

    try {
      System.out.println("lyapunov:  " + fn.calculateLyapunov(Point3d.ZERO));
      double dimension = new Octree(points,10).fractalDimension();
      System.out.println("dimension: " + dimension);
    } catch(IllegalArgumentException ex) {
      // sometimes the quadtree can fail to calculate.
      // Don't explode.
    }
    return new GenerationResult(fn.getParameters(), points);
  }

  private static GenerationResult generatePoints(ScriptedGenerator generator) {
    for (int i = 0; i < ATTEMPTS; i++) {
      ScriptedFn fn = generator.generate();
      if (fn == null) {
        continue;
      }

      List<Point3d> points = fn.iterate(Point3d.ZERO, ITERATIONS, FLUSH);
      if(!fn.isBounded(points.get(points.size()-1))) {
        continue;
      }
      points = Point3d.normalize(points);

      double dimension = new Octree(points,10).fractalDimension();
      if (dimension < .8) {
        continue;
      }

      System.out.println("lyapunov:  " + fn.calculateLyapunov(Point3d.ZERO));
      System.out.println("params: " + fn.getParameters());
      System.out.println("dimension: " + dimension);
      return new GenerationResult(fn.getParameters(), points);
    }

    System.out.println("failed to find function!");
    return null;
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
