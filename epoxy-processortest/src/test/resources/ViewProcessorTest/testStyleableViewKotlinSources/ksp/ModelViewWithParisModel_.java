package com.airbnb.epoxy;

import android.os.AsyncTask;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import com.airbnb.paris.StyleApplierUtils;
import com.airbnb.paris.styles.Style;
import com.airbnb.viewmodeladapter.R;
import java.lang.AssertionError;
import java.lang.CharSequence;
import java.lang.IllegalStateException;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.String;
import java.lang.UnsupportedOperationException;
import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * Generated file. Do not modify!
 */
public class ModelViewWithParisModel_ extends EpoxyModel<ModelViewWithParis> implements GeneratedModel<ModelViewWithParis>, ModelViewWithParisModelBuilder {
  private static final Style DEFAULT_PARIS_STYLE = new ModelViewWithParisStyleApplier.StyleBuilder().addDefault().build();

  private static WeakReference<Style> parisStyleReference_header;

  private static WeakReference<Style> parisStyleReference_default;

  private OnModelBoundListener<ModelViewWithParisModel_, ModelViewWithParis> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<ModelViewWithParisModel_, ModelViewWithParis> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<ModelViewWithParisModel_, ModelViewWithParis> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<ModelViewWithParisModel_, ModelViewWithParis> onModelVisibilityChangedListener_epoxyGeneratedModel;

  private int value_Int = 0;

  private int delegatedProperty_Int = 0;

