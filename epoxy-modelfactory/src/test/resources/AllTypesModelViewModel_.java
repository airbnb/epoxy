package com.airbnb.epoxy;

import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import java.lang.CharSequence;
import java.lang.IllegalArgumentException;
import java.lang.IllegalStateException;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.BitSet;
import java.util.List;

/**
 * Generated file. Do not modify! */
public class AllTypesModelViewModel_ extends EpoxyModel<AllTypesModelView> implements GeneratedModel<AllTypesModelView>, AllTypesModelViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(7);

  private OnModelBoundListener<AllTypesModelViewModel_, AllTypesModelView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<AllTypesModelViewModel_, AllTypesModelView> onModelUnboundListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0 */
  private boolean booleanValue_Boolean = false;

  /**
   * Bitset index: 1 */
  private double doubleValue_Double = 0.0d;

  /**
   * Bitset index: 2 */
  @DrawableRes
  private int drawableRes_Int = 0;

  /**
   * Bitset index: 3 */
  private int intValue_Int = 0;

  /**
   * Bitset index: 4 */
  @NonNull
  private View.OnClickListener onClickListener_OnClickListener;

  /**
   * Bitset index: 5 */
  @NonNull
  private String stringValue_String;

  /**
   * Bitset index: 6 */
  @NonNull
  private List<String> stringList_List;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(5)) {
    	throw new IllegalStateException("A value is required for setStringValue");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(4)) {
    	throw new IllegalStateException("A value is required for setOnClickListener");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(6)) {
    	throw new IllegalStateException("A value is required for setStringList");
    }
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final AllTypesModelView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final AllTypesModelView object) {
    super.bind(object);
    object.setStringValue(stringValue_String);
    object.setOnClickListener(onClickListener_OnClickListener);
    object.setIntValue(intValue_Int);
    object.setBooleanValue(booleanValue_Boolean);
    object.setDrawableRes(drawableRes_Int);
    object.setDoubleValue(doubleValue_Double);
    object.setStringList(stringList_List);
  }

  @Override
  public void bind(final AllTypesModelView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof AllTypesModelViewModel_)) {
      bind(object);
      return;
    }
    AllTypesModelViewModel_ that = (AllTypesModelViewModel_) previousModel;
    super.bind(object);

    if ((stringValue_String != null ? !stringValue_String.equals(that.stringValue_String) : that.stringValue_String != null)) {
      object.setStringValue(stringValue_String);
    }

    if (((onClickListener_OnClickListener == null) != (that.onClickListener_OnClickListener == null))) {
      object.setOnClickListener(onClickListener_OnClickListener);
    }

    if ((intValue_Int != that.intValue_Int)) {
      object.setIntValue(intValue_Int);
    }

    if ((booleanValue_Boolean != that.booleanValue_Boolean)) {
      object.setBooleanValue(booleanValue_Boolean);
    }

    if ((drawableRes_Int != that.drawableRes_Int)) {
      object.setDrawableRes(drawableRes_Int);
    }

    if ((Double.compare(that.doubleValue_Double, doubleValue_Double) != 0)) {
      object.setDoubleValue(doubleValue_Double);
    }

    if ((stringList_List != null ? !stringList_List.equals(that.stringList_List) : that.stringList_List != null)) {
      object.setStringList(stringList_List);
    }
  }

  @Override
  public void handlePostBind(final AllTypesModelView object, int position) {
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
  public AllTypesModelViewModel_ onBind(OnModelBoundListener<AllTypesModelViewModel_, AllTypesModelView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(AllTypesModelView object) {
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
  public AllTypesModelViewModel_ onUnbind(OnModelUnboundListener<AllTypesModelViewModel_, AllTypesModelView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Optional</i>: Default value is false
   *
   * @see AllTypesModelView#setBooleanValue(boolean)
   */
  public AllTypesModelViewModel_ booleanValue(boolean booleanValue) {
    assignedAttributes_epoxyGeneratedModel.set(0);
    onMutation();
    this.booleanValue_Boolean = booleanValue;
    return this;
  }

  public boolean booleanValue() {
    return booleanValue_Boolean;
  }

  /**
   * <i>Optional</i>: Default value is 0.0d
   *
   * @see AllTypesModelView#setDoubleValue(double)
   */
  public AllTypesModelViewModel_ doubleValue(double doubleValue) {
    assignedAttributes_epoxyGeneratedModel.set(1);
    onMutation();
    this.doubleValue_Double = doubleValue;
    return this;
  }

  public double doubleValue() {
    return doubleValue_Double;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see AllTypesModelView#setDrawableRes(int)
   */
  public AllTypesModelViewModel_ drawableRes(@DrawableRes int drawableRes) {
    assignedAttributes_epoxyGeneratedModel.set(2);
    onMutation();
    this.drawableRes_Int = drawableRes;
    return this;
  }

  @DrawableRes
  public int drawableRes() {
    return drawableRes_Int;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see AllTypesModelView#setIntValue(int)
   */
  public AllTypesModelViewModel_ intValue(int intValue) {
    assignedAttributes_epoxyGeneratedModel.set(3);
    onMutation();
    this.intValue_Int = intValue;
    return this;
  }

  public int intValue() {
    return intValue_Int;
  }

  /**
   * Set a click listener that will provide the parent view, model, and adapter position of the clicked view. This will clear the normal View.OnClickListener if one has been set */
  @NonNull
  public AllTypesModelViewModel_ onClickListener(final OnModelClickListener<AllTypesModelViewModel_, AllTypesModelView> onClickListener) {
    assignedAttributes_epoxyGeneratedModel.set(4);
    onMutation();
    if (onClickListener == null) {
      this.onClickListener_OnClickListener = null;
    }
    else {
      this.onClickListener_OnClickListener = new WrappedEpoxyModelClickListener(onClickListener);
    }
    return this;
  }

  /**
   * <i>Required.</i>
   *
   * @see AllTypesModelView#setOnClickListener(View.OnClickListener)
   */
  public AllTypesModelViewModel_ onClickListener(@NonNull View.OnClickListener onClickListener) {
    if (onClickListener == null) {
      throw new IllegalArgumentException("onClickListener cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(4);
    onMutation();
    this.onClickListener_OnClickListener = onClickListener;
    return this;
  }

  @NonNull
  public View.OnClickListener onClickListener() {
    return onClickListener_OnClickListener;
  }

  /**
   * <i>Required.</i>
   *
   * @see AllTypesModelView#setStringValue(String)
   */
  public AllTypesModelViewModel_ stringValue(@NonNull String stringValue) {
    if (stringValue == null) {
      throw new IllegalArgumentException("stringValue cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(5);
    onMutation();
    this.stringValue_String = stringValue;
    return this;
  }

  @NonNull
  public String stringValue() {
    return stringValue_String;
  }

  /**
   * <i>Required.</i>
   *
   * @see AllTypesModelView#setStringList(List<String>)
   */
  public AllTypesModelViewModel_ stringList(@NonNull List<String> stringList) {
    if (stringList == null) {
      throw new IllegalArgumentException("stringList cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(6);
    onMutation();
    this.stringList_List = stringList;
    return this;
  }

  @NonNull
  public List<String> stringList() {
    return stringList_List;
  }

  @Override
  public AllTypesModelViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public AllTypesModelViewModel_ id(@NonNull Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public AllTypesModelViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public AllTypesModelViewModel_ id(@NonNull CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public AllTypesModelViewModel_ id(@NonNull CharSequence arg0, @NonNull CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public AllTypesModelViewModel_ id(@NonNull CharSequence arg0, long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public AllTypesModelViewModel_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public AllTypesModelViewModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public AllTypesModelViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public AllTypesModelViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public AllTypesModelViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public AllTypesModelViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.booleanValue_Boolean = false;
    this.doubleValue_Double = 0.0d;
    this.drawableRes_Int = 0;
    this.intValue_Int = 0;
    this.onClickListener_OnClickListener = null;
    this.stringValue_String = null;
    this.stringList_List = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof AllTypesModelViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    AllTypesModelViewModel_ that = (AllTypesModelViewModel_) o;
    if (((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if (((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if ((booleanValue_Boolean != that.booleanValue_Boolean)) {
      return false;
    }
    if ((Double.compare(that.doubleValue_Double, doubleValue_Double) != 0)) {
      return false;
    }
    if ((drawableRes_Int != that.drawableRes_Int)) {
      return false;
    }
    if ((intValue_Int != that.intValue_Int)) {
      return false;
    }
    if (((onClickListener_OnClickListener == null) != (that.onClickListener_OnClickListener == null))) {
      return false;
    }
    if ((stringValue_String != null ? !stringValue_String.equals(that.stringValue_String) : that.stringValue_String != null)) {
      return false;
    }
    if ((stringList_List != null ? !stringList_List.equals(that.stringList_List) : that.stringList_List != null)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    long temp;
    result = 31 * result + (booleanValue_Boolean ? 1 : 0);
    temp = Double.doubleToLongBits(doubleValue_Double);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + drawableRes_Int;
    result = 31 * result + intValue_Int;
    result = 31 * result + (onClickListener_OnClickListener != null ? 1 : 0);
    result = 31 * result + (stringValue_String != null ? stringValue_String.hashCode() : 0);
    result = 31 * result + (stringList_List != null ? stringList_List.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "AllTypesModelViewModel_{" +
        "booleanValue_Boolean=" + booleanValue_Boolean +
        ", doubleValue_Double=" + doubleValue_Double +
        ", drawableRes_Int=" + drawableRes_Int +
        ", intValue_Int=" + intValue_Int +
        ", onClickListener_OnClickListener=" + onClickListener_OnClickListener +
        ", stringValue_String=" + stringValue_String +
        ", stringList_List=" + stringList_List +
        "}" + super.toString();
  }

  public static AllTypesModelViewModel_ from(ModelProperties properties) {
    AllTypesModelViewModel_ model = new AllTypesModelViewModel_();
    model.id(properties.getId());
    if (properties.has("booleanValue")) {
      model.booleanValue(properties.getBoolean("booleanValue"));
    }
    if (properties.has("doubleValue")) {
      model.doubleValue(properties.getDouble("doubleValue"));
    }
    if (properties.has("drawableRes")) {
      model.drawableRes(properties.getDrawableRes("drawableRes"));
    }
    if (properties.has("intValue")) {
      model.intValue(properties.getInt("intValue"));
    }
    if (properties.has("onClickListener")) {
      model.onClickListener(properties.getOnClickListener("onClickListener"));
    }
    if (properties.has("stringValue")) {
      model.stringValue(properties.getString("stringValue"));
    }
    if (properties.has("stringList")) {
      model.stringList(properties.getStringList("stringList"));
    }
    return model;
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}