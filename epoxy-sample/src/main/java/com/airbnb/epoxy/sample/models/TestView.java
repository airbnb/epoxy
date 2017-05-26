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
@ModelView(defaultLayout = R.layout.model_header, baseModelClass = BaseTestModel.class)
public class TestView extends BaseView {
  static final String DEFAULT_TITLE = "hello world!";

  public TestView(Context context) {
    super(context);
  }

  @ModelProp(options = Option.GenerateStringOverloads)
  public void setTitle(CharSequence text) {
  }

  /** Sets a description */
  @ModelProp(defaultValue = "DEFAULT_TITLE")
  public void setDescription(CharSequence text) {
  }

  /** Sets a subtitle */
  @ModelProp(options = Option.GenerateStringOverloads)
  public void setSubtitle(@Nullable CharSequence text) {
  }

  @ModelProp(group = "image", options = Option.ResetWithNull)
  public void setImageUrl(@Nullable CharSequence url) {
  }

  @ModelProp(group = "image")
  public void setImage(@DrawableRes int imageRes) {
  }

  @ModelProp(options = Option.DoNotHash)
  public void setListener(@Nullable OnClickListener listener) {

  }

  @ResetView
  public void clear() {

  }
}
