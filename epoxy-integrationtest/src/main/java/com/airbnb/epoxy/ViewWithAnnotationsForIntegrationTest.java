package com.airbnb.epoxy;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.airbnb.epoxy.ModelProp.Option;

@ModelView(defaultLayout = R.layout.view_with_annotations_for_integration_test)
public class ViewWithAnnotationsForIntegrationTest extends View {
  public CharSequence text;

  public ViewWithAnnotationsForIntegrationTest(Context context) {
    super(context);
  }

  public ViewWithAnnotationsForIntegrationTest(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public ViewWithAnnotationsForIntegrationTest(Context context,
      @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @ModelProp(options = Option.GenerateStringOverloads)
  public void setText(CharSequence text) {
    this.text = text;
  }
}
