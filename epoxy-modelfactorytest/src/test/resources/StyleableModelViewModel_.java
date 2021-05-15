package com.airbnb.epoxy;

import android.os.AsyncTask;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.airbnb.paris.StyleApplierUtils;
import com.airbnb.paris.styles.Style;
import com.airbnb.viewmodeladapter.R;
import java.lang.AssertionError;
import java.lang.CharSequence;
import java.lang.IllegalArgumentException;
import java.lang.IllegalStateException;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.String;
import java.lang.UnsupportedOperationException;
import java.lang.ref.WeakReference;
import java.util.BitSet;
import java.util.Objects;

/**
 * Generated file. Do not modify! */
public class StyleableModelViewModel_ extends EpoxyModel<StyleableModelView> implements GeneratedModel<StyleableModelView>, StyleableModelViewModelBuilder {
  private static final Style DEFAULT_PARIS_STYLE = new StyleableModelViewStyleApplier.StyleBuilder().addDefault().build();

  private static WeakReference<Style> parisStyleReference_default;

  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(2);

  private OnModelBoundListener<StyleableModelViewModel_, StyleableModelView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<StyleableModelViewModel_, StyleableModelView> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelVisibilityStateChangedListener<StyleableModelViewModel_, StyleableModelView> onModelVisibilityStateChangedListener_epoxyGeneratedModel;

  private OnModelVisibilityChangedListener<StyleableModelViewModel_, StyleableModelView> onModelVisibilityChangedListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0 */
  @NonNull
  private String title_String;

