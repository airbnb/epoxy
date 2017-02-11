package com.airbnb.epoxy;

public class GenerateDefaultLayoutMethodParentLayout {

  @EpoxyModelClass
  public static abstract class NoLayout extends WithLayout {
    @EpoxyAttribute int value;
  }

  @EpoxyModelClass(layout = 1)
  public static abstract class WithLayout extends EpoxyModel<Object> {

  }
}