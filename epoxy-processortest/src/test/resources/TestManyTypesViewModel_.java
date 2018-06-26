package com.airbnb.epoxy;

import android.content.Context;
import android.support.annotation.Dimension;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
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
public class TestManyTypesViewModel_ extends EpoxyModel<TestManyTypesView> implements GeneratedModel<TestManyTypesView>, TestManyTypesViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(14);

  private OnModelBoundListener<TestManyTypesViewModel_, TestManyTypesView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<TestManyTypesViewModel_, TestManyTypesView> onModelUnboundListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0 */
  @NonNull
  private String stringValue_String;

  /**
   * Bitset index: 1 */
  @Nullable
  private String nullableStringValue_String = (String) null;

  /**
   * Bitset index: 2 */
  private int intValue_Int = 0;

  /**
   * Bitset index: 3 */
  @StringRes
  private int intValueWithAnnotation_Int = 0;

  /**
   * Bitset index: 4 */
  @IntRange(
      from = 0,
      to = 200
  )
  private int intValueWithRangeAnnotation_Int = 0;

  /**
   * Bitset index: 5 */
  @Dimension(
      unit = 0
  )
  private int intValueWithDimenTypeAnnotation_Int = 0;

  /**
   * Bitset index: 6 */
  @IntRange(
      from = 0,
      to = 200
  )
  @Dimension(
      unit = 0
  )
  private int intWithMultipleAnnotations_Int = 0;

  /**
   * Bitset index: 7 */
  @NonNull
  private Integer integerValue_Integer;

  /**
   * Bitset index: 8 */
  private boolean boolValue_Boolean = false;

  /**
   * Bitset index: 9 */
  @NonNull
  private Boolean booleanValue_Boolean;

  /**
   * Bitset index: 10 */
  @NonNull
  private String[] arrayValue_StringArray;

  /**
   * Bitset index: 11 */
  @NonNull
  private List<String> listValue_List;

  /**
   * Bitset index: 12 */
  @NonNull
  private View.OnClickListener clickListener_OnClickListener;

  /**
   * Bitset index: 13 */
  private StringAttributeData title_StringAttributeData =  new StringAttributeData((CharSequence) null);

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(10)) {
    	throw new IllegalStateException("A value is required for setArrayValue");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(12)) {
    	throw new IllegalStateException("A value is required for setClickListener");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(9)) {
    	throw new IllegalStateException("A value is required for setBooleanValue");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(0)) {
    	throw new IllegalStateException("A value is required for setStringValue");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(7)) {
    	throw new IllegalStateException("A value is required for setIntegerValue");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(11)) {
    	throw new IllegalStateException("A value is required for setListValue");
    }
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final TestManyTypesView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final TestManyTypesView object) {
    super.bind(object);
    object.setArrayValue(arrayValue_StringArray);
    object.setClickListener(clickListener_OnClickListener);
    object.setBooleanValue(booleanValue_Boolean);
    object.setTitle(title_StringAttributeData.toString(object.getContext()));
    object.setStringValue(stringValue_String);
    object.setNullableStringValue(nullableStringValue_String);
    object.setIntValueWithAnnotation(intValueWithAnnotation_Int);
    object.setIntValueWithDimenTypeAnnotation(intValueWithDimenTypeAnnotation_Int);
    object.setIntWithMultipleAnnotations(intWithMultipleAnnotations_Int);
    object.setIntegerValue(integerValue_Integer);
    object.setListValue(listValue_List);
    object.setIntValue(intValue_Int);
    object.setIntValueWithRangeAnnotation(intValueWithRangeAnnotation_Int);
    object.setBoolValue(boolValue_Boolean);
  }

  @Override
  public void bind(final TestManyTypesView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof TestManyTypesViewModel_)) {
      bind(object);
      return;
    }
    TestManyTypesViewModel_ that = (TestManyTypesViewModel_) previousModel;
    super.bind(object);

    if (!Arrays.equals(arrayValue_StringArray, that.arrayValue_StringArray)) {
      object.setArrayValue(arrayValue_StringArray);
    }

    if (((clickListener_OnClickListener == null) != (that.clickListener_OnClickListener == null))) {
      object.setClickListener(clickListener_OnClickListener);
    }

    if ((booleanValue_Boolean != null ? !booleanValue_Boolean.equals(that.booleanValue_Boolean) : that.booleanValue_Boolean != null)) {
      object.setBooleanValue(booleanValue_Boolean);
    }

    if ((title_StringAttributeData != null ? !title_StringAttributeData.equals(that.title_StringAttributeData) : that.title_StringAttributeData != null)) {
      object.setTitle(title_StringAttributeData.toString(object.getContext()));
    }

    if ((stringValue_String != null ? !stringValue_String.equals(that.stringValue_String) : that.stringValue_String != null)) {
      object.setStringValue(stringValue_String);
    }

    if ((nullableStringValue_String != null ? !nullableStringValue_String.equals(that.nullableStringValue_String) : that.nullableStringValue_String != null)) {
      object.setNullableStringValue(nullableStringValue_String);
    }

    if ((intValueWithAnnotation_Int != that.intValueWithAnnotation_Int)) {
      object.setIntValueWithAnnotation(intValueWithAnnotation_Int);
    }

    if ((intValueWithDimenTypeAnnotation_Int != that.intValueWithDimenTypeAnnotation_Int)) {
      object.setIntValueWithDimenTypeAnnotation(intValueWithDimenTypeAnnotation_Int);
    }

    if ((intWithMultipleAnnotations_Int != that.intWithMultipleAnnotations_Int)) {
      object.setIntWithMultipleAnnotations(intWithMultipleAnnotations_Int);
    }

    if ((integerValue_Integer != null ? !integerValue_Integer.equals(that.integerValue_Integer) : that.integerValue_Integer != null)) {
      object.setIntegerValue(integerValue_Integer);
    }

    if ((listValue_List != null ? !listValue_List.equals(that.listValue_List) : that.listValue_List != null)) {
      object.setListValue(listValue_List);
    }

    if ((intValue_Int != that.intValue_Int)) {
      object.setIntValue(intValue_Int);
    }

    if ((intValueWithRangeAnnotation_Int != that.intValueWithRangeAnnotation_Int)) {
      object.setIntValueWithRangeAnnotation(intValueWithRangeAnnotation_Int);
    }

    if ((boolValue_Boolean != that.boolValue_Boolean)) {
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
  public TestManyTypesViewModel_ stringValue(@NonNull String stringValue) {
    if (stringValue == null) {
      throw new IllegalArgumentException("stringValue cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(0);
    onMutation();
    this.stringValue_String = stringValue;
    return this;
  }

  @NonNull
  public String stringValue() {
    return stringValue_String;
  }

  /**
   * <i>Optional</i>: Default value is (String) null
   *
   * @see TestManyTypesView#setNullableStringValue(String)
   */
  public TestManyTypesViewModel_ nullableStringValue(@Nullable String nullableStringValue) {
    assignedAttributes_epoxyGeneratedModel.set(1);
    onMutation();
    this.nullableStringValue_String = nullableStringValue;
    return this;
  }

  @Nullable
  public String nullableStringValue() {
    return nullableStringValue_String;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see TestManyTypesView#setIntValue(int)
   */
  public TestManyTypesViewModel_ intValue(int intValue) {
    assignedAttributes_epoxyGeneratedModel.set(2);
    onMutation();
    this.intValue_Int = intValue;
    return this;
  }

  public int intValue() {
    return intValue_Int;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see TestManyTypesView#setIntValueWithAnnotation(int)
   */
  public TestManyTypesViewModel_ intValueWithAnnotation(@StringRes int intValueWithAnnotation) {
    assignedAttributes_epoxyGeneratedModel.set(3);
    onMutation();
    this.intValueWithAnnotation_Int = intValueWithAnnotation;
    return this;
  }

  @StringRes
  public int intValueWithAnnotation() {
    return intValueWithAnnotation_Int;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see TestManyTypesView#setIntValueWithRangeAnnotation(int)
   */
  public TestManyTypesViewModel_ intValueWithRangeAnnotation(@IntRange(from = 0, to = 200) int intValueWithRangeAnnotation) {
    assignedAttributes_epoxyGeneratedModel.set(4);
    onMutation();
    this.intValueWithRangeAnnotation_Int = intValueWithRangeAnnotation;
    return this;
  }

  @IntRange(
      from = 0,
      to = 200
  )
  public int intValueWithRangeAnnotation() {
    return intValueWithRangeAnnotation_Int;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see TestManyTypesView#setIntValueWithDimenTypeAnnotation(int)
   */
  public TestManyTypesViewModel_ intValueWithDimenTypeAnnotation(@Dimension(unit = 0) int intValueWithDimenTypeAnnotation) {
    assignedAttributes_epoxyGeneratedModel.set(5);
    onMutation();
    this.intValueWithDimenTypeAnnotation_Int = intValueWithDimenTypeAnnotation;
    return this;
  }

  @Dimension(
      unit = 0
  )
  public int intValueWithDimenTypeAnnotation() {
    return intValueWithDimenTypeAnnotation_Int;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see TestManyTypesView#setIntWithMultipleAnnotations(int)
   */
  public TestManyTypesViewModel_ intWithMultipleAnnotations(@IntRange(from = 0, to = 200) @Dimension(unit = 0) int intWithMultipleAnnotations) {
    assignedAttributes_epoxyGeneratedModel.set(6);
    onMutation();
    this.intWithMultipleAnnotations_Int = intWithMultipleAnnotations;
    return this;
  }

  @IntRange(
      from = 0,
      to = 200
  )
  @Dimension(
      unit = 0
  )
  public int intWithMultipleAnnotations() {
    return intWithMultipleAnnotations_Int;
  }

  /**
   * <i>Required.</i>
   *
   * @see TestManyTypesView#setIntegerValue(Integer)
   */
  public TestManyTypesViewModel_ integerValue(@NonNull Integer integerValue) {
    if (integerValue == null) {
      throw new IllegalArgumentException("integerValue cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(7);
    onMutation();
    this.integerValue_Integer = integerValue;
    return this;
  }

  @NonNull
  public Integer integerValue() {
    return integerValue_Integer;
  }

  /**
   * <i>Optional</i>: Default value is false
   *
   * @see TestManyTypesView#setBoolValue(boolean)
   */
  public TestManyTypesViewModel_ boolValue(boolean boolValue) {
    assignedAttributes_epoxyGeneratedModel.set(8);
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
  public TestManyTypesViewModel_ booleanValue(@NonNull Boolean booleanValue) {
    if (booleanValue == null) {
      throw new IllegalArgumentException("booleanValue cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(9);
    onMutation();
    this.booleanValue_Boolean = booleanValue;
    return this;
  }

  @NonNull
  public Boolean booleanValue() {
    return booleanValue_Boolean;
  }

  /**
   * <i>Required.</i>
   *
   * @see TestManyTypesView#setArrayValue(String[])
   */
  public TestManyTypesViewModel_ arrayValue(@NonNull String[] arrayValue) {
    if (arrayValue == null) {
      throw new IllegalArgumentException("arrayValue cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(10);
    onMutation();
    this.arrayValue_StringArray = arrayValue;
    return this;
  }

  @NonNull
  public String[] arrayValue() {
    return arrayValue_StringArray;
  }

  /**
   * <i>Required.</i>
   *
   * @see TestManyTypesView#setListValue(List<String>)
   */
  public TestManyTypesViewModel_ listValue(@NonNull List<String> listValue) {
    if (listValue == null) {
      throw new IllegalArgumentException("listValue cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(11);
    onMutation();
    this.listValue_List = listValue;
    return this;
  }

  @NonNull
  public List<String> listValue() {
    return listValue_List;
  }

  /**
   * Set a click listener that will provide the parent view, model, and adapter position of the clicked view. This will clear the normal View.OnClickListener if one has been set */
  public TestManyTypesViewModel_ clickListener(@NonNull final OnModelClickListener<TestManyTypesViewModel_, TestManyTypesView> clickListener) {
    assignedAttributes_epoxyGeneratedModel.set(12);
    onMutation();
    if (clickListener == null) {
      this.clickListener_OnClickListener = null;
    }
    else {
      this.clickListener_OnClickListener = new WrappedEpoxyModelClickListener(clickListener);
    }
    return this;
  }

  /**
   * <i>Required.</i>
   *
   * @see TestManyTypesView#setClickListener(View.OnClickListener)
   */
  public TestManyTypesViewModel_ clickListener(@NonNull View.OnClickListener clickListener) {
    if (clickListener == null) {
      throw new IllegalArgumentException("clickListener cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(12);
    onMutation();
    this.clickListener_OnClickListener = clickListener;
    return this;
  }

  @NonNull
  public View.OnClickListener clickListener() {
    return clickListener_OnClickListener;
  }

  @Nullable
  public CharSequence getTitle(Context context) {
    return title_StringAttributeData.toString(context);
  }

  /**
   * <i>Optional</i>: Default value is (CharSequence) null
   *
   * @see TestManyTypesView#setTitle(CharSequence)
   */
  public TestManyTypesViewModel_ title(@Nullable CharSequence title) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(13);
    title_StringAttributeData.setValue(title);
    return this;
  }

  /**
   * If a value of 0 is set then this attribute will revert to its default value.
   * <p>
   * <i>Optional</i>: Default value is (CharSequence) null
   *
   * @see TestManyTypesView#setTitle(CharSequence)
   */
  public TestManyTypesViewModel_ title(@StringRes int stringRes) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(13);
    title_StringAttributeData.setValue(stringRes);
    return this;
  }

  /**
   * If a value of 0 is set then this attribute will revert to its default value.
   * <p>
   * <i>Optional</i>: Default value is (CharSequence) null
   *
   * @see TestManyTypesView#setTitle(CharSequence)
   */
  public TestManyTypesViewModel_ title(@StringRes int stringRes, Object... formatArgs) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(13);
    title_StringAttributeData.setValue(stringRes, formatArgs);
    return this;
  }

  /**
   * If a value of 0 is set then this attribute will revert to its default value.
   * <p>
   * <i>Optional</i>: Default value is (CharSequence) null
   *
   * @see TestManyTypesView#setTitle(CharSequence)
   */
  public TestManyTypesViewModel_ titleQuantityRes(@PluralsRes int pluralRes, int quantity,
      Object... formatArgs) {
    onMutation();
    assignedAttributes_epoxyGeneratedModel.set(13);
    title_StringAttributeData.setValue(pluralRes, quantity, formatArgs);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ id(@NonNull Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ id(@NonNull CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ id(@NonNull CharSequence arg0, @NonNull CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ id(@NonNull CharSequence arg0, long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
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
    this.nullableStringValue_String = (String) null;
    this.intValue_Int = 0;
    this.intValueWithAnnotation_Int = 0;
    this.intValueWithRangeAnnotation_Int = 0;
    this.intValueWithDimenTypeAnnotation_Int = 0;
    this.intWithMultipleAnnotations_Int = 0;
    this.integerValue_Integer = null;
    this.boolValue_Boolean = false;
    this.booleanValue_Boolean = null;
    this.arrayValue_StringArray = null;
    this.listValue_List = null;
    this.clickListener_OnClickListener = null;
    this.title_StringAttributeData =  new StringAttributeData((CharSequence) null);
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
    if (((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if (((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if ((stringValue_String != null ? !stringValue_String.equals(that.stringValue_String) : that.stringValue_String != null)) {
      return false;
    }
    if ((nullableStringValue_String != null ? !nullableStringValue_String.equals(that.nullableStringValue_String) : that.nullableStringValue_String != null)) {
      return false;
    }
    if ((intValue_Int != that.intValue_Int)) {
      return false;
    }
    if ((intValueWithAnnotation_Int != that.intValueWithAnnotation_Int)) {
      return false;
    }
    if ((intValueWithRangeAnnotation_Int != that.intValueWithRangeAnnotation_Int)) {
      return false;
    }
    if ((intValueWithDimenTypeAnnotation_Int != that.intValueWithDimenTypeAnnotation_Int)) {
      return false;
    }
    if ((intWithMultipleAnnotations_Int != that.intWithMultipleAnnotations_Int)) {
      return false;
    }
    if ((integerValue_Integer != null ? !integerValue_Integer.equals(that.integerValue_Integer) : that.integerValue_Integer != null)) {
      return false;
    }
    if ((boolValue_Boolean != that.boolValue_Boolean)) {
      return false;
    }
    if ((booleanValue_Boolean != null ? !booleanValue_Boolean.equals(that.booleanValue_Boolean) : that.booleanValue_Boolean != null)) {
      return false;
    }
    if (!Arrays.equals(arrayValue_StringArray, that.arrayValue_StringArray)) {
      return false;
    }
    if ((listValue_List != null ? !listValue_List.equals(that.listValue_List) : that.listValue_List != null)) {
      return false;
    }
    if (((clickListener_OnClickListener == null) != (that.clickListener_OnClickListener == null))) {
      return false;
    }
    if ((title_StringAttributeData != null ? !title_StringAttributeData.equals(that.title_StringAttributeData) : that.title_StringAttributeData != null)) {
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
    result = 31 * result + (nullableStringValue_String != null ? nullableStringValue_String.hashCode() : 0);
    result = 31 * result + intValue_Int;
    result = 31 * result + intValueWithAnnotation_Int;
    result = 31 * result + intValueWithRangeAnnotation_Int;
    result = 31 * result + intValueWithDimenTypeAnnotation_Int;
    result = 31 * result + intWithMultipleAnnotations_Int;
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
        ", nullableStringValue_String=" + nullableStringValue_String +
        ", intValue_Int=" + intValue_Int +
        ", intValueWithAnnotation_Int=" + intValueWithAnnotation_Int +
        ", intValueWithRangeAnnotation_Int=" + intValueWithRangeAnnotation_Int +
        ", intValueWithDimenTypeAnnotation_Int=" + intValueWithDimenTypeAnnotation_Int +
        ", intWithMultipleAnnotations_Int=" + intWithMultipleAnnotations_Int +
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