  /**
   * Bitset index: 1 */
  private Style style = DEFAULT_PARIS_STYLE;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
    if (!assignedAttributes_epoxyGeneratedModel.get(0)) {
    	throw new IllegalStateException("A value is required for setTitle");
    }
  }

  @Override
  protected int getViewType() {
    return 0;
  }

  @Override
  public StyleableModelView buildView(ViewGroup parent) {
    StyleableModelView v = new StyleableModelView(parent.getContext());
    v.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT));
    return v;
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final StyleableModelView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
    if (!Objects.equals(style, object.getTag(R.id.epoxy_saved_view_style))) {
      AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
        public void run() {
          try {
            StyleApplierUtils.Companion.assertSameAttributes(new StyleableModelViewStyleApplier(object), style, DEFAULT_PARIS_STYLE);
          }
          catch(AssertionError e) {
            throw new IllegalStateException("StyleableModelViewModel_ model at position " + position + " has an invalid style:\n\n" + e.getMessage());
          }
        }
      } );
    }
  }

  @Override
  public void bind(final StyleableModelView object) {

    if (!Objects.equals(style, object.getTag(R.id.epoxy_saved_view_style))) {
      StyleableModelViewStyleApplier styleApplier = new StyleableModelViewStyleApplier(object);
      styleApplier.apply(style);
      object.setTag(R.id.epoxy_saved_view_style, style);
    }
    super.bind(object);
    object.setTitle(title_String);
  }

  @Override
  public void bind(final StyleableModelView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof StyleableModelViewModel_)) {
      bind(object);
      return;
    }
    StyleableModelViewModel_ that = (StyleableModelViewModel_) previousModel;

    if (!Objects.equals(style, that.style)) {
      StyleableModelViewStyleApplier styleApplier = new StyleableModelViewStyleApplier(object);
      styleApplier.apply(style);
      object.setTag(R.id.epoxy_saved_view_style, style);
    }
    super.bind(object);

    if ((title_String != null ? !title_String.equals(that.title_String) : that.title_String != null)) {
      object.setTitle(title_String);
    }
  }

  @Override
  public void handlePostBind(final StyleableModelView object, int position) {
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
  public StyleableModelViewModel_ onBind(
      OnModelBoundListener<StyleableModelViewModel_, StyleableModelView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(StyleableModelView object) {
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
  public StyleableModelViewModel_ onUnbind(
      OnModelUnboundListener<StyleableModelViewModel_, StyleableModelView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityStateChanged(int visibilityState, final StyleableModelView object) {
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
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public StyleableModelViewModel_ onVisibilityStateChanged(
      OnModelVisibilityStateChangedListener<StyleableModelViewModel_, StyleableModelView> listener) {
    onMutation();
    this.onModelVisibilityStateChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void onVisibilityChanged(float percentVisibleHeight, float percentVisibleWidth,
      int visibleHeight, int visibleWidth, final StyleableModelView object) {
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
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public StyleableModelViewModel_ onVisibilityChanged(
      OnModelVisibilityChangedListener<StyleableModelViewModel_, StyleableModelView> listener) {
    onMutation();
    this.onModelVisibilityChangedListener_epoxyGeneratedModel = listener;
    return this;
  }

  public StyleableModelViewModel_ style(Style style) {
    assignedAttributes_epoxyGeneratedModel.set(1);
    onMutation();
    this.style = style;
    return this;
  }

  public StyleableModelViewModel_ styleBuilder(
      StyleBuilderCallback<StyleableModelViewStyleApplier.StyleBuilder> builderCallback) {
    StyleableModelViewStyleApplier.StyleBuilder builder = new StyleableModelViewStyleApplier.StyleBuilder();
    builderCallback.buildStyle(builder.addDefault());
    return style(builder.build());
  }

  /**
   *  Empty style  */
  public StyleableModelViewModel_ withDefaultStyle() {
    Style style = parisStyleReference_default != null ? parisStyleReference_default.get() : null;
    if (style == null) {
      style =  new StyleableModelViewStyleApplier.StyleBuilder().addDefault().build();
      parisStyleReference_default = new WeakReference<>(style);
    }
    return style(style);
  }

  /**
   * <i>Required.</i>
   *
   * @see StyleableModelView#setTitle(String)
   */
  public StyleableModelViewModel_ title(@NonNull String title) {
    if (title == null) {
      throw new IllegalArgumentException("title cannot be null");
    }
    assignedAttributes_epoxyGeneratedModel.set(0);
    onMutation();
    this.title_String = title;
    return this;
  }

  @NonNull
  public String title() {
    return title_String;
  }

  @Override
  public StyleableModelViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public StyleableModelViewModel_ id(@Nullable Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public StyleableModelViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public StyleableModelViewModel_ id(@Nullable CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public StyleableModelViewModel_ id(@Nullable CharSequence arg0, @Nullable CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public StyleableModelViewModel_ id(@Nullable CharSequence arg0, long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public StyleableModelViewModel_ layout(@LayoutRes int arg0) {
    throw new UnsupportedOperationException("Layout resources are unsupported with programmatic views.");
  }

  @Override
  public StyleableModelViewModel_ spanSizeOverride(
      @Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public StyleableModelViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public StyleableModelViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public StyleableModelViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    throw new UnsupportedOperationException("Layout resources are unsupported for views created programmatically.");
  }

  @Override
  public StyleableModelViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    onModelVisibilityStateChangedListener_epoxyGeneratedModel = null;
    onModelVisibilityChangedListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.title_String = null;
    this.style = DEFAULT_PARIS_STYLE;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof StyleableModelViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    StyleableModelViewModel_ that = (StyleableModelViewModel_) o;
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
    if ((title_String != null ? !title_String.equals(that.title_String) : that.title_String != null)) {
      return false;
    }
    if ((style != null ? !style.equals(that.style) : that.style != null)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelVisibilityStateChangedListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelVisibilityChangedListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (title_String != null ? title_String.hashCode() : 0);
    result = 31 * result + (style != null ? style.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "StyleableModelViewModel_{" +
        "title_String=" + title_String +
        ", style=" + style +
        "}" + super.toString();
  }

  public static StyleableModelViewModel_ from(ModelProperties properties) {
    StyleableModelViewModel_ model = new StyleableModelViewModel_();
    model.id(properties.getId());
    if (properties.has("title")) {
      model.title(properties.getString("title"));
    }
    Style style = properties.getStyle();
    if (style != null) {
      model.style(style);
    }
    return model;
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
