package com.airbnb.epoxy;

import android.content.Context;
import androidx.annotation.Nullable;
import android.view.View;

import java.util.List;

@ModelView(defaultLayout = 1)
public class TestStringOverloadsView extends View {

  public TestStringOverloadsView(Context context) {
    super(context);
  }

  @ModelProp(options = ModelProp.Option.GenerateStringOverloads)
  public void setTitle(CharSequence title) {

  }

  @ModelProp
  public void setTitle(@Nullable List<CharSequence> title) {
    // testing that a nullable overload works correctly
    // test setting options via the value param shortcut
  }

  @ModelProp(ModelProp.Option.GenerateStringOverloads)
  public void setTitleViaValueShortcut(CharSequence title) {
     // test setting options via the value param shortcut
  }
}
