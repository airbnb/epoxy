package com.airbnb.epoxy;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.view.View;
import java.lang.Boolean;
import java.lang.CharSequence;
import java.lang.IllegalArgumentException;
import java.lang.IllegalStateException;
import java.lang.Integer;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/**
 * Generated file. Do not modify! */
public class TestManyTypesViewModel_ extends EpoxyModel<TestManyTypesView> implements GeneratedModel<TestManyTypesView> {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(9);

  private OnModelBoundListener<TestManyTypesViewModel_, TestManyTypesView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<TestManyTypesViewModel_, TestManyTypesView> onModelUnboundListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0 */
  private String stringValue_String;

  /**
   * Bitset index: 1 */
  private int intValue_Int;

  /**
   * Bitset index: 2 */
  private Integer integerValue_Integer;

  /**
   * Bitset index: 3 */
  private boolean boolValue_Boolean;

  /**
   * Bitset index: 4 */
  private Boolean booleanValue_Boolean;

  /**
   * Bitset index: 5 */
  private String[] arrayValue_StringArray;

  /**
   * Bitset index: 6 */
  private List<String> listValue_List;

  /**
   * Bitset index: 7 */
  private View.OnClickListener clickListener_OnClickListener;

  private OnModelClickListener<TestManyTypesViewModel_, TestManyTypesView> clickListener_OnClickListener_epoxyGeneratedModel;

