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
import java.util.ArrayList;
import java.util.BitSet;

/**
 * Generated file. Do not modify! */
public class ListSubtypeModelViewModel_ extends EpoxyModel<ListSubtypeModelView> implements GeneratedModel<ListSubtypeModelView>, ListSubtypeModelViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(1);

  private OnModelBoundListener<ListSubtypeModelViewModel_, ListSubtypeModelView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<ListSubtypeModelViewModel_, ListSubtypeModelView> onModelUnboundListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0 */
  @NonNull
  private ArrayList<String> stringArrayList_ArrayList;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(0)) {
    	throw new IllegalStateException("A value is required for setStringArrayList");
    }
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final ListSubtypeModelView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final ListSubtypeModelView object) {
    super.bind(object);
    object.setStringArrayList(stringArrayList_ArrayList);
  }

  @Override
  public void bind(final ListSubtypeModelView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof ListSubtypeModelViewModel_)) {
      bind(object);
      return;
    }
    ListSubtypeModelViewModel_ that = (ListSubtypeModelViewModel_) previousModel;
    super.bind(object);

    if ((stringArrayList_ArrayList != null ? !stringArrayList_ArrayList.equals(that.stringArrayList_ArrayList) : that.stringArrayList_ArrayList != null)) {
      object.setStringArrayList(stringArrayList_ArrayList);
    }
  }

  @Override
  public void handlePostBind(final ListSubtypeModelView object, int position) {
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
  public ListSubtypeModelViewModel_ onBind(OnModelBoundListener<ListSubtypeModelViewModel_, ListSubtypeModelView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(ListSubtypeModelView object) {
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
  public ListSubtypeModelViewModel_ onUnbind(OnModelUnboundListener<ListSubtypeModelViewModel_, ListSubtypeModelView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Required.</i>
   *
   * @see ListSubtypeModelView#setStringArrayList(ArrayList<String>)
   */
  public ListSubtypeModelViewModel_ stringArrayList(@NonNull ArrayList<String> stringArrayList) {
    if (stringArrayList == null) {
      throw new IllegalArgumentException("stringArrayList cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(0);
    onMutation();
    this.stringArrayList_ArrayList = stringArrayList;
    return this;
  }

  @NonNull
  public ArrayList<String> stringArrayList() {
    return stringArrayList_ArrayList;
  }

  @Override
  public ListSubtypeModelViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ListSubtypeModelViewModel_ id(@Nullable Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public ListSubtypeModelViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public ListSubtypeModelViewModel_ id(@Nullable CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public ListSubtypeModelViewModel_ id(@Nullable CharSequence arg0,
      @Nullable CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public ListSubtypeModelViewModel_ id(@Nullable CharSequence arg0, long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public ListSubtypeModelViewModel_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ListSubtypeModelViewModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public ListSubtypeModelViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public ListSubtypeModelViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ListSubtypeModelViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public ListSubtypeModelViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.stringArrayList_ArrayList = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ListSubtypeModelViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ListSubtypeModelViewModel_ that = (ListSubtypeModelViewModel_) o;
    if (((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if (((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if ((stringArrayList_ArrayList != null ? !stringArrayList_ArrayList.equals(that.stringArrayList_ArrayList) : that.stringArrayList_ArrayList != null)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (stringArrayList_ArrayList != null ? stringArrayList_ArrayList.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ListSubtypeModelViewModel_{" +
        "stringArrayList_ArrayList=" + stringArrayList_ArrayList +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}