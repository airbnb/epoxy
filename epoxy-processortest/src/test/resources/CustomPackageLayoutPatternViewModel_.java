package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.CharSequence;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.BitSet;

/**
 * Generated file. Do not modify! */
public class CustomPackageLayoutPatternViewModel_ extends EpoxyModel<CustomPackageLayoutPatternView> implements GeneratedModel<CustomPackageLayoutPatternView>, CustomPackageLayoutPatternViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(0);

  private OnModelBoundListener<CustomPackageLayoutPatternViewModel_, CustomPackageLayoutPatternView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<CustomPackageLayoutPatternViewModel_, CustomPackageLayoutPatternView> onModelUnboundListener_epoxyGeneratedModel;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder,
      final CustomPackageLayoutPatternView object, final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final CustomPackageLayoutPatternView object) {
    super.bind(object);
  }

  @Override
  public void bind(final CustomPackageLayoutPatternView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof CustomPackageLayoutPatternViewModel_)) {
      bind(object);
      return;
    }
    CustomPackageLayoutPatternViewModel_ that = (CustomPackageLayoutPatternViewModel_) previousModel;
    super.bind(object);
  }

  @Override
  public void handlePostBind(final CustomPackageLayoutPatternView object, int position) {
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
  public CustomPackageLayoutPatternViewModel_ onBind(OnModelBoundListener<CustomPackageLayoutPatternViewModel_, CustomPackageLayoutPatternView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(CustomPackageLayoutPatternView object) {
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
  public CustomPackageLayoutPatternViewModel_ onUnbind(OnModelUnboundListener<CustomPackageLayoutPatternViewModel_, CustomPackageLayoutPatternView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public CustomPackageLayoutPatternViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public CustomPackageLayoutPatternViewModel_ id(@NonNull Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public CustomPackageLayoutPatternViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public CustomPackageLayoutPatternViewModel_ id(@NonNull CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public CustomPackageLayoutPatternViewModel_ id(@NonNull CharSequence arg0,
      @NonNull CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public CustomPackageLayoutPatternViewModel_ id(@NonNull CharSequence arg0, long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public CustomPackageLayoutPatternViewModel_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public CustomPackageLayoutPatternViewModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public CustomPackageLayoutPatternViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public CustomPackageLayoutPatternViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public CustomPackageLayoutPatternViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return R.layout.hello_custom_package_layout_pattern_view_me;
  }

  @Override
  public CustomPackageLayoutPatternViewModel_ reset() {
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
    if (!(o instanceof CustomPackageLayoutPatternViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    CustomPackageLayoutPatternViewModel_ that = (CustomPackageLayoutPatternViewModel_) o;
    if (((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if (((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null))) {
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
    return "CustomPackageLayoutPatternViewModel_{" +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}