package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import java.lang.CharSequence;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.BitSet;

/**
 * Generated file. Do not modify! */
public class RLayoutInViewModelAnnotationWorksViewModel_ extends EpoxyModel<RLayoutInViewModelAnnotationWorksView> implements GeneratedModel<RLayoutInViewModelAnnotationWorksView> {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(0);

  private OnModelBoundListener<RLayoutInViewModelAnnotationWorksViewModel_, RLayoutInViewModelAnnotationWorksView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<RLayoutInViewModelAnnotationWorksViewModel_, RLayoutInViewModelAnnotationWorksView> onModelUnboundListener_epoxyGeneratedModel;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder,
      final RLayoutInViewModelAnnotationWorksView object, int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final RLayoutInViewModelAnnotationWorksView object) {
    super.bind(object);
  }

  @Override
  public void bind(final RLayoutInViewModelAnnotationWorksView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof RLayoutInViewModelAnnotationWorksViewModel_)) {
      bind(object);
      return;
    }
    RLayoutInViewModelAnnotationWorksViewModel_ that = (RLayoutInViewModelAnnotationWorksViewModel_) previousModel;
    super.bind(object);
  }

  @Override
  public void handlePostBind(final RLayoutInViewModelAnnotationWorksView object, int position) {
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
  public RLayoutInViewModelAnnotationWorksViewModel_ onBind(OnModelBoundListener<RLayoutInViewModelAnnotationWorksViewModel_, RLayoutInViewModelAnnotationWorksView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(RLayoutInViewModelAnnotationWorksView object) {
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
  public RLayoutInViewModelAnnotationWorksViewModel_ onUnbind(OnModelUnboundListener<RLayoutInViewModelAnnotationWorksViewModel_, RLayoutInViewModelAnnotationWorksView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public RLayoutInViewModelAnnotationWorksViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public RLayoutInViewModelAnnotationWorksViewModel_ id(Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public RLayoutInViewModelAnnotationWorksViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public RLayoutInViewModelAnnotationWorksViewModel_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public RLayoutInViewModelAnnotationWorksViewModel_ id(CharSequence key,
      CharSequence... otherKeys) {
    super.id(key, otherKeys);
    return this;
  }

  @Override
  public RLayoutInViewModelAnnotationWorksViewModel_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public RLayoutInViewModelAnnotationWorksViewModel_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public RLayoutInViewModelAnnotationWorksViewModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public RLayoutInViewModelAnnotationWorksViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public RLayoutInViewModelAnnotationWorksViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public RLayoutInViewModelAnnotationWorksViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return R.layout.res;
  }

  @Override
  public RLayoutInViewModelAnnotationWorksViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof RLayoutInViewModelAnnotationWorksViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    RLayoutInViewModelAnnotationWorksViewModel_ that = (RLayoutInViewModelAnnotationWorksViewModel_) o;
    if ((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if ((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "RLayoutInViewModelAnnotationWorksViewModel_{" +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}