  private Style style = DEFAULT_PARIS_STYLE;

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
  public ModelViewWithParis buildView(ViewGroup parent) {
    ModelViewWithParis v = new ModelViewWithParis(parent.getContext());
    v.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT));
    return v;
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final ModelViewWithParis object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
    if (!Objects.equals(style, object.getTag(R.id.epoxy_saved_view_style))) {
      AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
        public void run() {
          try {
            StyleApplierUtils.Companion.assertSameAttributes(new ModelViewWithParisStyleApplier(object), style, DEFAULT_PARIS_STYLE);
          }
          catch(AssertionError e) {
            throw new IllegalStateException("ModelViewWithParisModel_ model at position " + position + " has an invalid style:\n\n" + e.getMessage());
          }
        }
      } );
    }
  }

  @Override
  public void bind(final ModelViewWithParis object) {

    if (!Objects.equals(style, object.getTag(R.id.epoxy_saved_view_style))) {
      ModelViewWithParisStyleApplier styleApplier = new ModelViewWithParisStyleApplier(object);
      styleApplier.apply(style);
      object.setTag(R.id.epoxy_saved_view_style, style);
    }
    super.bind(object);
    object.setValue(value_Int);
    object.setDelegatedProperty(delegatedProperty_Int);
  }

  @Override
  public void bind(final ModelViewWithParis object, EpoxyModel previousModel) {
    if (!(previousModel instanceof ModelViewWithParisModel_)) {
      bind(object);
      return;
    }
    ModelViewWithParisModel_ that = (ModelViewWithParisModel_) previousModel;

    if (!Objects.equals(style, that.style)) {
      ModelViewWithParisStyleApplier styleApplier = new ModelViewWithParisStyleApplier(object);
      styleApplier.apply(style);
      object.setTag(R.id.epoxy_saved_view_style, style);
    }
    super.bind(object);

    if ((value_Int != that.value_Int)) {
      object.setValue(value_Int);
    }

    if ((delegatedProperty_Int != that.delegatedProperty_Int)) {
      object.setDelegatedProperty(delegatedProperty_Int);
    }
  }

  @Override
  public void handlePostBind(final ModelViewWithParis object, int position) {
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
  public ModelViewWithParisModel_ onBind(
      OnModelBoundListener<ModelViewWithParisModel_, ModelViewWithParis> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(ModelViewWithParis object) {
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
  public ModelViewWithParisModel_ onUnbind(
      OnModelUnboundListener<ModelViewWithParisModel_, ModelViewWithParis> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState, final ModelViewWithParis object) {
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
  public ModelViewWithParisModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<ModelViewWithParisModel_, ModelViewWithParis> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final ModelViewWithParis object) {
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
  public ModelViewWithParisModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<ModelViewWithParisModel_, ModelViewWithParis> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  public ModelViewWithParisModel_ style(Style style) {
    onMutation();
    this.style = style;
    return this;
  }

  public ModelViewWithParisModel_ styleBuilder(
      StyleBuilderCallback<ModelViewWithParisStyleApplier.StyleBuilder> builderCallback) {
    ModelViewWithParisStyleApplier.StyleBuilder builder = new ModelViewWithParisStyleApplier.StyleBuilder();
    builderCallback.buildStyle(builder.addDefault());
    return style(builder.build());
  }

  /**
   *
   *  @see ModelViewWithParis.Companion#headerStyle
   */
  public ModelViewWithParisModel_ withHeaderStyle() {
    Style style = parisStyleReference_header != null ? parisStyleReference_header.get() : null;
    if (style == null) {
      style =  new ModelViewWithParisStyleApplier.StyleBuilder().addHeader().build();
      parisStyleReference_header = new WeakReference<>(style);
    }
    return style(style);
  }

  /**
   *
   *  @see ModelViewWithParis.Companion#headerStyle
   */
  public ModelViewWithParisModel_ withDefaultStyle() {
    Style style = parisStyleReference_default != null ? parisStyleReference_default.get() : null;
    if (style == null) {
      style =  new ModelViewWithParisStyleApplier.StyleBuilder().addDefault().build();
      parisStyleReference_default = new WeakReference<>(style);
    }
    return style(style);
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see ModelViewWithParis#setValue(int)
   */
  public ModelViewWithParisModel_ value(int value) {
    onMutation();
    this.value_Int = value;
    return this;
  }

  public int value() {
    return value_Int;
  }

  /**
   * <i>Optional</i>: Default value is 0
   *
   * @see ModelViewWithParis#setDelegatedProperty(int)
   */
  public ModelViewWithParisModel_ delegatedProperty(int delegatedProperty) {
    onMutation();
    this.delegatedProperty_Int = delegatedProperty;
    return this;
  }

  public int delegatedProperty() {
    return delegatedProperty_Int;
  }

  @Override
  public ModelViewWithParisModel_ id(long p0) {
    super.id(p0);
    return this;
  }

  @Override
  public ModelViewWithParisModel_ id(@Nullable Number... p0) {
    super.id(p0);
    return this;
  }

  @Override
  public ModelViewWithParisModel_ id(long p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public ModelViewWithParisModel_ id(@Nullable CharSequence p0) {
    super.id(p0);
    return this;
  }

  @Override
  public ModelViewWithParisModel_ id(@Nullable CharSequence p0, @Nullable CharSequence... p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public ModelViewWithParisModel_ id(@Nullable CharSequence p0, long p1) {
    super.id(p0, p1);
    return this;
  }

  @Override
  public ModelViewWithParisModel_ layout(@LayoutRes int p0) {
    throw new UnsupportedOperationException("Layout resources are unsupported with programmatic views.");
  }

  @Override
  public ModelViewWithParisModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback p0) {
    super.spanSizeOverride(p0);
    return this;
  }

  @Override
  public ModelViewWithParisModel_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelViewWithParisModel_ show(boolean p0) {
    super.show(p0);
    return this;
  }

  @Override
  public ModelViewWithParisModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    throw new UnsupportedOperationException("Layout resources are unsupported for views created programmatically.");
  }

  @Override
  public ModelViewWithParisModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    this.value_Int = 0;
    this.delegatedProperty_Int = 0;
    this.style = DEFAULT_PARIS_STYLE;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelViewWithParisModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelViewWithParisModel_ that = (ModelViewWithParisModel_) o;
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
    if ((value_Int != that.value_Int)) {
      return false;
    }
    if ((delegatedProperty_Int != that.delegatedProperty_Int)) {
      return false;
    }
    if ((style != null ? !style.equals(that.style) : that.style != null)) {
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
    _result = 31 * _result + value_Int;
    _result = 31 * _result + delegatedProperty_Int;
    _result = 31 * _result + (style != null ? style.hashCode() : 0);
    return _result;
  }

  @Override
  public String toString() {
    return "ModelViewWithParisModel_{" +
        "value_Int=" + value_Int +
        ", delegatedProperty_Int=" + delegatedProperty_Int +
        ", style=" + style +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
