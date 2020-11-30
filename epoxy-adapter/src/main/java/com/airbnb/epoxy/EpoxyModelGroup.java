package com.airbnb.epoxy;

import android.view.View;
import android.view.ViewParent;
import android.view.ViewStub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * An {@link EpoxyModel} that contains other models, and allows you to combine those models in
 * whatever view configuration you want.
 * <p>
 * The constructors take a list of models and a layout resource. The layout must have a viewgroup as
 * its top level view; it determines how the view of each model is laid out. There are two ways to
 * specify this
 * <p>
 * 1. Leave the viewgroup empty. The view for each model will be inflated and added in order. This
 * works fine if you don't need to include any other views, your model views don't need their layout
 * params changed, and your views don't need ids (eg for saving state).
 * <p>
 * Alternatively you can have nested view groups, with the innermost viewgroup given the id
 * "epoxy_model_group_child_container" to mark it as the viewgroup that should have the model views
 * added to it. The viewgroup marked with this id should be empty. This allows you to nest
 * viewgroups, such as a LinearLayout inside of a CardView.
 * <p>
 * 2. Include a {@link ViewStub} for each of the models in the list. There should be at least as
 * many view stubs as models. Extra stubs will be ignored. Each model will have its view replace the
 * stub in order of the view stub's position in the view group. That is, the view group's children
 * will be iterated through in order. The first view stub found will be used for the first model in
 * the models list, the second view stub will be used for the second model, and so on. A depth first
 * recursive search through nested viewgroups is done to find these viewstubs.
 * <p>
 * The layout can be of any ViewGroup subclass, and can have arbitrary other child views besides the
 * view stubs. It can arrange the views and view stubs however is needed.
 * <p>
 * Any layout param options set on the view stubs will be transferred to the corresponding model
 * view by default. If you want a model to keep the layout params from it's own layout resource you
 * can override {@link #useViewStubLayoutParams(EpoxyModel, int)}
 * <p>
 * If you want to override the id used for a model's view you can set {@link
 * ViewStub#setInflatedId(int)} via xml. That id will be transferred over to the view taking that
 * stub's place. This is necessary if you want your model to save view state, since without this the
 * model's view won't have an id to associate the saved state with.
 * <p>
 * By default this model inherits the same id as the first model in the list. Call {@link #id(long)}
 * to override that if needed.
 * <p>
 * When a model group is recycled, its child views are automatically recycled to a pool that is
 * shared with all other model groups in the activity. This enables model groups to more efficiently
 * manage their children. The shared pool is cleaned up when the activity is destroyed.
 */
@SuppressWarnings("rawtypes")
public class EpoxyModelGroup extends EpoxyModelWithHolder<ModelGroupHolder> {

  protected final List<EpoxyModel<?>> models;

  private boolean shouldSaveViewStateDefault = false;

  @Nullable
  private Boolean shouldSaveViewState = null;

  /**
   * @param layoutRes The layout to use with these models.
   * @param models    The models that will be used to bind the views in the given layout.
   */
  public EpoxyModelGroup(@LayoutRes int layoutRes, Collection<? extends EpoxyModel<?>> models) {
    this(layoutRes, new ArrayList<>(models));
  }

  /**
   * @param layoutRes The layout to use with these models.
   * @param models    The models that will be used to bind the views in the given layout.
   */
  public EpoxyModelGroup(@LayoutRes int layoutRes, EpoxyModel<?>... models) {
    this(layoutRes, new ArrayList<>(Arrays.asList(models)));
  }

  /**
   * @param layoutRes The layout to use with these models.
   * @param models    The models that will be used to bind the views in the given layout.
   */
  private EpoxyModelGroup(@LayoutRes int layoutRes, List<EpoxyModel<?>> models) {
    if (models.isEmpty()) {
      throw new IllegalArgumentException("Models cannot be empty");
    }

    this.models = models;
    layout(layoutRes);
    id(models.get(0).id());

    boolean saveState = false;
    for (EpoxyModel<?> model : models) {
      if (model.shouldSaveViewState()) {
        saveState = true;
        break;
      }
    }
    // By default we save view state if any of the models need to save state.
    shouldSaveViewStateDefault = saveState;
  }

  /**
   * Constructor use for DSL
   */
  protected EpoxyModelGroup() {
    models = new ArrayList<>();
    shouldSaveViewStateDefault = false;
  }

  /**
   * Constructor use for DSL
   */
  protected EpoxyModelGroup(@LayoutRes int layoutRes) {
    this();
    layout(layoutRes);
  }

  protected void addModel(@NonNull EpoxyModel<?> model) {
    // By default we save view state if any of the models need to save state.
    shouldSaveViewStateDefault |= model.shouldSaveViewState();
    models.add(model);
  }

  @CallSuper
  @Override
  public void bind(@NonNull ModelGroupHolder holder) {
    iterateModels(holder, new IterateModelsCallback() {
      @Override
      public void onModel(EpoxyModel model, EpoxyViewHolder viewHolder, int modelIndex) {
        setViewVisibility(model, viewHolder);
        viewHolder.bind(model, null, Collections.emptyList(), modelIndex);
      }
    });
  }

  @CallSuper
  @Override
  public void bind(@NonNull ModelGroupHolder holder, @NonNull final List<Object> payloads) {
    iterateModels(holder, new IterateModelsCallback() {
      @Override
      public void onModel(EpoxyModel model, EpoxyViewHolder viewHolder, int modelIndex) {
        setViewVisibility(model, viewHolder);
        viewHolder.bind(model, null, Collections.emptyList(), modelIndex);
      }
    });
  }

  @Override
  public void bind(@NonNull ModelGroupHolder holder, @NonNull EpoxyModel<?> previouslyBoundModel) {
    if (!(previouslyBoundModel instanceof EpoxyModelGroup)) {
      bind(holder);
      return;
    }

    final EpoxyModelGroup previousGroup = (EpoxyModelGroup) previouslyBoundModel;

    iterateModels(holder, new IterateModelsCallback() {
      @Override
      public void onModel(EpoxyModel model, EpoxyViewHolder viewHolder, int modelIndex) {
        setViewVisibility(model, viewHolder);

        if (modelIndex < previousGroup.models.size()) {
          EpoxyModel<?> previousModel = previousGroup.models.get(modelIndex);
          if (previousModel.id() == model.id()) {
            viewHolder.bind(model, previousModel, Collections.emptyList(), modelIndex);
            return;
          }
        }

        viewHolder.bind(model, null, Collections.emptyList(), modelIndex);
      }
    });
  }

  private static void setViewVisibility(EpoxyModel model, EpoxyViewHolder viewHolder) {
    if (model.isShown()) {
      viewHolder.itemView.setVisibility(View.VISIBLE);
    } else {
      viewHolder.itemView.setVisibility(View.GONE);
    }
  }

  @CallSuper
  @Override
  public void unbind(@NonNull ModelGroupHolder holder) {
    holder.unbindGroup();
  }

  @CallSuper
  @Override
  public void onViewAttachedToWindow(ModelGroupHolder holder) {
    iterateModels(holder, new IterateModelsCallback() {
      @Override
      public void onModel(EpoxyModel model, EpoxyViewHolder viewHolder, int modelIndex) {
        //noinspection unchecked
        model.onViewAttachedToWindow(viewHolder.objectToBind());
      }
    });
  }

  @CallSuper
  @Override
  public void onViewDetachedFromWindow(ModelGroupHolder holder) {
    iterateModels(holder, new IterateModelsCallback() {
      @Override
      public void onModel(EpoxyModel model, EpoxyViewHolder viewHolder, int modelIndex) {
        //noinspection unchecked
        model.onViewDetachedFromWindow(viewHolder.objectToBind());
      }
    });
  }

  private void iterateModels(ModelGroupHolder holder, IterateModelsCallback callback) {
    holder.bindGroupIfNeeded(this);
    int modelCount = models.size();

    for (int i = 0; i < modelCount; i++) {
      callback.onModel(models.get(i), holder.getViewHolders().get(i), i);
    }
  }

  private interface IterateModelsCallback {
    void onModel(EpoxyModel model, EpoxyViewHolder viewHolder, int modelIndex);
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    // Defaults to using the span size of the first model. Override this if you need to customize it
    return models.get(0).spanSize(totalSpanCount, position, itemCount);
  }

  @Override
  protected final int getDefaultLayout() {
    throw new UnsupportedOperationException(
        "You should set a layout with layout(...) instead of using this.");
  }

  @NonNull
  public EpoxyModelGroup shouldSaveViewState(boolean shouldSaveViewState) {
    onMutation();
    this.shouldSaveViewState = shouldSaveViewState;
    return this;
  }

  @Override
  public boolean shouldSaveViewState() {
    // By default state is saved if any of the models have saved state enabled.
    // Override this if you need custom behavior.
    if (shouldSaveViewState != null) {
      return shouldSaveViewState;
    } else {
      return shouldSaveViewStateDefault;
    }
  }

  /**
   * Whether the layout params set on the view stub for the given model should be carried over to
   * the model's view. Default is true
   * <p>
   * Set this to false if you want the layout params on the model's layout resource to be kept.
   *
   * @param model         The model who's view is being created
   * @param modelPosition The position of the model in the models list
   */
  protected boolean useViewStubLayoutParams(EpoxyModel<?> model, int modelPosition) {
    return true;
  }

  @Override
  protected final ModelGroupHolder createNewHolder(@NonNull ViewParent parent) {
    return new ModelGroupHolder(parent);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EpoxyModelGroup)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    EpoxyModelGroup that = (EpoxyModelGroup) o;

    return models.equals(that.models);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + models.hashCode();
    return result;
  }
}
