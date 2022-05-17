package com.airbnb.epoxy;

import android.content.Context;
import android.widget.FrameLayout;

@ModelView(defaultLayout = 1)
public class SourceView extends FrameLayout {

  public SourceView(Context context) {
    super(context);
  }

  @SuppressWarnings("unused")
  @ModelProp String sectionId;
}