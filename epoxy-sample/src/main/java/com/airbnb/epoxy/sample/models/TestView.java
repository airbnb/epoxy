package com.airbnb.epoxy.sample.models;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;

import com.airbnb.epoxy.ModelProp;
import com.airbnb.epoxy.ModelProp.Option;
import com.airbnb.epoxy.ModelView;
import com.airbnb.epoxy.R;
import com.airbnb.epoxy.ResetView;

// TODO: (eli_hart 5/22/17) delete this before public release
@ModelView(defaultLayout = R.layout.model_header)
public class TestView extends BaseView {
  static final String DEFAULT_TITLE = "hello world!";

  public TestView(Context context) {
    super(context);
  }

  @ModelProp(options = Option.GenerateStringOverloads)
  public void setTitle(CharSequence tex) {
  }

  /** Sets a description. */
  @ModelProp(defaultValue = "DEFAULT_TITLE")
  public void setDescription(String text) {
  }

  /** Sets a subtitle */
  @ModelProp
  public void setSubtitle(@Nullable String text) {
  }

  @ModelProp(group = "image", options = Option.ResetWithNull)
  public void setImageUrl(@Nullable String url) {
  }

  @ModelProp(group = "image")
  public void setImage(@DrawableRes int imageRes) {
  }

  @ResetView
  public void clear() {

  }
}
