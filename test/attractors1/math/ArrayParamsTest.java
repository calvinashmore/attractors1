/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math;

import java.util.Random;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author ashmore
 */
public class ArrayParamsTest {

  @Test
  public void testParse() {
    ArrayParams params = ArrayParams.newParams(10, new Random(123));

    String paramString = params.toString();
    ArrayParams parsedParams = ArrayParams.parse(paramString);

    Assert.assertEquals(params, parsedParams);
  }
}
