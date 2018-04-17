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
public class TestAfterBindPropsViewModel_ extends EpoxyModel<TestAfterBindPropsView> implements GeneratedModel<TestAfterBindPropsView>, TestAfterBindPropsViewModelBuilder {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(2);

  private OnModelBoundListener<TestAfterBindPropsViewModel_, TestAfterBindPropsView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<TestAfterBindPropsViewModel_, TestAfterBindPropsView> onModelUnboundListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0 */
  private boolean flag_Boolean = false;

  /**
   * Bitset index: 1 */
  private boolean flagSuper_Boolean = false;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final TestAfterBindPropsView object,
      final int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final TestAfterBindPropsView object) {
    super.bind(object);
    object.setFlagSuper(flagSuper_Boolean);
    object.setFlag(flag_Boolean);
  }

  @Override
  public void bind(final TestAfterBindPropsView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof TestAfterBindPropsViewModel_)) {
      bind(object);
      return;
    }
    TestAfterBindPropsViewModel_ that = (TestAfterBindPropsViewModel_) previousModel;
    super.bind(object);

    if ((flagSuper_Boolean != that.flagSuper_Boolean)) {
      object.setFlagSuper(flagSuper_Boolean);
    }

    if ((flag_Boolean != that.flag_Boolean)) {
      object.setFlag(flag_Boolean);
    }
  }

  @Override
  public void handlePostBind(final TestAfterBindPropsView object, int position) {
    if (onModelBoundListener_epoxyGeneratedModel != null) {
      onModelBoundListener_epoxyGeneratedModel.onModelBound(this, object, position);
    }
    validateStateHasNotChangedSinceAdded("The model was changed during the bind call.", position);
    object.afterFlagSet();
    object.afterFlagSetSuper();
  }

  /**
   * Register a listener that will be called when this model is bound to a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public TestAfterBindPropsViewModel_ onBind(OnModelBoundListener<TestAfterBindPropsViewModel_, TestAfterBindPropsView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(TestAfterBindPropsView object) {
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
  public TestAfterBindPropsViewModel_ onUnbind(OnModelUnboundListener<TestAfterBindPropsViewModel_, TestAfterBindPropsView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Optional</i>: Default value is false
   *
   * @see TestAfterBindPropsView#setFlag(boolean)
   */
  public TestAfterBindPropsViewModel_ flag(boolean flag) {
    assignedAttributes_epoxyGeneratedModel.set(0);
    onMutation();
    this.flag_Boolean = flag;
    return this;
  }

  public boolean flag() {
    return flag_Boolean;
  }

  /**
   * <i>Optional</i>: Default value is false
   *
   * @see TestAfterBindPropsView#setFlagSuper(boolean)
   */
  public TestAfterBindPropsViewModel_ flagSuper(boolean flagSuper) {
    assignedAttributes_epoxyGeneratedModel.set(1);
    onMutation();
    this.flagSuper_Boolean = flagSuper;
    return this;
  }

  public boolean flagSuper() {
    return flagSuper_Boolean;
  }

  @Override
  public TestAfterBindPropsViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ id(@NonNull Number... arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ id(@NonNull CharSequence arg0) {
    super.id(arg0);
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ id(@NonNull CharSequence arg0,
      @NonNull CharSequence... arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ id(@NonNull CharSequence arg0, long arg1) {
    super.id(arg0, arg1);
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ spanSizeOverride(@Nullable EpoxyModel.SpanSizeOverrideCallback arg0) {
    super.spanSizeOverride(arg0);
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public TestAfterBindPropsViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public TestAfterBindPropsViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.flag_Boolean = false;
    this.flagSuper_Boolean = false;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof TestAfterBindPropsViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    TestAfterBindPropsViewModel_ that = (TestAfterBindPropsViewModel_) o;
    if (((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if (((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null))) {
      return false;
    }
    if ((flag_Boolean != that.flag_Boolean)) {
      return false;
    }
    if ((flagSuper_Boolean != that.flagSuper_Boolean)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (flag_Boolean ? 1 : 0);
    result = 31 * result + (flagSuper_Boolean ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TestAfterBindPropsViewModel_{" +
        "flag_Boolean=" + flag_Boolean +
        ", flagSuper_Boolean=" + flagSuper_Boolean +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}