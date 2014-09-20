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
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.base.Joiner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.script.ScriptException;


/**
 *
 * @author ashmore
 */
public class CommandLineRenderSaver {
  @Parameter
  private List<String> globalArgs = new ArrayList<>();

  private final LargeRenderSaver saver;

  CommandLineRenderSaver(String[] args) throws IOException {

    RenderSaverParameters params = new RenderSaverParameters();
    JCommander commandLine = new JCommander();
    commandLine.addObject(this);
    commandLine.addObject(params);
    commandLine.parse(args);

    System.out.println("Rendering: "+globalArgs.get(0));

    AttractorFunction<Point3d,ArrayParams> fn = readFn(globalArgs.get(0));
      String outFilename = globalArgs.get(0) + ".obj";
      Tesselator.ProgressListener progressListener = new Tesselator.ProgressListener() {
        @Override
        public void progress(int line, int totalLines, int triangles) {
          // do nothing
        }
      };
      LargeRenderSaver.Logger logger = new LargeRenderSaver.Logger() {
        @Override
        public void setText(String text) {
          System.out.println(text);
        }
      };
      saver = new LargeRenderSaver(
              progressListener, logger, new File(outFilename), fn, params);
  }

  void render() {
    saver.startBlocking();
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
