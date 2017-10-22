package com.airbnb.epoxy.integrationtest;

import android.widget.TextView;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EpoxyModelClass
public abstract class ModelWithConstructors extends EpoxyModel<TextView> {
  @EpoxyAttribute public int value;

  public ModelWithConstructors(long id, int value) {
    super(id);
    this.value = value;
  }

  public ModelWithConstructors(int value) {
    this.value = value;
  }

  public ModelWithConstructors(long id) {
    super(id);
  }

  // Tests that kotlin extension functions
  public ModelWithConstructors(
      Collection<String> collection,
      Iterable<String> iterable,
      List<String> list,
      List<? extends String> upperBoundList,
//      List<? super String> lowerBoundList,!!! Doesn't work, we would need to transform this
// type to
// a MutableList
      List<?> wildCardList,
      Map<String, String> map,
      Set<String> set,
      Boolean boxedBoolean,
      Byte boxedByte,
      Short boxedShort,
      Integer boxedInteger,
      Long boxedLong,
      Character boxedCharacter,
      Float boxedFloat,
      Double boxedDouble,
      String[] stringArray,
      byte[] byteArray,
      short[] shortArray,
      char[] charArray,
      int[] intArray,
      float[] floatArray,
      double[] doubleArray,
      long[] longArray,
      boolean[] booleanArray,
      Byte[] boxedByteArray,
      Short[] boxedShortArray,
      Character[] boxedCharArray,
      Integer[] boxedIntArray,
      Float[] boxedFloatArray,
      Double[] boxedDoubleArray,
      Long[] boxedLongArray,
      Boolean[] boxedBooleanArray,
      Object objectParam
  ) {

  }

  @Override
  protected int getDefaultLayout() {
    return R.layout.model_with_click_listener;
  }
}
