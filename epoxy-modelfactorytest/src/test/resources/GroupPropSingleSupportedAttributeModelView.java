package com.airbnb.epoxy;

import android.content.Context;
import android.widget.FrameLayout;

import com.airbnb.epoxy.ModelProp.Option;

@ModelView(defaultLayout = 1)
public class GroupPropSingleSupportedAttributeModelView extends FrameLayout {

  public GroupPropSingleSupportedAttributeModelView(Context context) {
    super(context);
  }

  @ModelProp
  public void setTitle(String title) {

  }

  @ModelProp({ Option.IgnoreRequireHashCode })
  public void setTitle(Object title) {

  }
}