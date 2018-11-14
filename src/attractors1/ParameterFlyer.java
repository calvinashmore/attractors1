/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1;

import attractors1.fn.scripting.ScriptedGenerator;
import attractors1.math.ArrayParams;
import attractors1.math.SplineEval;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author ashmore
 */
public class ParameterFlyer {
  private static final int ATTEMPTS = 100;
  private static final int ITERATIONS = 5000;
  private static final int FLUSH = 1000;
  
  private static final int INTERPOLATION_QUEUE_SIZE = 10;
  private static final double INCREMENT = 0.01;
  private static final long SLEEP_TIME = 10;
  
  private final Renderer renderer;
  private final ScriptedGenerator generator;
  private final ResultFinder resultFinder = new ResultFinder(ITERATIONS, FLUSH, ATTEMPTS);
  private final ArrayDeque<ArrayParams> interpolationQueue = new ArrayDeque(INTERPOLATION_QUEUE_SIZE);
  private final ExecutorService executor = new ScheduledThreadPoolExecutor(2);
  
  private ArrayParams currentParams;
  private double t = 0;
  private boolean stop = false;
  
  ParameterFlyer(Renderer renderer, ScriptedGenerator generator) {
    this.renderer = renderer;
    this.generator = generator;
  }
  
  void start() {
    executor.submit(this::run);
  }
  
  void stop() {
    stop = true;
  }
  
  private void run() {
    System.out.println("starting");
    if(!populateQueue()) {
      System.out.println("failed to populate");
      return;
    }
    System.out.println("flying...");
    
    double speedBoost = 1;
    
    try {
      while(!stop) {
        System.out.println("..."+t);
        t += INCREMENT*speedBoost;
        if (t > 1) {
          t -= 1;
          interpolationQueue.pop();
        }
        
        if (interpolationQueue.size() < INTERPOLATION_QUEUE_SIZE) {
          System.out.println("populating");
          executor.submit(this::populateQueue);
        }
        
        Iterator<ArrayParams> iter = interpolationQueue.iterator();
        currentParams = SplineEval.eval(iter.next(), iter.next(), iter.next(), iter.next(), t);
        
        GenerationResult result;
        try {
          result = resultFinder.generatePoints(generator, currentParams);
        } catch (IllegalArgumentException ex) {
          continue;
        }
        
        
        if (result.getResult().getDimension() < .1) {
          if (speedBoost < 100) {
            speedBoost += .1;
          }
        } else if (result.getResult().getDimension() > .8) {
          speedBoost = 1;
        }
        
        
        renderer.setPoints(result.getPoints());
        
        Thread.sleep(SLEEP_TIME);
      }
    } catch (InterruptedException ex) {
      // return
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    System.out.println("DONE");
  }
  
  private boolean populateQueue() {
    System.out.println("populating queue");
    ArrayParams params;
    
    if(interpolationQueue.isEmpty()) {
      GenerationResult result = resultFinder.generatePoints(generator);
      if (result == null) return false;
      params = result.getParams();
      interpolationQueue.add(params);
    } else {
      params = interpolationQueue.getLast();
    }
    
    while (interpolationQueue.size() < INTERPOLATION_QUEUE_SIZE) {
      // TODO: change this so it gets parameters that are near params.
      GenerationResult result = resultFinder.generatePoints(generator);
      if (result == null) return false;
      params = result.getParams();
      interpolationQueue.add(params);
    }
    return true;
  }
  
  public ArrayParams getCurrentParams() {
    return currentParams;
  }
  
}
