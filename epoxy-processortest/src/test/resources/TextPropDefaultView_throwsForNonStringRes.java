package com.airbnb.epoxy;

import android.content.Context;
import androidx.annotation.Nullable;
import android.view.View;

@ModelView(defaultLayout = 1)
public class TextPropDefaultView_throwsForNonStringRes extends View {

  public TextPropDefaultView_throwsForNonStringRes(Context context) {
    super(context);
  }

  @TextProp(defaultRes = R.layout.res)
  public void textWithDefault(CharSequence title) {

  }
}
