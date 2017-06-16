package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
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

/**
 * Generated file. Do not modify! */
public class DoNotHashViewModel_ extends EpoxyModel<DoNotHashView> implements GeneratedModel<DoNotHashView> {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(3);

  private OnModelBoundListener<DoNotHashViewModel_, DoNotHashView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<DoNotHashViewModel_, DoNotHashView> onModelUnboundListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0 */
  private CharSequence title_CharSequence;

  /**
   * Bitset index: 1 */
  private View.OnClickListener clickListener_OnClickListener;

  /**
   * Bitset index: 2 */
  private CharSequence normalProp_CharSequence;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(1)) {
    	throw new IllegalStateException("A value is required for setClickListener");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(0)) {
    	throw new IllegalStateException("A value is required for setTitle");
    }
    if (!assignedAttributes_epoxyGeneratedModel.get(2)) {
    	throw new IllegalStateException("A value is required for normalProp");
    }
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final DoNotHashView object,
      int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
    if (clickListener_OnClickListener instanceof WrappedEpoxyModelClickListener) {
      ((com.airbnb.epoxy.WrappedEpoxyModelClickListener) clickListener_OnClickListener).bind(holder, object);
    }
  }

  @Override
  public void bind(final DoNotHashView object) {
    super.bind(object);
    object.setClickListener(clickListener_OnClickListener);
    object.setTitle(title_CharSequence);
    object.normalProp(normalProp_CharSequence);
  }

  @Override
  public void bind(final DoNotHashView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof DoNotHashViewModel_)) {
      bind(object);
      return;
    }
    DoNotHashViewModel_ that = (DoNotHashViewModel_) previousModel;
    super.bind(object);

    if ((clickListener_OnClickListener == null) != (that.clickListener_OnClickListener == null)) {
      object.setClickListener(clickListener_OnClickListener);
    }

    if ((title_CharSequence == null) != (that.title_CharSequence == null)) {
      object.setTitle(title_CharSequence);
    }

    if (normalProp_CharSequence != null ? !normalProp_CharSequence.equals(that.normalProp_CharSequence) : that.normalProp_CharSequence != null) {
      object.normalProp(normalProp_CharSequence);
    }
  }

  @Override
  public void handlePostBind(final DoNotHashView object, int position) {
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
  public DoNotHashViewModel_ onBind(OnModelBoundListener<DoNotHashViewModel_, DoNotHashView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(DoNotHashView object) {
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
  public DoNotHashViewModel_ onUnbind(OnModelUnboundListener<DoNotHashViewModel_, DoNotHashView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Required.</i>
   *
   * @see DoNotHashView#setTitle(CharSequence)
   */
  public DoNotHashViewModel_ title(CharSequence title) {
    if (title == null) {
      throw new IllegalArgumentException("title cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(0);
    onMutation();
    this.title_CharSequence = title;
    return this;
  }

  public CharSequence title() {
    return title_CharSequence;
  }

  /**
   * Set a click listener that will provide the parent view, model, and adapter position of the clicked view. This will clear the normal View.OnClickListener if one has been set */
  public DoNotHashViewModel_ clickListener_OnClickListener(final OnModelClickListener<DoNotHashViewModel_, DoNotHashView> clickListener_OnClickListener) {
    assignedAttributes_epoxyGeneratedModel.set(1);
    onMutation();
    if (clickListener_OnClickListener == null) {
      this.clickListener_OnClickListener = null;
    }
    else {
      this.clickListener_OnClickListener = new WrappedEpoxyModelClickListener(this, clickListener_OnClickListener);
    }
    return this;
  }

  /**
   * <i>Required.</i>
   *
   * @see DoNotHashView#setClickListener(View.OnClickListener)
   */
  public DoNotHashViewModel_ clickListener(View.OnClickListener clickListener) {
    if (clickListener == null) {
      throw new IllegalArgumentException("clickListener cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(1);
    onMutation();
    this.clickListener_OnClickListener = clickListener;
    return this;
  }

  public View.OnClickListener clickListener() {
    return clickListener_OnClickListener;
  }

  /**
   * <i>Required.</i>
   *
   * @see DoNotHashView#normalProp(CharSequence)
   */
  public DoNotHashViewModel_ normalProp(CharSequence normalProp) {
    if (normalProp == null) {
      throw new IllegalArgumentException("normalProp cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(2);
    onMutation();
    this.normalProp_CharSequence = normalProp;
    return this;
  }

  public CharSequence normalProp() {
    return normalProp_CharSequence;
  }

  @Override
  public DoNotHashViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public DoNotHashViewModel_ id(Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public DoNotHashViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public DoNotHashViewModel_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public DoNotHashViewModel_ id(CharSequence key, CharSequence... otherKeys) {
    super.id(key, otherKeys);
    return this;
  }

  @Override
  public DoNotHashViewModel_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public DoNotHashViewModel_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public DoNotHashViewModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public DoNotHashViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public DoNotHashViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public DoNotHashViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public DoNotHashViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.title_CharSequence = null;
    this.clickListener_OnClickListener = null;
    this.normalProp_CharSequence = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof DoNotHashViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    DoNotHashViewModel_ that = (DoNotHashViewModel_) o;
    if ((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if ((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if ((title_CharSequence == null) != (that.title_CharSequence == null)) {
      return false;
    }
    if ((clickListener_OnClickListener == null) != (that.clickListener_OnClickListener == null)) {
      return false;
    }
    if (normalProp_CharSequence != null ? !normalProp_CharSequence.equals(that.normalProp_CharSequence) : that.normalProp_CharSequence != null) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (title_CharSequence != null ? 1 : 0);
    result = 31 * result + (clickListener_OnClickListener != null ? 1 : 0);
    result = 31 * result + (normalProp_CharSequence != null ? normalProp_CharSequence.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "DoNotHashViewModel_{" +
        "title_CharSequence=" + title_CharSequence +
        ", clickListener_OnClickListener=" + clickListener_OnClickListener +
        ", normalProp_CharSequence=" + normalProp_CharSequence +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}