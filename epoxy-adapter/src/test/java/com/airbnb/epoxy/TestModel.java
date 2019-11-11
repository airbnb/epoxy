package com.airbnb.epoxy;

import android.view.View;

import java.util.Random;

public class TestModel extends EpoxyModel<View> {
  private static final Random RANDOM = new Random(10);
  boolean updated;
  private int value;

  public TestModel() {
    // Uses a random id to make sure the algorithm doesn't have different behavior for
    // consecutive or varied ids
    super(RANDOM.nextLong());
    randomizeValue();
  }

  public TestModel(long id) {
    super(id);
    randomizeValue();
  }

  TestModel randomizeValue() {
    value = RANDOM.nextInt();
    return this;
  }

  @Override
  public int getDefaultLayout() {
    return 0;
  }

  TestModel value(int value) {
    this.value = value;
    return this;
  }

  TestModel incrementValue() {
    this.value++;
    return this;
  }

  int value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    TestModel testModel = (TestModel) o;

    return value == testModel.value;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + value;
    return result;
  }

  public TestModel clone() {
    TestModel clone = new TestModel()
        .value(value);

    return (TestModel) clone.id(id())
        .layout(getLayout());
  }
}
