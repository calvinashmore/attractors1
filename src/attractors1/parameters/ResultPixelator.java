/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.parameters;

import attractors1.math.AttractorResult;
import attractors1.math.Linear;
import java.awt.Color;

/**
 * Turns AttractorResults into Colors
 */
public interface ResultPixelator<T extends Linear<T>, P extends Linear<P>> {

  /**
   * @param result nullable input.
   */
  Color pixelate(AttractorResult<T, P> result);
}
