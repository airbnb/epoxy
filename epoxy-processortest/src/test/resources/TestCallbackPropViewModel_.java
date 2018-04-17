package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import java.lang.CharSequence;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.BitSet;

/**
 * Generated file. Do not modify! */
public class TestCallbackPropViewModel_ extends EpoxyModel<TestCallbackPropView> implements GeneratedModel<TestCallbackPropView>, TestCallbackPropViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(1);

  private OnModelBoundListener<TestCallbackPropViewModel_, TestCallbackPropView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<TestCallbackPropViewModel_, TestCallbackPropView> onModelUnboundListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0 */
  @Nullable
  private View.OnClickListener listener_OnClickListener = (View.OnClickListener) null;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final TestCallbackPropView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final TestCallbackPropView object) {
    super.bind(object);
    object.setListener(listener_OnClickListener);
  }

  @Override
  public void bind(final TestCallbackPropView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof TestCallbackPropViewModel_)) {
      bind(object);
      return;
    }
    TestCallbackPropViewModel_ that = (TestCallbackPropViewModel_) previousModel;
    super.bind(object);

    if (((listener_OnClickListener == null) != (that.listener_OnClickListener == null))) {
      object.setListener(listener_OnClickListener);
    }
  }

  @Override
  public void handlePostBind(final TestCallbackPropView object, int position) {
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
  public TestCallbackPropViewModel_ onBind(OnModelBoundListener<TestCallbackPropViewModel_, TestCallbackPropView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(TestCallbackPropView object) {
    super.unbind(object);
    if (onModelUnboundListener_epoxyGeneratedModel != null) {
      onModelUnboundListener_epoxyGeneratedModel.onModelUnbound(this, object);
    }
    object.setListener((View.OnClickListener) null);
  }

  /**
   * Register a listener that will be called when this model is unbound from a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public TestCallbackPropViewModel_ onUnbind(OnModelUnboundListener<TestCallbackPropViewModel_, TestCallbackPropView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * Set a click listener that will provide the parent view, model, and adapter position of the clicked view. This will clear the normal View.OnClickListener if one has been set */
  @Nullable
  public TestCallbackPropViewModel_ listener(final OnModelClickListener<TestCallbackPropViewModel_, TestCallbackPropView> listener) {
    assignedAttributes_epoxyGeneratedModel.set(0);
    onMutation();
    if (listener == null) {
      this.listener_OnClickListener = null;
    }
    else {
      this.listener_OnClickListener = new WrappedEpoxyModelClickListener(listener);
    }
    return this;
  }

  /**
   * <i>Optional</i>: Default value is (View.OnClickListener) null
   *
   * @see TestCallbackPropView#setListener(View.OnClickListener)
   */
  public TestCallbackPropViewModel_ listener(@Nullable View.OnClickListener listener) {
    assignedAttributes_epoxyGeneratedModel.set(0);
    onMutation();
    this.listener_OnClickListener = listener;
    return this;
  }

  @Nullable
  public View.OnClickListener listener() {
    return listener_OnClickListener;
  }

  @Override
  public TestCallbackPropViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ id(@NonNull Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ id(@NonNull CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ id(@NonNull CharSequence arg0, @NonNull CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ id(@NonNull CharSequence arg0, long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public TestCallbackPropViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public TestCallbackPropViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.listener_OnClickListener = (View.OnClickListener) null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof TestCallbackPropViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    TestCallbackPropViewModel_ that = (TestCallbackPropViewModel_) o;
    if (((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if (((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if (((listener_OnClickListener == null) != (that.listener_OnClickListener == null))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (listener_OnClickListener != null ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TestCallbackPropViewModel_{" +
        "listener_OnClickListener=" + listener_OnClickListener +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}