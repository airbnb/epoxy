package com.airbnb.epoxy;

public class ModelWithType<T extends String> extends EpoxyModel<Object> {

  @EpoxyAttribute int value;

  @Override
  protected int getDefaultLayout() {
    return 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ModelWithType_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    ModelWithType that = (ModelWithType) o;

    return value == that.value;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + value;
    return result;
  }
}