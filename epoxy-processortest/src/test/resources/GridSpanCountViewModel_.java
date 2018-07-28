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
public class GridSpanCountViewModel_ extends EpoxyModel<GridSpanCountView> implements GeneratedModel<GridSpanCountView>, GridSpanCountViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(1);

  private OnModelBoundListener<GridSpanCountViewModel_, GridSpanCountView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<GridSpanCountViewModel_, GridSpanCountView> onModelUnboundListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0 */
  @NonNull
  private String clickListener_String;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(0)) {
    	throw new IllegalStateException("A value is required for setClickListener");
    }
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final GridSpanCountView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final GridSpanCountView object) {
    super.bind(object);
    object.setClickListener(clickListener_String);
  }

  @Override
  public void bind(final GridSpanCountView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof GridSpanCountViewModel_)) {
      bind(object);
      return;
    }
    GridSpanCountViewModel_ that = (GridSpanCountViewModel_) previousModel;
    super.bind(object);

    if ((clickListener_String != null ? !clickListener_String.equals(that.clickListener_String) : that.clickListener_String != null)) {
      object.setClickListener(clickListener_String);
    }
  }

  @Override
  public void handlePostBind(final GridSpanCountView object, int position) {
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
  public GridSpanCountViewModel_ onBind(OnModelBoundListener<GridSpanCountViewModel_, GridSpanCountView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(GridSpanCountView object) {
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
  public GridSpanCountViewModel_ onUnbind(OnModelUnboundListener<GridSpanCountViewModel_, GridSpanCountView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Required.</i>
   *
   * @see GridSpanCountView#setClickListener(String)
   */
  public GridSpanCountViewModel_ clickListener(@NonNull String clickListener) {
    if (clickListener == null) {
      throw new IllegalArgumentException("clickListener cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(0);
    onMutation();
    this.clickListener_String = clickListener;
    return this;
  }

  @NonNull
  public String clickListener() {
    return clickListener_String;
  }

  @Override
  public GridSpanCountViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public GridSpanCountViewModel_ id(@Nullable Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public GridSpanCountViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public GridSpanCountViewModel_ id(@Nullable CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public GridSpanCountViewModel_ id(@Nullable CharSequence arg0, @Nullable CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public GridSpanCountViewModel_ id(@Nullable CharSequence arg0, long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public GridSpanCountViewModel_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public GridSpanCountViewModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public GridSpanCountViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public GridSpanCountViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public GridSpanCountViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public GridSpanCountViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.clickListener_String = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof GridSpanCountViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    GridSpanCountViewModel_ that = (GridSpanCountViewModel_) o;
    if (((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if (((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if ((clickListener_String != null ? !clickListener_String.equals(that.clickListener_String) : that.clickListener_String != null)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (clickListener_String != null ? clickListener_String.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "GridSpanCountViewModel_{" +
        "clickListener_String=" + clickListener_String +
        "}" + super.toString();
  }
}