  /**
   * Bitset index: 8 */
  private StringAttributeData title_StringAttributeData =  new StringAttributeData(null);

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(0)) {
    	throw new IllegalStateException("A value is required for setStringValue");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(2)) {
    	throw new IllegalStateException("A value is required for setIntegerValue");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(5)) {
    	throw new IllegalStateException("A value is required for setArrayValue");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(6)) {
    	throw new IllegalStateException("A value is required for setListValue");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(1)) {
    	throw new IllegalStateException("A value is required for setIntValue");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(7)) {
    	throw new IllegalStateException("A value is required for setClickListener");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(4)) {
    	throw new IllegalStateException("A value is required for setBooleanValue");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(3)) {
    	throw new IllegalStateException("A value is required for setBoolValue");
    }
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final TestManyTypesView object,
      int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
    if (clickListener_OnClickListener_epoxyGeneratedModel != null) {
      this.clickListener_OnClickListener = new WrappedEpoxyModelClickListener(clickListener_OnClickListener_epoxyGeneratedModel) {
              @Override
              protected void wrappedOnClick(View v, OnModelClickListener originalClickListener) {
                 originalClickListener.onClick(com.airbnb.epoxy.TestManyTypesViewModel_.this, object, v,
                        holder.getAdapterPosition());
                 }
              };
    }
  }

  @Override
  public void bind(final TestManyTypesView object) {
    super.bind(object);
    object.setStringValue(stringValue_String);
    object.setIntegerValue(integerValue_Integer);
    object.setArrayValue(arrayValue_StringArray);
    object.setListValue(listValue_List);
    object.setIntValue(intValue_Int);
    object.setClickListener(clickListener_OnClickListener);
    object.setBooleanValue(booleanValue_Boolean);
    object.setTitle(title_StringAttributeData.toString(object.getContext()));
    object.setBoolValue(boolValue_Boolean);
  }

  @Override
  public void bind(final TestManyTypesView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof TestManyTypesViewModel_)) {
      bind(object);
      return;
    }
    TestManyTypesViewModel_ that = (TestManyTypesViewModel_) previousModel;

    if (stringValue_String != null ? !stringValue_String.equals(that.stringValue_String) : that.stringValue_String != null) {
      object.setStringValue(stringValue_String);
    }

    if (integerValue_Integer != null ? !integerValue_Integer.equals(that.integerValue_Integer) : that.integerValue_Integer != null) {
      object.setIntegerValue(integerValue_Integer);
    }

    if (!Arrays.equals(arrayValue_StringArray, that.arrayValue_StringArray)) {
      object.setArrayValue(arrayValue_StringArray);
    }

    if (listValue_List != null ? !listValue_List.equals(that.listValue_List) : that.listValue_List != null) {
      object.setListValue(listValue_List);
    }

    if (intValue_Int != that.intValue_Int) {
      object.setIntValue(intValue_Int);
    }

    if ((clickListener_OnClickListener == null) != (that.clickListener_OnClickListener == null)) {
      object.setClickListener(clickListener_OnClickListener);
    }

    if (booleanValue_Boolean != null ? !booleanValue_Boolean.equals(that.booleanValue_Boolean) : that.booleanValue_Boolean != null) {
      object.setBooleanValue(booleanValue_Boolean);
    }

    if (!title_StringAttributeData.equals(that.title_StringAttributeData)) {
      object.setTitle(title_StringAttributeData.toString(object.getContext()));
    }

    if (boolValue_Boolean != that.boolValue_Boolean) {
      object.setBoolValue(boolValue_Boolean);
    }
  }

  @Override
  public void handlePostBind(final TestManyTypesView object, int position) {
    if (onModelBoundListener_epoxyGeneratedModel != null) {
      onModelBoundListener_epoxyGeneratedModel.onModelBound(this, object, position);
    }
    validateStateHasNotChangedSinceAdded("The model was changed during the bind call.", position);
  }

  /**
   * Register a listener that will be called when this model is bound to a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public TestManyTypesViewModel_ onBind(OnModelBoundListener<TestManyTypesViewModel_, TestManyTypesView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(TestManyTypesView object) {
    super.unbind(object);
    if (onModelUnboundListener_epoxyGeneratedModel != null) {
      onModelUnboundListener_epoxyGeneratedModel.onModelUnbound(this, object);
    }
  }

  /**
   * Register a listener that will be called when this model is unbound from a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public TestManyTypesViewModel_ onUnbind(OnModelUnboundListener<TestManyTypesViewModel_, TestManyTypesView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Required.</i>
   *
   * @see TestManyTypesView#setStringValue(String)
   */
  public TestManyTypesViewModel_ stringValue(String stringValue) {
    if (stringValue == null) {
      throw new IllegalArgumentException("stringValue cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(0);
    onMutation();
    this.stringValue_String = stringValue;
    return this;
  }

  public String stringValue() {
    return stringValue_String;
  }

  /**
   * <i>Required.</i>
   *
   * @see TestManyTypesView#setIntValue(int)
   */
  public TestManyTypesViewModel_ intValue(int intValue) {
    assignedAttributes_epoxyGeneratedModel.set(1);
    onMutation();
    this.intValue_Int = intValue;
    return this;
  }

  public int intValue() {
    return intValue_Int;
  }

  /**
   * <i>Required.</i>
   *
   * @see TestManyTypesView#setIntegerValue(Integer)
   */
  public TestManyTypesViewModel_ integerValue(Integer integerValue) {
    if (integerValue == null) {
      throw new IllegalArgumentException("integerValue cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(2);
    onMutation();
    this.integerValue_Integer = integerValue;
    return this;
  }

  public Integer integerValue() {
    return integerValue_Integer;
  }

  /**
   * <i>Required.</i>
   *
   * @see TestManyTypesView#setBoolValue(boolean)
   */
  public TestManyTypesViewModel_ boolValue(boolean boolValue) {
    assignedAttributes_epoxyGeneratedModel.set(3);
    onMutation();
    this.boolValue_Boolean = boolValue;
    return this;
  }

  public boolean boolValue() {
    return boolValue_Boolean;
  }

  /**
   * <i>Required.</i>
   *
   * @see TestManyTypesView#setBooleanValue(Boolean)
   */
  public TestManyTypesViewModel_ booleanValue(Boolean booleanValue) {
    if (booleanValue == null) {
      throw new IllegalArgumentException("booleanValue cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(4);
    onMutation();
    this.booleanValue_Boolean = booleanValue;
    return this;
  }

  public Boolean booleanValue() {
    return booleanValue_Boolean;
  }

  /**
   * <i>Required.</i>
   *
   * @see TestManyTypesView#setArrayValue(String[])
   */
  public TestManyTypesViewModel_ arrayValue(String[] arrayValue) {
    if (arrayValue == null) {
      throw new IllegalArgumentException("arrayValue cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(5);
    onMutation();
    this.arrayValue_StringArray = arrayValue;
    return this;
  }

  public String[] arrayValue() {
    return arrayValue_StringArray;
  }

  /**
   * <i>Required.</i>
   *
   * @see TestManyTypesView#setListValue(List<String>)
   */
  public TestManyTypesViewModel_ listValue(List<String> listValue) {
    if (listValue == null) {
      throw new IllegalArgumentException("listValue cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(6);
    onMutation();
    this.listValue_List = listValue;
    return this;
  }

  public List<String> listValue() {
    return listValue_List;
  }

  /**
   * Set a click listener that will provide the parent view, model, and adapter position of the clicked view. This will clear the normal View.OnClickListener if one has been set */
  public TestManyTypesViewModel_ clickListener_OnClickListener(final OnModelClickListener<TestManyTypesViewModel_, TestManyTypesView> clickListener_OnClickListener) {
    assignedAttributes_epoxyGeneratedModel.set(7);
    onMutation();
    this.clickListener_OnClickListener_epoxyGeneratedModel = clickListener_OnClickListener;
    if (clickListener_OnClickListener == null) {
      this.clickListener_OnClickListener = null;
    }
    else {
      this.clickListener_OnClickListener = new WrappedEpoxyModelClickListener(clickListener_OnClickListener)  {
                  @Override
                  protected void wrappedOnClick(View v, OnModelClickListener originalClickListener) {
                    
                  }
                };
    }
    return this;
  }

  /**
   * <i>Required.</i>
   *
   * @see TestManyTypesView#setClickListener(View.OnClickListener)
   */
  public TestManyTypesViewModel_ clickListener(View.OnClickListener clickListener) {
    if (clickListener == null) {
      throw new IllegalArgumentException("clickListener cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(7);
    onMutation();
    this.clickListener_OnClickListener = clickListener;
    this.clickListener_OnClickListener_epoxyGeneratedModel = null;
    return this;
  }

  public View.OnClickListener clickListener() {
    return clickListener_OnClickListener;
  }

  @Nullable
  public CharSequence getTitle(Context context) {
    return title_StringAttributeData.toString(context);
  }

  /**
   * <i>Optional</i>: Default value is null
   *
   * @see TestManyTypesView#setTitle(CharSequence)
   */
  public TestManyTypesViewModel_ title(@Nullable CharSequence title) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(8);
    title_StringAttributeData.setValue(title);
    return this;
  }

  /**
   * <i>Optional</i>: Default value is null
   *
   * @see TestManyTypesView#setTitle(CharSequence)
   */
  public TestManyTypesViewModel_ title(@StringRes int stringRes) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(8);
    title_StringAttributeData.setValue(stringRes);
    return this;
  }

  /**
   * <i>Optional</i>: Default value is null
   *
   * @see TestManyTypesView#setTitle(CharSequence)
   */
  public TestManyTypesViewModel_ title(@StringRes int stringRes, Object... formatArgs) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(8);
    title_StringAttributeData.setValue(stringRes, formatArgs);
    return this;
  }

  /**
   * <i>Optional</i>: Default value is null
   *
   * @see TestManyTypesView#setTitle(CharSequence)
   */
  public TestManyTypesViewModel_ titleQuantityRes(@PluralsRes int pluralRes, int quantity,
      Object... formatArgs) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(8);
    title_StringAttributeData.setValue(pluralRes, quantity, formatArgs);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ id(Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ spanSizeCallback(@Nullable EpoxyModel.SpanSizeCallback arg0) {
    super.spanSizeCallback(arg0);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public TestManyTypesViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public TestManyTypesViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.stringValue_String = null;
    this.intValue_Int = 0;
    this.integerValue_Integer = null;
    this.boolValue_Boolean = false;
    this.booleanValue_Boolean = null;
    this.arrayValue_StringArray = null;
    this.listValue_List = null;
    this.clickListener_OnClickListener = null;
    clickListener_OnClickListener_epoxyGeneratedModel = null;
    this.title_StringAttributeData =  new StringAttributeData(null);
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof TestManyTypesViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    TestManyTypesViewModel_ that = (TestManyTypesViewModel_) o;
    if ((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if ((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if (stringValue_String != null ? !stringValue_String.equals(that.stringValue_String) : that.stringValue_String != null) {
      return false;
    }
    if (intValue_Int != that.intValue_Int) {
      return false;
    }
    if (integerValue_Integer != null ? !integerValue_Integer.equals(that.integerValue_Integer) : that.integerValue_Integer != null) {
      return false;
    }
    if (boolValue_Boolean != that.boolValue_Boolean) {
      return false;
    }
    if (booleanValue_Boolean != null ? !booleanValue_Boolean.equals(that.booleanValue_Boolean) : that.booleanValue_Boolean != null) {
      return false;
    }
    if (!Arrays.equals(arrayValue_StringArray, that.arrayValue_StringArray)) {
      return false;
    }
    if (listValue_List != null ? !listValue_List.equals(that.listValue_List) : that.listValue_List != null) {
      return false;
    }
    if ((clickListener_OnClickListener == null) != (that.clickListener_OnClickListener == null)) {
      return false;
    }
    if (title_StringAttributeData != null ? !title_StringAttributeData.equals(that.title_StringAttributeData) : that.title_StringAttributeData != null) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (stringValue_String != null ? stringValue_String.hashCode() : 0);
    result = 31 * result + intValue_Int;
    result = 31 * result + (integerValue_Integer != null ? integerValue_Integer.hashCode() : 0);
    result = 31 * result + (boolValue_Boolean ? 1 : 0);
    result = 31 * result + (booleanValue_Boolean != null ? booleanValue_Boolean.hashCode() : 0);
    result = 31 * result + Arrays.hashCode(arrayValue_StringArray);
    result = 31 * result + (listValue_List != null ? listValue_List.hashCode() : 0);
    result = 31 * result + (clickListener_OnClickListener != null ? 1 : 0);
    result = 31 * result + (title_StringAttributeData != null ? title_StringAttributeData.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TestManyTypesViewModel_{" +
        "stringValue_String=" + stringValue_String +
        ", intValue_Int=" + intValue_Int +
        ", integerValue_Integer=" + integerValue_Integer +
        ", boolValue_Boolean=" + boolValue_Boolean +
        ", booleanValue_Boolean=" + booleanValue_Boolean +
        ", arrayValue_StringArray=" + arrayValue_StringArray +
        ", listValue_List=" + listValue_List +
        ", clickListener_OnClickListener=" + clickListener_OnClickListener +
        ", title_StringAttributeData=" + title_StringAttributeData +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}