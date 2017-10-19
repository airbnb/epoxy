package com.airbnb.epoxy;

public class InsertedModel extends TestModel {
  public static final InsertedModel INSTANCE = new InsertedModel();

  @Override
  public int getDefaultLayout() {
    return 0;
  }
}
