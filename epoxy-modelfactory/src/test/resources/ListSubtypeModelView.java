package com.airbnb.epoxy;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;
import java.util.ArrayList;

@ModelView(defaultLayout = 1)
public class ListSubtypeModelView extends FrameLayout {

  public ListSubtypeModelView(Context context) {
    super(context);
  }

  @ModelProp
  public void setStringArrayList(ArrayList<String> value) {

  }
}
