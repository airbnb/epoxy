package com.airbnb.epoxy;

public class GenerateDefaultLayoutMethodNextParentLayout {

  @EpoxyModelClass
  public static abstract class NoLayout extends WithLayout {
    @EpoxyAttribute int value;
  }

  @EpoxyModelClass
  public static abstract class StillNoLayout extends WithLayout {

  }

  @EpoxyModelClass(layout = 1)
  public static abstract class WithLayout extends EpoxyModel<Object> {

  }
}