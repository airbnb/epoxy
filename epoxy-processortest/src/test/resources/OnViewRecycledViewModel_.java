package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.CharSequence;
import java.lang.IllegalArgumentException;
import java.lang.IllegalStateException;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.BitSet;

/**
 * Generated file. Do not modify! */
public class OnViewRecycledViewModel_ extends EpoxyModel<OnViewRecycledView> implements GeneratedModel<OnViewRecycledView>, OnViewRecycledViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(1);

  private OnModelBoundListener<OnViewRecycledViewModel_, OnViewRecycledView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<OnViewRecycledViewModel_, OnViewRecycledView> onModelUnboundListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0 */
  @NonNull
  private CharSequence title_CharSequence;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(0)) {
    	throw new IllegalStateException("A value is required for setTitle");
    }
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final OnViewRecycledView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final OnViewRecycledView object) {
    super.bind(object);
    object.setTitle(title_CharSequence);
  }

  @Override
  public void bind(final OnViewRecycledView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof OnViewRecycledViewModel_)) {
      bind(object);
      return;
    }
    OnViewRecycledViewModel_ that = (OnViewRecycledViewModel_) previousModel;
    super.bind(object);

    if ((title_CharSequence != null ? !title_CharSequence.equals(that.title_CharSequence) : that.title_CharSequence != null)) {
      object.setTitle(title_CharSequence);
    }
  }

  @Override
  public void handlePostBind(final OnViewRecycledView object, int position) {
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
  public OnViewRecycledViewModel_ onBind(OnModelBoundListener<OnViewRecycledViewModel_, OnViewRecycledView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(OnViewRecycledView object) {
    super.unbind(object);
    if (onModelUnboundListener_epoxyGeneratedModel != null) {
      onModelUnboundListener_epoxyGeneratedModel.onModelUnbound(this, object);
    }
    object.onRecycled1();
    object.onRecycled2();
  }

  /**
   * Register a listener that will be called when this model is unbound from a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public OnViewRecycledViewModel_ onUnbind(OnModelUnboundListener<OnViewRecycledViewModel_, OnViewRecycledView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Required.</i>
   *
   * @see OnViewRecycledView#setTitle(CharSequence)
   */
  public OnViewRecycledViewModel_ title(@NonNull CharSequence title) {
    if (title == null) {
      throw new IllegalArgumentException("title cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(0);
    onMutation();
    this.title_CharSequence = title;
    return this;
  }

  @NonNull
  public CharSequence title() {
    return title_CharSequence;
  }

  @Override
  public OnViewRecycledViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public OnViewRecycledViewModel_ id(@NonNull Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public OnViewRecycledViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public OnViewRecycledViewModel_ id(@NonNull CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public OnViewRecycledViewModel_ id(@NonNull CharSequence arg0, @NonNull CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public OnViewRecycledViewModel_ id(@NonNull CharSequence arg0, long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public OnViewRecycledViewModel_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public OnViewRecycledViewModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public OnViewRecycledViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public OnViewRecycledViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public OnViewRecycledViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public OnViewRecycledViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.title_CharSequence = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof OnViewRecycledViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    OnViewRecycledViewModel_ that = (OnViewRecycledViewModel_) o;
    if (((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if (((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if ((title_CharSequence != null ? !title_CharSequence.equals(that.title_CharSequence) : that.title_CharSequence != null)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (title_CharSequence != null ? title_CharSequence.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "OnViewRecycledViewModel_{" +
        "title_CharSequence=" + title_CharSequence +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}