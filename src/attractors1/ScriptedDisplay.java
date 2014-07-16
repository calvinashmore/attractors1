/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1;

import attractors1.fn.AbstractFn;
import attractors1.fn.BasicGenerator;
import attractors1.fn.scripting.FnScript;
import attractors1.fn.scripting.ScriptLoader;
import attractors1.fn.scripting.ScriptedFn;
import attractors1.fn.scripting.ScriptedGenerator;
import attractors1.math.Point3d;
import attractors1.math.octree.Octree;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.script.ScriptException;
import javax.swing.JButton;
import javax.swing.JFrame;
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

  public ScriptedDisplay() {
    super(new BorderLayout());

    renderer = new Renderer3d();
    renderer.setPreferredSize(new Dimension(-1, -1));


    JPanel editor = new JPanel(new BorderLayout());
    add(editor, BorderLayout.EAST);
    textArea = new JTextArea();
    textArea.setText("test test\netc");
    JScrollPane textAreaScrollPane = new JScrollPane(textArea);
    editor.add(textAreaScrollPane, BorderLayout.CENTER);

    JButton renderButton = new JButton("render");
    editor.add(renderButton, BorderLayout.SOUTH);
    renderButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        generate();
      }
    });

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, renderer, editor);
    splitPane.setResizeWeight(0.75);
    add(splitPane, BorderLayout.CENTER);
  }

  private void generate() {
    if (!textArea.getText().equals(lastContents)) {
      // attempt to load a script.

      FnScript script;
      try {
        script = new ScriptLoader().loadScript(textArea.getText());
      } catch (ScriptException ex) {
        // todo: show message
        ex.printStackTrace();
        return;
      }

      lastContents = textArea.getText();
      generator = new ScriptedGenerator(script);
    }

    // generator should not be null
    List<Point3d> points = generatePoints(generator);
    if(points != null) {
      renderer.setPoints(points);
    }

  }

  private static final int ATTEMPTS = 10;

  private static final int ITERATIONS = 50000;
  private static final int FLUSH = 10000;

  private static List<Point3d> generatePoints(ScriptedGenerator generator) {
    for (int i = 0; i < ATTEMPTS; i++) {
      ScriptedFn fn = generator.generate();
      if (fn == null) {
        continue;
      }
      List<Point3d> points = Point3d.normalize(fn.iterate(Point3d.ZERO, ITERATIONS, FLUSH));
      if(!fn.isBounded(points.get(points.size()-1))) {
        continue;
      }

      double dimension = new Octree(points,10).fractalDimension();
      if (dimension < 1.0) {
        continue;
      }

      System.out.println("lyapunov:  " + fn.calculateLyapunov(Point3d.ZERO));
      System.out.println("params: " + fn.getParameters());
      System.out.println("dimension: " + dimension);
      return points;
    }

    System.out.println("failed to find function!");
    return null;
  }

  public static void main(String args[]) {

    JFrame frame = new JFrame("meef");
    frame.add(new ScriptedDisplay());
    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}
