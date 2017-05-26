package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import java.lang.CharSequence;
import java.lang.Number;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.BitSet;

/**
 * Generated file. Do not modify! */
public class TestNullStringOverloadsViewModel_ extends EpoxyModel<TestNullStringOverloadsView> implements GeneratedModel<TestNullStringOverloadsView> {
  private final BitSet assignedAttributes_epoxyGeneratedModel = new BitSet(2);

  private OnModelBoundListener<TestNullStringOverloadsViewModel_, TestNullStringOverloadsView> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<TestNullStringOverloadsViewModel_, TestNullStringOverloadsView> onModelUnboundListener_epoxyGeneratedModel;

  /**
   * Bitset index: 0 */
  @Nullable
  private String title_String = null;

  /**
   * Bitset index: 1 */
  @StringRes
  private int title_Int;

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final TestNullStringOverloadsView object,
      int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
  }

  @Override
  public void bind(final TestNullStringOverloadsView object) {
    super.bind(object);
    if (assignedAttributes_epoxyGeneratedModel.get(0)) {
      object.setTitle(title_String);
    }
    else if (assignedAttributes_epoxyGeneratedModel.get(1)) {
      object.setTitle(object.getContext().getString(title_Int));
    }
    else {
      this.title_String = null;
    }
  }

  @Override
  public void bind(final TestNullStringOverloadsView object, EpoxyModel previousModel) {
    if (!(previousModel instanceof TestNullStringOverloadsViewModel_)) {
      bind(object);
      return;
    }
    TestNullStringOverloadsViewModel_ that = (TestNullStringOverloadsViewModel_) previousModel;

    if (assignedAttributes_epoxyGeneratedModel.equals(that.assignedAttributes_epoxyGeneratedModel)) {
      if (assignedAttributes_epoxyGeneratedModel.get(0)) {
        if (title_String != null ? !title_String.equals(that.title_String) : that.title_String != null) {
          object.setTitle(title_String);
        }
      }
       else if (assignedAttributes_epoxyGeneratedModel.get(1)) {
        if (title_Int != that.title_Int) {
          object.setTitle(object.getContext().getString(title_Int));
        }
      }
    }
    else {
      if (assignedAttributes_epoxyGeneratedModel.get(0) && !that.assignedAttributes_epoxyGeneratedModel.get(0)) {
        object.setTitle(title_String);
      }
       else if (assignedAttributes_epoxyGeneratedModel.get(1) && !that.assignedAttributes_epoxyGeneratedModel.get(1)) {
        object.setTitle(object.getContext().getString(title_Int));
      }
      else {
        object.setTitle(null);
      }
    }
  }

  @Override
  public void handlePostBind(final TestNullStringOverloadsView object, int position) {
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
  public TestNullStringOverloadsViewModel_ onBind(OnModelBoundListener<TestNullStringOverloadsViewModel_, TestNullStringOverloadsView> listener) {
    onMutation();
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(TestNullStringOverloadsView object) {
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
  public TestNullStringOverloadsViewModel_ onUnbind(OnModelUnboundListener<TestNullStringOverloadsViewModel_, TestNullStringOverloadsView> listener) {
    onMutation();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * <i>Optional</i>: Default value is null
   *
   * @see TestNullStringOverloadsView#setTitle(String)
   */
  public TestNullStringOverloadsViewModel_ title(@Nullable String title) {
    assignedAttributes_epoxyGeneratedModel.set(0);
    assignedAttributes_epoxyGeneratedModel.clear(1);
    this.title_Int = 0;
    onMutation();
    this.title_String = title;
    return this;
  }

  @Nullable
  public String titleString() {
    return title_String;
  }

  /**
   * <i>Optional</i>: Default value is null
   *
   * @see TestNullStringOverloadsView#setTitle(String)
   */
  public TestNullStringOverloadsViewModel_ title(@StringRes int title) {
    assignedAttributes_epoxyGeneratedModel.set(1);
    assignedAttributes_epoxyGeneratedModel.clear(0);
    this.title_String = null;
    onMutation();
    this.title_Int = title;
    if (title == 0) {
      // Since this is an optional attribute we'll use the default value instead by not marking this as attribute as set.
      assignedAttributes_epoxyGeneratedModel.clear(1);
    }
    return this;
  }

  @StringRes
  public int titleInt() {
    return title_Int;
  }

  @Override
  public TestNullStringOverloadsViewModel_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ id(Number... ids) {
    super.id(ids);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ id(long id1, long id2) {
    super.id(id1, id2);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ spanSizeCallback(@Nullable EpoxyModel.SpanSizeCallback arg0) {
    super.spanSizeCallback(arg0);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ show() {
    super.show();
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public TestNullStringOverloadsViewModel_ hide() {
    super.hide();
    return this;
  }

  @Override
  @LayoutRes
  protected int getDefaultLayout() {
    return 1;
  }

  @Override
  public TestNullStringOverloadsViewModel_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    assignedAttributes_epoxyGeneratedModel.clear();
    this.title_String = null;
    this.title_Int = 0;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof TestNullStringOverloadsViewModel_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    TestNullStringOverloadsViewModel_ that = (TestNullStringOverloadsViewModel_) o;
    if ((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if ((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if (title_String != null ? !title_String.equals(that.title_String) : that.title_String != null) {
      return false;
    }
    if (title_Int != that.title_Int) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (title_String != null ? title_String.hashCode() : 0);
    result = 31 * result + title_Int;
    return result;
  }

  @Override
  public String toString() {
    return "TestNullStringOverloadsViewModel_{" +
        "title_String=" + title_String +
        ", title_Int=" + title_Int +
        "}" + super.toString();
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}