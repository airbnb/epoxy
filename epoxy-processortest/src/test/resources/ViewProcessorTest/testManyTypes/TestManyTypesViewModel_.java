package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Dimension;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;
import java.lang.Boolean;
import java.lang.CharSequence;
import java.lang.IllegalArgumentException;
import java.lang.IllegalStateException;
import java.lang.Integer;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.UnsupportedOperationException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Generated file. Do not modify!
 */
public class TestManyTypesViewModel_ extends EpoxyModel<TestManyTypesView> implements GeneratedModel<TestManyTypesView>, TestManyTypesViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(24);

  private OnModelBoundListener<TestManyTypesViewModel_, TestManyTypesView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<TestManyTypesViewModel_, TestManyTypesView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<TestManyTypesViewModel_, TestManyTypesView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<TestManyTypesViewModel_, TestManyTypesView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  private int myProperty_Int = 0;

  @Nullable("")
  private Integer myNullableProperty_Integer = (Integer) null;

  private int delegatedProperty_Int = 0;

  private boolean enabled_Boolean = TestManyTypesView.DEFAULT_ENABLED;

  /**
   * Bitset index: 4
   */
  @NotNull("")
  private String stringValue_String;

  /**
   * Bitset index: 5
   */
  @NotNull("")
  private Function2<? super String, ? super String, Integer> functionType_Function2;

  /**
   * Bitset index: 6
   */
  @NotNull("")
  private List<SomeDataClass> listOfDataClass_List;

  /**
   * Bitset index: 7
   */
  @NotNull("")
  private List<? extends SomeEnumClass> listOfEnumClass_List;

  @Nullable("")
  private String nullableStringValue_String = (String) null;

  private int intValue_Int = 0;

  /**
   * Bitset index: 10
   */
  private int intValueWithDefault_Int;

  @StringRes
  private int intValueWithAnnotation_Int = 0;

  @IntRange(
      from = 0,
      to = 200
  )
  private int intValueWithRangeAnnotation_Int = 0;

  @Dimension(
      unit = 0
  )
  private int intValueWithDimenTypeAnnotation_Int = 0;

  @Dimension(
      unit = 0
  )
  @IntRange(
      from = 0,
      to = 200
  )
  private int intWithMultipleAnnotations_Int = 0;

  private int integerValue_Int = 0;

  private boolean boolValue_Boolean = false;

  /**
   * Bitset index: 17
   */
  @NotNull("")
  private List<? extends EpoxyModel<?>> models_List;

  @Nullable("")
  private Boolean booleanValue_Boolean = (Boolean) null;

  @Nullable("")
  private String[] arrayValue_StringArray = (String[]) null;

  @Nullable("")
  private List<String> listValue_List = (List<String>) null;

  @Nullable("")
  private View.OnClickListener clickListener_OnClickListener = (View.OnClickListener) null;

  @Nullable("")
  private CustomClickListenerSubclass customClickListener_CustomClickListenerSubclass = (CustomClickListenerSubclass) null;

  private StringAttributeData title_StringAttributeData =  new StringAttributeData((CharSequence) null);

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(7)) {
    	throw new IllegalStateException("A value is required for setListOfEnumClass");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(4)) {
    	throw new IllegalStateException("A value is required for setStringValue");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(5)) {
    	throw new IllegalStateException("A value is required for setFunctionType");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(6)) {
    	throw new IllegalStateException("A value is required for setListOfDataClass");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(17)) {
    	throw new IllegalStateException("A value is required for setModels");
    }
  }

  @Override
  protected int getViewType() {
    return 0;
  }

  @Override
  public TestManyTypesView buildView(ViewGroup parent) {
    TestManyTypesView v = new TestManyTypesView(parent.getContext());
    v.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT));
    return v;
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final TestManyTypesView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final TestManyTypesView object) {
    super.bind(object);
    object.setListOfEnumClass(listOfEnumClass_List);
    object.setArrayValue(arrayValue_StringArray);
    object.setClickListener(clickListener_OnClickListener);
    object.setBooleanValue(booleanValue_Boolean);
    object.setMyProperty(myProperty_Int);
    object.setCustomClickListener(customClickListener_CustomClickListenerSubclass);
    object.setTitle(title_StringAttributeData.toString(object.getContext()));
    object.setStringValue(stringValue_String);
    object.setNullableStringValue(nullableStringValue_String);
    object.setIntValueWithAnnotation(intValueWithAnnotation_Int);
    object.setIntValueWithDimenTypeAnnotation(intValueWithDimenTypeAnnotation_Int);
    object.setIntWithMultipleAnnotations(intWithMultipleAnnotations_Int);
    object.setEnabled(enabled_Boolean);
    object.setFunctionType(functionType_Function2);
    object.setIntegerValue(integerValue_Int);
    object.setListValue(listValue_List);
    object.setIntValue(intValue_Int);
    object.setDelegatedProperty(delegatedProperty_Int);
    object.setListOfDataClass(listOfDataClass_List);
    object.setMyNullableProperty(myNullableProperty_Integer);
    if (assignedAttributes_epoxyGeneratedModel.get(10)) {
      object.setIntValueWithDefault(intValueWithDefault_Int);
    }
    else {
      object.setIntValueWithDefault();
    }
    object.setIntValueWithRangeAnnotation(intValueWithRangeAnnotation_Int);
    object.setBoolValue(boolValue_Boolean);
    object.setModels(models_List);
  }

  @Override
  public void bind(final TestManyTypesView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof TestManyTypesViewModel_)) {
      bind(object);
      return;
    }
    TestManyTypesViewModel_ that = (TestManyTypesViewModel_) previousModel;
    super.bind(object);

    if ((listOfEnumClass_List != null ? !listOfEnumClass_List.equals(that.listOfEnumClass_List) : that.listOfEnumClass_List != null)) {
      object.setListOfEnumClass(listOfEnumClass_List);
    }

    if (!Arrays.equals(arrayValue_StringArray, that.arrayValue_StringArray)) {
      object.setArrayValue(arrayValue_StringArray);
    }

    if (((clickListener_OnClickListener == null) != (that.clickListener_OnClickListener == null))) {
      object.setClickListener(clickListener_OnClickListener);
    }

    if ((booleanValue_Boolean != null ? !booleanValue_Boolean.equals(that.booleanValue_Boolean) : that.booleanValue_Boolean != null)) {
      object.setBooleanValue(booleanValue_Boolean);
    }

    if ((myProperty_Int != that.myProperty_Int)) {
      object.setMyProperty(myProperty_Int);
    }

    if (((customClickListener_CustomClickListenerSubclass == null) != (that.customClickListener_CustomClickListenerSubclass == null))) {
      object.setCustomClickListener(customClickListener_CustomClickListenerSubclass);
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

    if ((enabled_Boolean != that.enabled_Boolean)) {
      object.setEnabled(enabled_Boolean);
    }

    if (((functionType_Function2 == null) != (that.functionType_Function2 == null))) {
      object.setFunctionType(functionType_Function2);
    }

    if ((integerValue_Int != that.integerValue_Int)) {
      object.setIntegerValue(integerValue_Int);
    }

    if ((listValue_List != null ? !listValue_List.equals(that.listValue_List) : that.listValue_List != null)) {
      object.setListValue(listValue_List);
    }

    if ((intValue_Int != that.intValue_Int)) {
      object.setIntValue(intValue_Int);
    }

    if ((delegatedProperty_Int != that.delegatedProperty_Int)) {
      object.setDelegatedProperty(delegatedProperty_Int);
    }

    if ((listOfDataClass_List != null ? !listOfDataClass_List.equals(that.listOfDataClass_List) : that.listOfDataClass_List != null)) {
      object.setListOfDataClass(listOfDataClass_List);
    }

    if ((myNullableProperty_Integer != null ? !myNullableProperty_Integer.equals(that.myNullableProperty_Integer) : that.myNullableProperty_Integer != null)) {
      object.setMyNullableProperty(myNullableProperty_Integer);
    }

    if (assignedAttributes_epoxyGeneratedModel.get(10)) {
      if ((intValueWithDefault_Int != that.intValueWithDefault_Int)) {
        object.setIntValueWithDefault(intValueWithDefault_Int);
      }
    }
    // A value was not set so we should use the default value, but we only need to set it if the previous model had a custom value set.
    else if (that.assignedAttributes_epoxyGeneratedModel.get(10)) {
      object.setIntValueWithDefault();
    }

    if ((intValueWithRangeAnnotation_Int != that.intValueWithRangeAnnotation_Int)) {
      object.setIntValueWithRangeAnnotation(intValueWithRangeAnnotation_Int);
    }

    if ((boolValue_Boolean != that.boolValue_Boolean)) {
      object.setBoolValue(boolValue_Boolean);
    }

    if ((models_List != null ? !models_List.equals(that.models_List) : that.models_List != null)) {
      object.setModels(models_List);
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
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public TestManyTypesViewModel_ onBind(
      OnModelBoundListener<TestManyTypesViewModel_, TestManyTypesView> listener) {
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
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public TestManyTypesViewModel_ onUnbind(
      OnModelUnboundListener<TestManyTypesViewModel_, TestManyTypesView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState, final TestManyTypesView object) {
    if (onModelVisibilityStateChangedListener_epoxyGeneratedModel != null) {
      onModelVisibilityStateChangedListener_epoxyGeneratedModel.onVisibilityStateChanged(this, object, visibilityState);
    }
    super.onVisibilityStateChanged(visibilityState, object);
  }

  /**
   * Register a listener that will be called when this model visibility state has changed.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   */
  public TestManyTypesViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<TestManyTypesViewModel_, TestManyTypesView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final TestManyTypesView object) {
    if (onModelVisibilityChangedListener_epoxyGeneratedModel != null) {
      onModelVisibilityChangedListener_epoxyGeneratedModel.onVisibilityChanged(this, object, percentVisibleHeight, percentVisibleWidth, visibleHeight, visibleWidth);
    }
    super.onVisibilityChanged(percentVisibleHeight, percentVisibleWidth, visibleHeight, visibleWidth, object);
  }

  /**
   * Register a listener that will be called when this model visibility has changed.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   */
  public TestManyTypesViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<TestManyTypesViewModel_, TestManyTypesView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see TestManyTypesView#setMyProperty(int)
   */
  public TestManyTypesViewModel_ myProperty(int myProperty) {
    onMutation();
    this.myProperty_Int = myProperty;
    return this;
  }

  public int myProperty() {
    return myProperty_Int;
  }

  /**
   * <i>Optional</i>: Default value is (Integer) null
   *
   * @see TestManyTypesView#setMyNullableProperty(Integer)
   */
  public TestManyTypesViewModel_ myNullableProperty(@Nullable("") Integer myNullableProperty) {
    onMutation();
    this.myNullableProperty_Integer = myNullableProperty;
    return this;
  }

  @Nullable("")
  public Integer myNullableProperty() {
    return myNullableProperty_Integer;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see TestManyTypesView#setDelegatedProperty(int)
   */
  public TestManyTypesViewModel_ delegatedProperty(int delegatedProperty) {
    onMutation();
    this.delegatedProperty_Int = delegatedProperty;
    return this;
  }

  public int delegatedProperty() {
    return delegatedProperty_Int;
  }

  /**
   * <i>Optional</i>: Default value is <b>{@value TestManyTypesView#DEFAULT_ENABLED}</b>
   *
   * @see TestManyTypesView#setEnabled(boolean)
   */
  public TestManyTypesViewModel_ enabled(boolean enabled) {
    onMutation();
    this.enabled_Boolean = enabled;
    return this;
  }

  public boolean enabled() {
    return enabled_Boolean;
  }

  /**
   * <i>Required.</i>
   *
   * @see TestManyTypesView#setStringValue(String)
   */
  public TestManyTypesViewModel_ stringValue(@NotNull("") String stringValue) {
    if (stringValue == null) {
      throw new IllegalArgumentException("stringValue cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(4);
    onMutation();
    this.stringValue_String = stringValue;
    return this;
  }

  @NotNull("")
  public String stringValue() {
    return stringValue_String;
  }

  /**
   * <i>Required.</i>
   *
   * @see TestManyTypesView#setFunctionType(Function2<? super String, ? super String, Integer>)
   */
  public TestManyTypesViewModel_ functionType(
      @NotNull("") Function2<? super String, ? super String, Integer> functionType) {
    if (functionType == null) {
      throw new IllegalArgumentException("functionType cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(5);
    onMutation();
    this.functionType_Function2 = functionType;
    return this;
  }

  @NotNull("")
  public Function2<? super String, ? super String, Integer> functionType() {
    return functionType_Function2;
  }

  /**
   * <i>Required.</i>
   *
   * @see TestManyTypesView#setListOfDataClass(List<SomeDataClass>)
   */
  public TestManyTypesViewModel_ listOfDataClass(@NotNull("") List<SomeDataClass> listOfDataClass) {
    if (listOfDataClass == null) {
      throw new IllegalArgumentException("listOfDataClass cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(6);
    onMutation();
    this.listOfDataClass_List = listOfDataClass;
    return this;
  }

  @NotNull("")
  public List<SomeDataClass> listOfDataClass() {
    return listOfDataClass_List;
  }

  /**
   * <i>Required.</i>
   *
   * @see TestManyTypesView#setListOfEnumClass(List<? extends SomeEnumClass>)
   */
  public TestManyTypesViewModel_ listOfEnumClass(
      @NotNull("") List<? extends SomeEnumClass> listOfEnumClass) {
    if (listOfEnumClass == null) {
      throw new IllegalArgumentException("listOfEnumClass cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(7);
    onMutation();
    this.listOfEnumClass_List = listOfEnumClass;
    return this;
  }

  @NotNull("")
  public List<? extends SomeEnumClass> listOfEnumClass() {
    return listOfEnumClass_List;
  }

  /**
   * <i>Optional</i>: Default value is (String) null
   *
   * @see TestManyTypesView#setNullableStringValue(String)
   */
  public TestManyTypesViewModel_ nullableStringValue(@Nullable("") String nullableStringValue) {
    onMutation();
    this.nullableStringValue_String = nullableStringValue;
    return this;
  }

  @Nullable("")
  public String nullableStringValue() {
    return nullableStringValue_String;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see TestManyTypesView#setIntValue(int)
   */
  public TestManyTypesViewModel_ intValue(int intValue) {
    onMutation();
    this.intValue_Int = intValue;
    return this;
  }

  public int intValue() {
    return intValue_Int;
  }

  /**
   * <i>Optional</i>: View function has a Kotlin default argument
   *
   * @see TestManyTypesView#setIntValueWithDefault(int)
   */
  public TestManyTypesViewModel_ intValueWithDefault(int intValueWithDefault) {
    assignedAttributes_epoxyGeneratedModel.set(10);
    onMutation();
    this.intValueWithDefault_Int = intValueWithDefault;
    return this;
  }

  public int intValueWithDefault() {
    return intValueWithDefault_Int;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see TestManyTypesView#setIntValueWithAnnotation(int)
   */
  public TestManyTypesViewModel_ intValueWithAnnotation(@StringRes int intValueWithAnnotation) {
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
  public TestManyTypesViewModel_ intValueWithRangeAnnotation(
      @IntRange(from = 0, to = 200) int intValueWithRangeAnnotation) {
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
  public TestManyTypesViewModel_ intValueWithDimenTypeAnnotation(
      @Dimension(unit = 0) int intValueWithDimenTypeAnnotation) {
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
  public TestManyTypesViewModel_ intWithMultipleAnnotations(
      @Dimension(unit = 0) @IntRange(from = 0, to = 200) int intWithMultipleAnnotations) {
    onMutation();
    this.intWithMultipleAnnotations_Int = intWithMultipleAnnotations;
    return this;
  }

  @Dimension(
      unit = 0
  )
  @IntRange(
      from = 0,
      to = 200
  )
  public int intWithMultipleAnnotations() {
    return intWithMultipleAnnotations_Int;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see TestManyTypesView#setIntegerValue(int)
   */
  public TestManyTypesViewModel_ integerValue(int integerValue) {
    onMutation();
    this.integerValue_Int = integerValue;
    return this;
  }

  public int integerValue() {
    return integerValue_Int;
  }

  /**
   * <i>Optional</i>: Default value is false
   *
   * @see TestManyTypesView#setBoolValue(boolean)
   */
  public TestManyTypesViewModel_ boolValue(boolean boolValue) {
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
   * @see TestManyTypesView#setModels(List<? extends EpoxyModel<?>>)
   */
  public TestManyTypesViewModel_ models(@NotNull("") List<? extends EpoxyModel<?>> models) {
    if (models == null) {
      throw new IllegalArgumentException("models cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(17);
    onMutation();
    this.models_List = models;
    return this;
  }

  @NotNull("")
  public List<? extends EpoxyModel<?>> models() {
    return models_List;
  }

  /**
   * <i>Optional</i>: Default value is (Boolean) null
   *
   * @see TestManyTypesView#setBooleanValue(Boolean)
   */
  public TestManyTypesViewModel_ booleanValue(@Nullable("") Boolean booleanValue) {
    onMutation();
    this.booleanValue_Boolean = booleanValue;
    return this;
  }

  @Nullable("")
  public Boolean booleanValue() {
    return booleanValue_Boolean;
  }

  /**
   * <i>Optional</i>: Default value is (String[]) null
   *
   * @see TestManyTypesView#setArrayValue(String[])
   */
  public TestManyTypesViewModel_ arrayValue(@Nullable("") String[] arrayValue) {
    onMutation();
    this.arrayValue_StringArray = arrayValue;
    return this;
  }

  @Nullable("")
  public String[] arrayValue() {
    return arrayValue_StringArray;
  }

  /**
   * <i>Optional</i>: Default value is (List<String>) null
   *
   * @see TestManyTypesView#setListValue(List<String>)
   */
  public TestManyTypesViewModel_ listValue(@Nullable("") List<String> listValue) {
    onMutation();
    this.listValue_List = listValue;
    return this;
  }

  @Nullable("")
  public List<String> listValue() {
    return listValue_List;
  }

  /**
   * Set a click listener that will provide the parent view, model, and adapter position of the clicked view. This will clear the normal View.OnClickListener if one has been set
   */
  public TestManyTypesViewModel_ clickListener(
      @Nullable("") final OnModelClickListener<TestManyTypesViewModel_, TestManyTypesView> clickListener) {
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
   * <i>Optional</i>: Default value is (View.OnClickListener) null
   *
   * @see TestManyTypesView#setClickListener(View.OnClickListener)
   */
  public TestManyTypesViewModel_ clickListener(@Nullable("") View.OnClickListener clickListener) {
    onMutation();
    this.clickListener_OnClickListener = clickListener;
    return this;
  }

  @Nullable("")
  public View.OnClickListener clickListener() {
    return clickListener_OnClickListener;
  }

  /**
   * <i>Optional</i>: Default value is (CustomClickListenerSubclass) null
   *
   * @see TestManyTypesView#setCustomClickListener(CustomClickListenerSubclass)
   */
  public TestManyTypesViewModel_ customClickListener(
      @Nullable("") CustomClickListenerSubclass customClickListener) {
    onMutation();
    this.customClickListener_CustomClickListenerSubclass = customClickListener;
    return this;
  }

  @Nullable("")
  public CustomClickListenerSubclass customClickListener() {
    return customClickListener_CustomClickListenerSubclass;
  }

  @androidx.annotation.Nullable
  public CharSequence getTitle(Context context) {
    return title_StringAttributeData.toString(context);
  }

  /**
   * <i>Optional</i>: Default value is (CharSequence) null
   *
   * @see TestManyTypesView#setTitle(CharSequence)
   */
  public TestManyTypesViewModel_ title(@androidx.annotation.Nullable CharSequence title) {
    onMutation();
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
    title_StringAttributeData.setValue(pluralRes, quantity, formatArgs);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ id(@androidx.annotation.Nullable Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ id(@androidx.annotation.Nullable CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ id(@androidx.annotation.Nullable CharSequence key,
      @androidx.annotation.Nullable CharSequence... otherKeys) {
    super.id(key, otherKeys);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ id(@androidx.annotation.Nullable CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public TestManyTypesViewModel_ layout(@LayoutRes int layoutRes) {
    throw new UnsupportedOperationException("Layout resources are unsupported with programmatic views.");
  }

  @Override
  public TestManyTypesViewModel_ spanSizeOverride(
      @androidx.annotation.Nullable EpoxyModel.SpanSizeOverrideCallback spanSizeCallback) {
    super.spanSizeOverride(spanSizeCallback);
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
    throw new UnsupportedOperationException("Layout resources are unsupported for views created programmatically.");
  }

  @Override
  public TestManyTypesViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.myProperty_Int = 0;
    this.myNullableProperty_Integer = (Integer) null;
    this.delegatedProperty_Int = 0;
    this.enabled_Boolean = TestManyTypesView.DEFAULT_ENABLED;
    this.stringValue_String = null;
    this.functionType_Function2 = null;
    this.listOfDataClass_List = null;
    this.listOfEnumClass_List = null;
    this.nullableStringValue_String = (String) null;
    this.intValue_Int = 0;
    this.intValueWithDefault_Int = 0;
    this.intValueWithAnnotation_Int = 0;
    this.intValueWithRangeAnnotation_Int = 0;
    this.intValueWithDimenTypeAnnotation_Int = 0;
    this.intWithMultipleAnnotations_Int = 0;
    this.integerValue_Int = 0;
    this.boolValue_Boolean = false;
    this.models_List = null;
    this.booleanValue_Boolean = (Boolean) null;
    this.arrayValue_StringArray = (String[]) null;
    this.listValue_List = (List<String>) null;
    this.clickListener_OnClickListener = (View.OnClickListener) null;
    this.customClickListener_CustomClickListenerSubclass = (CustomClickListenerSubclass) null;
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
    if (((onModelVisibilityStateChangedListener_epoxyGeneratedModel == null) != (that.onModelVisibilityStateChangedListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if (((onModelVisibilityChangedListener_epoxyGeneratedModel == null) != (that.onModelVisibilityChangedListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if ((myProperty_Int != that.myProperty_Int)) {
      return false;
    }
    if ((myNullableProperty_Integer != null ? !myNullableProperty_Integer.equals(that.myNullableProperty_Integer) : that.myNullableProperty_Integer != null)) {
      return false;
    }
    if ((delegatedProperty_Int != that.delegatedProperty_Int)) {
      return false;
    }
    if ((enabled_Boolean != that.enabled_Boolean)) {
      return false;
    }
    if ((stringValue_String != null ? !stringValue_String.equals(that.stringValue_String) : that.stringValue_String != null)) {
      return false;
    }
    if (((functionType_Function2 == null) != (that.functionType_Function2 == null))) {
      return false;
    }
    if ((listOfDataClass_List != null ? !listOfDataClass_List.equals(that.listOfDataClass_List) : that.listOfDataClass_List != null)) {
      return false;
    }
    if ((listOfEnumClass_List != null ? !listOfEnumClass_List.equals(that.listOfEnumClass_List) : that.listOfEnumClass_List != null)) {
      return false;
    }
    if ((nullableStringValue_String != null ? !nullableStringValue_String.equals(that.nullableStringValue_String) : that.nullableStringValue_String != null)) {
      return false;
    }
    if ((intValue_Int != that.intValue_Int)) {
      return false;
    }
    if ((intValueWithDefault_Int != that.intValueWithDefault_Int)) {
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
    if ((integerValue_Int != that.integerValue_Int)) {
      return false;
    }
    if ((boolValue_Boolean != that.boolValue_Boolean)) {
      return false;
    }
    if ((models_List != null ? !models_List.equals(that.models_List) : that.models_List != null)) {
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
    if (((customClickListener_CustomClickListenerSubclass == null) != (that.customClickListener_CustomClickListenerSubclass == null))) {
      return false;
    }
    if ((title_StringAttributeData != null ? !title_StringAttributeData.equals(that.title_StringAttributeData) : that.title_StringAttributeData != null)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int _result = super.hashCode();
    _result = 31 * _result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    _result = 31 * _result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    _result = 31 * _result + (onModelVisibilityStateChangedListener_epoxyGeneratedModel != null ? 1 : 0);
    _result = 31 * _result + (onModelVisibilityChangedListener_epoxyGeneratedModel != null ? 1 : 0);
    _result = 31 * _result + myProperty_Int;
    _result = 31 * _result + (myNullableProperty_Integer != null ? myNullableProperty_Integer.hashCode() : 0);
    _result = 31 * _result + delegatedProperty_Int;
    _result = 31 * _result + (enabled_Boolean ? 1 : 0);
    _result = 31 * _result + (stringValue_String != null ? stringValue_String.hashCode() : 0);
    _result = 31 * _result + (functionType_Function2 != null ? 1 : 0);
    _result = 31 * _result + (listOfDataClass_List != null ? listOfDataClass_List.hashCode() : 0);
    _result = 31 * _result + (listOfEnumClass_List != null ? listOfEnumClass_List.hashCode() : 0);
    _result = 31 * _result + (nullableStringValue_String != null ? nullableStringValue_String.hashCode() : 0);
    _result = 31 * _result + intValue_Int;
    _result = 31 * _result + intValueWithDefault_Int;
    _result = 31 * _result + intValueWithAnnotation_Int;
    _result = 31 * _result + intValueWithRangeAnnotation_Int;
    _result = 31 * _result + intValueWithDimenTypeAnnotation_Int;
    _result = 31 * _result + intWithMultipleAnnotations_Int;
    _result = 31 * _result + integerValue_Int;
    _result = 31 * _result + (boolValue_Boolean ? 1 : 0);
    _result = 31 * _result + (models_List != null ? models_List.hashCode() : 0);
    _result = 31 * _result + (booleanValue_Boolean != null ? booleanValue_Boolean.hashCode() : 0);
    _result = 31 * _result + Arrays.hashCode(arrayValue_StringArray);
    _result = 31 * _result + (listValue_List != null ? listValue_List.hashCode() : 0);
    _result = 31 * _result + (clickListener_OnClickListener != null ? 1 : 0);
    _result = 31 * _result + (customClickListener_CustomClickListenerSubclass != null ? 1 : 0);
    _result = 31 * _result + (title_StringAttributeData != null ? title_StringAttributeData.hashCode() : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "TestManyTypesViewModel_{" +
        "myProperty_Int=" + myProperty_Int +
        ", myNullableProperty_Integer=" + myNullableProperty_Integer +
        ", delegatedProperty_Int=" + delegatedProperty_Int +
        ", enabled_Boolean=" + enabled_Boolean +
        ", stringValue_String=" + stringValue_String +
        ", listOfDataClass_List=" + listOfDataClass_List +
        ", listOfEnumClass_List=" + listOfEnumClass_List +
        ", nullableStringValue_String=" + nullableStringValue_String +
        ", intValue_Int=" + intValue_Int +
        ", intValueWithDefault_Int=" + intValueWithDefault_Int +
        ", intValueWithAnnotation_Int=" + intValueWithAnnotation_Int +
        ", intValueWithRangeAnnotation_Int=" + intValueWithRangeAnnotation_Int +
        ", intValueWithDimenTypeAnnotation_Int=" + intValueWithDimenTypeAnnotation_Int +
        ", intWithMultipleAnnotations_Int=" + intWithMultipleAnnotations_Int +
        ", integerValue_Int=" + integerValue_Int +
        ", boolValue_Boolean=" + boolValue_Boolean +
        ", models_List=" + models_List +
        ", booleanValue_Boolean=" + booleanValue_Boolean +
        ", arrayValue_StringArray=" + arrayValue_StringArray +
        ", listValue_List=" + listValue_List +
        ", clickListener_OnClickListener=" + clickListener_OnClickListener +
        ", customClickListener_CustomClickListenerSubclass=" + customClickListener_CustomClickListenerSubclass +
        ", title_StringAttributeData=" + title_StringAttributeData +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
