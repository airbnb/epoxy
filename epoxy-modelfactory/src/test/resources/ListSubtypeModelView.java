package com.airbnb.epoxy;

import android.content.Context;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
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
