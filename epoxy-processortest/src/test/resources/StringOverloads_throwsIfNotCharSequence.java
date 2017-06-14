package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class StringOverloads_throwsIfNotCharSequence extends View {

  public StringOverloads_throwsIfNotCharSequence(Context context) {
    super(context);
  }

  @ModelProp(options = ModelProp.Option.GenerateStringOverloads)
  public void setTitle(String title) {

  }
}