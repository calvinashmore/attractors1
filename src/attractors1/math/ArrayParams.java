/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package attractors1.math;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 *
 * @author ashmore
 */
public class ArrayParams extends Linear<ArrayParams> {

  private final double[] data;

  public ArrayParams(double[] data) {
    this.data = Arrays.copyOf(data, data.length);
  }

  public int size() {
    return data.length;
  }

  @Override
  public ArrayParams add(ArrayParams other) {
    assert(this.size() == other.size());
    double[] newData = new double[size()];
    for(int i=0;i<size();i++) {
      newData[i] = this.data[i] + other.data[i];
    }
    return new ArrayParams(newData);
  }

  @Override
  public ArrayParams multiply(double other) {
    double[] newData = new double[size()];
    for(int i=0;i<size();i++) {
      newData[i] = this.data[i] * other;
    }
    return new ArrayParams(newData);
  }

  @Override
  public double norm() {
    double norm = 0;
    // L0 norm
    for(int i=0;i<size();i++){
      norm += Math.abs(data[i]);
    }
    return norm;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ArrayParams) {
      ArrayParams that = (ArrayParams) obj;
      return Arrays.equals(this.data, that.data);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(data);
  }

  @Override
  public String toString() {
    return Arrays.toString(data);
  }

  public static ArrayParams newParams(int size, Random random) {
    double data[] = new double[size];
    for(int i=0;i<size;i++) {
      data[i] = random.nextDouble() * 2 - 1;
    }
    return new ArrayParams(data);
  }

  // exposes internals, which is bad
  public double[] getData() {
    return data;
  }
}
