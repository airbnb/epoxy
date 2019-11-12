package com.airbnb.epoxy;

public class GenerateDefaultLayoutMethodParentStillNoLayout {

  @EpoxyModelClass
  public static abstract class NoLayout extends StillNoLayout {
    @EpoxyAttribute int value;
  }

  @EpoxyModelClass
  public static abstract class StillNoLayout extends EpoxyModel<Object> {

  }
}