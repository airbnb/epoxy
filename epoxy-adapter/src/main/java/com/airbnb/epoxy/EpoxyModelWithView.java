package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

/**
 * A model that allows its view to be built programmatically instead of by inflating a layout
 * resource. Just implement {@link #buildView} so the adapter can create a new view for this model
 * when needed.
 * <p>
 * {@link #getViewType()} is used by the adapter to know how to reuse views for this model. If it is
 * left unimplemented then the generated model will include an implementation which returns a value
 * based on the generated model's name. This means that all models of that type should be able to
 * share the same view, but the view won't be shared with models of any other type.
 * <p>
 * The generated view type will be negative so that it cannot collide with values from layout
 * resources, which are used in normal Epoxy models. However, it is possible for generated view
 * types to collide if models from different libraries are used together, since the model processor
 * can only guarantee that view types are unique within a library. If you need to use {@link
 * EpoxyModelWithView} models from different libraries or modules together you can define a view
 * type manually to avoid this small collision chance. A good way to manually create value is by
 * creating an R.id. value in an ids resource while, which will guarantee a unique value.
 */
public abstract class EpoxyModelWithView<T extends View> extends EpoxyModel<T> {

  /**
   * Get the view type associated with this model's view. Any models with the same view type will
   * have views recycled between them.
   *
   * @see android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)
   */
  @Override
  protected abstract int getViewType();

  /**
   * Create and return a new instance of a view for this model. If no layout params are set on the
   * returned view then the RecyclerView will set default layout params on it, which set the size to
   * wrap_content.
   *
   * @param parent The parent viewgroup that the returned view will be added to. It can be used to
   *               create layout params for the new view.
   */
  @Override
  protected abstract T buildView(ViewGroup parent);

  @Override
  protected final int getDefaultLayout() {
    throw new UnsupportedOperationException(
        "Layout resources are unsupported. Views must be created with `buildView`");
  }

  @Override
  public final EpoxyModel<T> layout(@LayoutRes int layoutRes) {
    throw new UnsupportedOperationException(
        "Layout resources are unsupported. Views must be created with `buildView`");
  }
}
