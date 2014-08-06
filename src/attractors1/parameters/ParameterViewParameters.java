/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.parameters;

/**
 *
 * @author ashmore
 */
public class ParameterViewParameters {
  final double minXParam;
  final double maxXParam;
  final double minYParam;
  final double maxYParam;

  final int indexXParam;
  final int indexYParam;

  public ParameterViewParameters() {
    this(-1,1, -1,1, 0,1);
  }

  public ParameterViewParameters(double minXParam, double maxXParam, double minYParam, double maxYParam, int indexXParam, int indexYParam) {
    this.minXParam = minXParam;
    this.maxXParam = maxXParam;
    this.minYParam = minYParam;
    this.maxYParam = maxYParam;
    this.indexXParam = indexXParam;
    this.indexYParam = indexYParam;

    if(minXParam >= maxXParam)
      throw new IllegalArgumentException();
    if(minYParam >= maxYParam)
      throw new IllegalArgumentException();
    if(indexXParam == indexYParam)
      throw new IllegalArgumentException();
    if(indexXParam < 0 || indexYParam < 0)
      throw new IllegalArgumentException();
  }
}
