package com.airbnb.epoxy;

import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import java.lang.CharSequence;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.UnsupportedOperationException;

/**
 * Generated file. Do not modify!
 */
public class SourceViewModel_ extends AirEpoxyModel<SourceView> implements GeneratedModel<SourceView>, SourceViewModelBuilder {
  private OnModelBoundListener<SourceViewModel_, SourceView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<SourceViewModel_, SourceView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<SourceViewModel_, SourceView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<SourceViewModel_, SourceView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  @Nullable
  private String sectionId_String = (String) null;

  private int baseViewProp_Int = 0;

  public SourceViewModel_() {
    super();
  }

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  protected int getViewType() {
    return 0;
  }

  @Override
  public SourceView buildView(ViewGroup parent) {
    SourceView v = new SourceView(parent.getContext());
    v.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT));
    return v;
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final SourceView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final SourceView object) {
    super.bind(object);
    object.setSectionId(sectionId_String);
    object.baseViewProp(baseViewProp_Int);
  }

  @Override
  public void bind(final SourceView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof SourceViewModel_)) {
      bind(object);
      return;
    }
    SourceViewModel_ that = (SourceViewModel_) previousModel;
    super.bind(object);

    if ((sectionId_String != null ? !sectionId_String.equals(that.sectionId_String) : that.sectionId_String != null)) {
      object.setSectionId(sectionId_String);
    }

    if ((baseViewProp_Int != that.baseViewProp_Int)) {
      object.baseViewProp(baseViewProp_Int);
    }
  }

  @Override
  public void handlePostBind(final SourceView object, int position) {
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
  public SourceViewModel_ onBind(OnModelBoundListener<SourceViewModel_, SourceView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(SourceView object) {
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
  public SourceViewModel_ onUnbind(OnModelUnboundListener<SourceViewModel_, SourceView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState, final SourceView object) {
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
  public SourceViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<SourceViewModel_, SourceView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final SourceView object) {
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
  public SourceViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<SourceViewModel_, SourceView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Optional</i>: Default value is (String) null
   *
   * @see SourceView#setSectionId(String)
   */
  public SourceViewModel_ sectionId(@Nullable String sectionId) {
    onMutation();
    this.sectionId_String = sectionId;
    return this;
  }

  @Nullable
  public String sectionId() {
    return sectionId_String;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see BaseView#baseViewProp(int)
   */
  public SourceViewModel_ baseViewProp(int baseViewProp) {
    onMutation();
    this.baseViewProp_Int = baseViewProp;
    return this;
  }

  public int baseViewProp() {
    return baseViewProp_Int;
  }

  /**
   * The number of items that should be shown on screen. This assumes the model is used in a horizontal RecyclerView (aka {@link Carousel}), and the
   *  view will have its width resized so that the appropriate number of items fit on screen.
   *  <p>
   *  We don't generate a getter since this class defines one, so it is accessible with just a `AirEpoxyModel` reference.
   */
  public SourceViewModel_ numCarouselItemsShown(
      @Nullable AirEpoxyModel.SomeType numCarouselItemsShown) {
    onMutation();
    super.numCarouselItemsShown = numCarouselItemsShown;
    return this;
  }

  @Override
  public SourceViewModel_ id(long p0) {
    super.id(p0);
    return this;
  }

  @Override
  public SourceViewModel_ id(@Nullable Number... p0) {
    super.id(p0);
    return this;
  }

  @Override
  public SourceViewModel_ id(long p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public SourceViewModel_ id(@Nullable CharSequence p0) {
    super.id(p0);
    return this;
  }

  @Override
  public SourceViewModel_ id(@Nullable CharSequence p0, @Nullable CharSequence... p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public SourceViewModel_ id(@Nullable CharSequence p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public SourceViewModel_ layout(@LayoutRes int p0) {
    throw new UnsupportedOperationException("Layout resources are unsupported with programmatic views.");
  }

  @Override
  public SourceViewModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback p0) {
    super.spanSizeOverride(p0);
    return this;
  }

  @Override
  public SourceViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public SourceViewModel_ show(boolean p0) {
    super.show(p0);
    return this;
  }

  @Override
  public SourceViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    throw new UnsupportedOperationException("Layout resources are unsupported for views created programmatically.");
  }

  @Override
  public SourceViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    this.sectionId_String = (String) null;
    this.baseViewProp_Int = 0;
    super.showDivider = null;
    super.numCarouselItemsShown = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof SourceViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    SourceViewModel_ that = (SourceViewModel_) o;
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
    if ((sectionId_String != null ? !sectionId_String.equals(that.sectionId_String) : that.sectionId_String != null)) {
      return false;
    }
    if ((baseViewProp_Int != that.baseViewProp_Int)) {
      return false;
    }
    if ((showDivider != null ? !showDivider.equals(that.showDivider) : that.showDivider != null)) {
      return false;
    }
    if ((numCarouselItemsShown != null ? !numCarouselItemsShown.equals(that.numCarouselItemsShown) : that.numCarouselItemsShown != null)) {
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
    _result = 31 * _result + (sectionId_String != null ? sectionId_String.hashCode() : 0);
    _result = 31 * _result + baseViewProp_Int;
    _result = 31 * _result + (showDivider != null ? showDivider.hashCode() : 0);
    _result = 31 * _result + (numCarouselItemsShown != null ? numCarouselItemsShown.hashCode() : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "SourceViewModel_{" +
        "sectionId_String=" + sectionId_String +
        ", baseViewProp_Int=" + baseViewProp_Int +
        ", showDivider=" + showDivider +
        ", numCarouselItemsShown=" + numCarouselItemsShown +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
