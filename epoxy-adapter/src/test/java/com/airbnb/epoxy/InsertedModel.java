package com.airbnb.epoxy;

class InsertedModel extends TestModel {
  static final InsertedModel INSTANCE = new InsertedModel();

  @Override
  public int getDefaultLayout() {
    return 0;
  }
}
