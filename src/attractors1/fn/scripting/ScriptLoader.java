/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.fn.scripting;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author ashmore
 */
public class ScriptLoader {

  public ScriptLoader() {
  }

  public FnScript loadScript(String contents) throws ScriptException {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("jython");
    engine.eval(contents);

    Invocable invocable = (Invocable) engine;
    return invocable.getInterface(FnScript.class);
  }
}
