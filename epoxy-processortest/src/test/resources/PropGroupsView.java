package com.airbnb.epoxy;

import android.content.Context;
import androidx.annotation.Nullable;
import android.view.View;

@ModelView(defaultLayout = 1)
public class PropGroupsView extends View {
  static final long DEFAULT_PRIMITIVE = 234;

  public PropGroupsView(Context context) {
    super(context);
  }

  @ModelProp
  public void setSomething(@Nullable CharSequence title) {
    // The default value for this group should be to set null on this prop, since that is clearer
    // than setting 0 on the primitive prop
  }

  @ModelProp
  public void setSomething(int title) {
    // Implicit grouping by having the same method name
  }

  @ModelProp
  public void setSomethingElse(CharSequence title) {

  }

  @ModelProp
  public void setSomethingElse(int title) {
    // Implicit grouping by having the same method name
  }

  @ModelProp
  public void setPrimitive(int title) {
  }

  @ModelProp
  public void setPrimitive(long title) {
    // Implicit grouping by having the same method name
    // This should be optional, with the default value being either primitive prop (it is
    // undefined which is used)
  }

  @ModelProp
  public void setPrimitiveWithDefault(int title) {
  }

  @ModelProp(defaultValue = "DEFAULT_PRIMITIVE")
  public void setPrimitiveWithDefault(long title) {
    // Implicit grouping by having the same method name
    // This should be optional, with the specified default
  }

  @ModelProp(defaultValue = "DEFAULT_PRIMITIVE")
  public void primitiveAndObjectGroupWithPrimitiveDefault(long title) {
  }

  @ModelProp
  public void primitiveAndObjectGroupWithPrimitiveDefault(CharSequence title) {

  }

  @ModelProp(group = "myGroup")
  public void setOneThing(long title) {
  }

  @ModelProp(group = "myGroup")
  public void setAnotherThing(CharSequence title) {
    // should be in same group because of group key
  }

  @ModelProp
  public void requiredGroup(String title) {
  }

  @ModelProp
  public void requiredGroup(CharSequence title) {
  }
}
