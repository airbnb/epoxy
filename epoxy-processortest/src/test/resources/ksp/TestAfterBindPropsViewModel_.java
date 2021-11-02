package com.airbnb.epoxy;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import java.lang.CharSequence;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify!
 */
public class TestAfterBindPropsViewModel_ extends EpoxyModel<TestAfterBindPropsView> implements GeneratedModel<TestAfterBindPropsView>, TestAfterBindPropsViewModelBuilder {
  private OnModelBoundListener<TestAfterBindPropsViewModel_, TestAfterBindPropsView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<TestAfterBindPropsViewModel_, TestAfterBindPropsView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<TestAfterBindPropsViewModel_, TestAfterBindPropsView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<TestAfterBindPropsViewModel_, TestAfterBindPropsView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  private boolean flag_Boolean = false;

  private boolean flagSuper_Boolean = false;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final TestAfterBindPropsView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final TestAfterBindPropsView object) {
    super.bind(object);
    object.setFlagSuper(flagSuper_Boolean);
    object.setFlag(flag_Boolean);
  }

  @Override
  public void bind(final TestAfterBindPropsView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof TestAfterBindPropsViewModel_)) {
      bind(object);
      return;
    }
    TestAfterBindPropsViewModel_ that = (TestAfterBindPropsViewModel_) previousModel;
    super.bind(object);

    if ((flagSuper_Boolean != that.flagSuper_Boolean)) {
      object.setFlagSuper(flagSuper_Boolean);
    }

    if ((flag_Boolean != that.flag_Boolean)) {
      object.setFlag(flag_Boolean);
    }
  }

  @Override
  public void handlePostBind(final TestAfterBindPropsView object, int position) {
    if (onModelBoundListener_epoxyGeneratedModel != null) {
      onModelBoundListener_epoxyGeneratedModel.onModelBound(this, object, position);
    }
    validateStateHasNotChangedSinceAdded("The model was changed during the bind call.", position);
    object.afterFlagSet();
    object.afterFlagSetSuper();
  }

  /**
   * Register a listener that will be called when this model is bound to a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()}
   */
  public TestAfterBindPropsViewModel_ onBind(
      OnModelBoundListener<TestAfterBindPropsViewModel_, TestAfterBindPropsView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(TestAfterBindPropsView object) {
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
  public TestAfterBindPropsViewModel_ onUnbind(
      OnModelUnboundListener<TestAfterBindPropsViewModel_, TestAfterBindPropsView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState, final TestAfterBindPropsView object) {
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
  public TestAfterBindPropsViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<TestAfterBindPropsViewModel_, TestAfterBindPropsView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final TestAfterBindPropsView object) {
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
  public TestAfterBindPropsViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<TestAfterBindPropsViewModel_, TestAfterBindPropsView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Optional</i>: Default value is false
   *
   * @see TestAfterBindPropsView#setFlag(boolean)
   */
  public TestAfterBindPropsViewModel_ flag(boolean flag) {
    onMutation();
    this.flag_Boolean = flag;
    return this;
  }

  public boolean flag() {
    return flag_Boolean;
  }

  /**
   * <i>Optional</i>: Default value is false
   *
   * @see TestAfterBindPropsSuperView#setFlagSuper(boolean)
   */
  public TestAfterBindPropsViewModel_ flagSuper(boolean flagSuper) {
    onMutation();
    this.flagSuper_Boolean = flagSuper;
    return this;
  }

  public boolean flagSuper() {
    return flagSuper_Boolean;
  }

  @Override
  public TestAfterBindPropsViewModel_ id(long p0) {
    super.id(p0);
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ id(@Nullable Number... p0) {
    super.id(p0);
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ id(long p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ id(@Nullable CharSequence p0) {
    super.id(p0);
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ id(@Nullable CharSequence p0, @Nullable CharSequence... p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ id(@Nullable CharSequence p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ layout(@LayoutRes int p0) {
    super.layout(p0);
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback p0) {
    super.spanSizeOverride(p0);
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ show(boolean p0) {
    super.show(p0);
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public TestAfterBindPropsViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    this.flag_Boolean = false;
    this.flagSuper_Boolean = false;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof TestAfterBindPropsViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    TestAfterBindPropsViewModel_ that = (TestAfterBindPropsViewModel_) o;
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
    if ((flag_Boolean != that.flag_Boolean)) {
      return false;
    }
    if ((flagSuper_Boolean != that.flagSuper_Boolean)) {
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
    _result = 31 * _result + (flag_Boolean ? 1 : 0);
    _result = 31 * _result + (flagSuper_Boolean ? 1 : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "TestAfterBindPropsViewModel_{" +
        "flag_Boolean=" + flag_Boolean +
        ", flagSuper_Boolean=" + flagSuper_Boolean +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
