package com.airbnb.epoxy.sample.models;

import android.graphics.Color;

import com.facebook.litho.Column;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.ComponentLayout;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.annotations.ResType;
import com.facebook.litho.widget.Text;

import static com.facebook.yoga.YogaEdge.ALL;

/**
 * This is an example of the optional litho integration. A ListItemModel_ class will be
 * automatically generated for this component. Any props declared in the layout will be included on
 * the model.
 */
@LayoutSpec
public class ListItemSpec {

  @OnCreateLayout
  static ComponentLayout onCreateLayout(
      ComponentContext c,
      @Prop(optional = true) int color,
      @Prop(optional = true, resType = ResType.STRING) String title,
      @Prop String subtitle) {

    return Column.create(c)
        .paddingDip(ALL, 16)
        .backgroundColor(color)
        .child(
            Text.create(c)
                .text(title)
                .textSizeSp(40))
        .child(
            Text.create(c)
                .text(subtitle)
                .textSizeSp(20))
        .build();
  }

  void exampleUsage() {
    new ListItemModel_()
        .id(1)
        .color(Color.RED)
        .subtitle("hello world");
  }
}
