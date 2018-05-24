package com.airbnb.epoxy;

import android.content.Context;
import androidx.annotation.Nullable;
import android.view.View;

@ModelView(defaultLayout = 1)
public class TextPropDefaultView extends View {

  public TextPropDefaultView(Context context) {
    super(context);
  }

  @TextProp(defaultRes = R.string.string_resource_value)
  public void textWithDefault(CharSequence title) {

  }

  @TextProp(defaultRes = R.string.string_resource_value)
  public void nullableTextWithDefault(@Nullable CharSequence title) {

  }
}
