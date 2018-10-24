package com.airbnb.epoxy;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStub;

import com.airbnb.epoxy.EpoxyModelGroup.Holder;
import com.airbnb.viewmodeladapter.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

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
 * many view stubs as models. Extra stubs will be ignored. Each model will be inflated into a view
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
 * If an {@link EpoxyModelWithView} is used then the view created by that model will simply replace
 * its ViewStub instead of inflating the view stub with a resource. If layout params are set on the
 * view created by {@link EpoxyModelWithView#buildView(ViewGroup)} then those will be kept,
 * otherwise any layout params specified on the view stub will be transferred over as with normal
 * models.
 * <p>
 * If you want to override the id used for a model's view you can set {@link
 * ViewStub#setInflatedId(int)} via xml. That id will be transferred over to the view taking that
 * stub's place. This is necessary if you want your model to save view state, since without this the
 * model's view won't have an id to associate the saved state with.
 * <p>
 * By default this model inherits the same id as the first model in the list. Call {@link #id(long)}
 * to override that if needed.
 * <p>
 * The same number of models must always be used for a specific layout resource. This is because the
 * view stubs are only inflated once and then the view is recycled between groups with the same
 * layout. If you want to dynamically change what models are shown you can use {@link
 * EpoxyModel#hide()} to have the associated view be set to GONE.
 */
@SuppressWarnings("rawtypes")
public class EpoxyModelGroup extends EpoxyModelWithHolder<Holder>
    implements GeneratedModel<Holder> {

  protected final List<? extends EpoxyModel<?>> models;
  /** By default we save view state if any of the models need to save state. */
  private final boolean shouldSaveViewState;

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
  private EpoxyModelGroup(@LayoutRes int layoutRes, List<? extends EpoxyModel<?>> models) {
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

    shouldSaveViewState = saveState;
  }

  @Override
  public void handlePostBind(Holder groupHolder, final int position) {
    iterateModels(groupHolder, new IterateModelsCallback() {
      @Override
      public void onModel(EpoxyModel model, Object boundObject, View view, int modelIndex) {
        if (model instanceof GeneratedModel) {
          //noinspection unchecked
          ((GeneratedModel) model).handlePostBind(boundObject, position);
        }
      }
    });
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, Holder groupHolder, final int position) {
    iterateModels(groupHolder, new IterateModelsCallback() {
      @Override
      public void onModel(EpoxyModel model, Object boundObject, View view, int modelIndex) {
        if (model instanceof GeneratedModel) {
          //noinspection unchecked
          ((GeneratedModel) model).handlePreBind(holder, boundObject, position);
        }
      }
    });
  }

  @CallSuper
  @Override
  public void bind(@NonNull Holder holder) {
    iterateModels(holder, new IterateModelsCallback() {
      @Override
      public void onModel(EpoxyModel model, Object boundObject, View view, int modelIndex) {
        setViewVisibility(model, view);
        //noinspection unchecked
        model.bind(boundObject);
      }
    });
  }

  @CallSuper
  @Override
  public void bind(@NonNull Holder holder, @NonNull final List<Object> payloads) {
    iterateModels(holder, new IterateModelsCallback() {
      @Override
      public void onModel(EpoxyModel model, Object boundObject, View view, int modelIndex) {
        setViewVisibility(model, view);
        //noinspection unchecked
        model.bind(boundObject);
      }
    });
  }

  @Override
  public void bind(@NonNull Holder holder, @NonNull EpoxyModel<?> previouslyBoundModel) {
    if (!(previouslyBoundModel instanceof EpoxyModelGroup)) {
      bind(holder);
      return;
    }

    final EpoxyModelGroup previousGroup = (EpoxyModelGroup) previouslyBoundModel;
    if (previousGroup.models.size() != models.size()) {
      throw createInconsistentModelCountException();
    }

    iterateModels(holder, new IterateModelsCallback() {
      @Override
      public void onModel(EpoxyModel model, Object boundObject, View view, int modelIndex) {
        setViewVisibility(model, view);

        EpoxyModel<?> previousModel = previousGroup.models.get(modelIndex);
        if (previousModel.id() == model.id()) {
          //noinspection unchecked
          model.bind(boundObject, previousModel);
        } else {
          //noinspection unchecked
          model.bind(boundObject);
        }
      }
    });
  }

  private static void setViewVisibility(EpoxyModel model, View view) {
    if (model.isShown()) {
      view.setVisibility(View.VISIBLE);
    } else {
      view.setVisibility(View.GONE);
    }
  }

  @CallSuper
  @Override
  public void unbind(@NonNull Holder holder) {
    iterateModels(holder, new IterateModelsCallback() {
      @Override
      public void onModel(EpoxyModel model, Object boundObject, View view, int modelIndex) {
        //noinspection unchecked
        model.unbind(boundObject);
      }
    });
  }

  @CallSuper
  @Override
  public void onViewAttachedToWindow(Holder holder) {
    iterateModels(holder, new IterateModelsCallback() {
      @Override
      public void onModel(EpoxyModel model, Object boundObject, View view, int modelIndex) {
        //noinspection unchecked
        model.onViewAttachedToWindow(boundObject);
      }
    });
  }

  @CallSuper
  @Override
  public void onViewDetachedFromWindow(Holder holder) {
    iterateModels(holder, new IterateModelsCallback() {
      @Override
      public void onModel(EpoxyModel model, Object boundObject, View view, int modelIndex) {
        //noinspection unchecked
        model.onViewDetachedFromWindow(boundObject);
      }
    });
  }

  private void iterateModels(Holder holder, IterateModelsCallback callback) {
    int modelCount = models.size();
    if (modelCount != holder.views.size()) {
      throw createInconsistentModelCountException();
    }

    for (int i = 0; i < modelCount; i++) {
      EpoxyModel model = models.get(i);
      View view = holder.views.get(i);
      EpoxyHolder epoxyHolder = holder.holders.get(i);
      Object objectToBind = (model instanceof EpoxyModelWithHolder) ? epoxyHolder : view;

      callback.onModel(model, objectToBind, view, i);
    }
  }

  private RuntimeException createInconsistentModelCountException() {
    return new IllegalStateException(
        "The number of models used in this group has changed. The model count must remain "
            + "constant if the same layout resource is used. If you need to change which models"
            + " are shown you can call EpoxyMode#hide() to have a model's view hidden, or use a"
            + " different layout resource for the group.");
  }

  private interface IterateModelsCallback {
    void onModel(EpoxyModel model, Object boundObject, View view, int modelIndex);
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    // Defaults to using the span size of the first model. Override this if you need to customize it
    return models.get(0).getSpanSizeInternal(totalSpanCount, position, itemCount);
  }

  @Override
  protected final int getDefaultLayout() {
    throw new UnsupportedOperationException(
        "You should set a layout with layout(...) instead of using this.");
  }

  @Override
  public boolean shouldSaveViewState() {
    // By default state is saved if any of the models have saved state enabled.
    // Override this if you need custom behavior.
    return shouldSaveViewState;
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
  protected final Holder createNewHolder() {
    return new Holder(this);
  }

  public static class Holder extends EpoxyHolder {
    // We use the model group that was used to create the view holder for initializing the view.
    // We release the reference after the viewholder is initialized.
    private EpoxyModelGroup initializingModelGroup;
    private List<View> views;
    private List<EpoxyHolder> holders;
    private ViewGroup rootView;

    public Holder(@NonNull EpoxyModelGroup initializingModelGroup) {
      this.initializingModelGroup = initializingModelGroup;
    }

    /**
     * Get the root view group (aka
     * {@link androidx.recyclerview.widget.RecyclerView.ViewHolder#itemView}.
     * You can override {@link EpoxyModelGroup#bind(Holder)} and use this method to make custom
     * changes to the root view.
     */
    public ViewGroup getRootView() {
      return rootView;
    }

    @Override
    protected void bindView(@NonNull View itemView) {
      if (!(itemView instanceof ViewGroup)) {
        throw new IllegalStateException(
            "The layout provided to EpoxyModelGroup must be a ViewGroup");
      }
      rootView = (ViewGroup) itemView;
      ViewGroup childContainer = findChildContainer(rootView);

      List<? extends EpoxyModel<?>> models = initializingModelGroup.models;
      int modelCount = models.size();

      views = new ArrayList<>(modelCount);
      holders = new ArrayList<>(modelCount);

      boolean useViewStubs = childContainer.getChildCount() != 0;
      for (int i = 0; i < models.size(); i++) {
        EpoxyModel model = models.get(i);
        View view;
        if (useViewStubs) {
          view = replaceNextViewStub(childContainer, model,
              initializingModelGroup.useViewStubLayoutParams(model, i));
        } else {
          view = createAndAddView(childContainer, model);
        }

        if (model instanceof EpoxyModelWithHolder) {
          EpoxyHolder holder = ((EpoxyModelWithHolder) model).createNewHolder();
          holder.bindView(view);
          holders.add(holder);
        } else {
          holders.add(null);
        }

        views.add(view);
      }

      initializingModelGroup = null;
    }

    /**
     * By default the outermost viewgroup is used as the container that views are added to. However,
     * users can specify a different, nested view group to use as the child container by marking it
     * with a special id.
     */
    private ViewGroup findChildContainer(ViewGroup outermostRoot) {
      View customRoot = outermostRoot.findViewById(R.id.epoxy_model_group_child_container);

      if (customRoot instanceof ViewGroup) {
        return (ViewGroup) customRoot;
      }

      return outermostRoot;
    }

    private View createAndAddView(ViewGroup groupView, EpoxyModel<?> model) {
      View modelView = model.buildView(groupView);
      LayoutParams modelLayoutParams = modelView.getLayoutParams();
      if (modelLayoutParams != null) {
        groupView.addView(modelView, modelLayoutParams);
      } else {
        groupView.addView(modelView);
      }
      return modelView;
    }

    private View replaceNextViewStub(ViewGroup groupView, EpoxyModel<?> model,
        boolean useStubLayoutParams) {
      ViewStubData stubData = getNextViewStubPosition(groupView);
      if (stubData == null) {
        throw new IllegalStateException(
            "Your layout should provide a ViewStub for each model to be inflated into.");
      }

      stubData.viewGroup.removeView(stubData.viewStub);
      View modelView = model.buildView(stubData.viewGroup);

      // Carry over the stub id manually since we aren't inflating via the stub
      int inflatedId = stubData.viewStub.getInflatedId();
      if (inflatedId != View.NO_ID) {
        modelView.setId(inflatedId);
      }

      LayoutParams modelLayoutParams = modelView.getLayoutParams();
      if (useStubLayoutParams) {
        stubData.viewGroup
            .addView(modelView, stubData.position, stubData.viewStub.getLayoutParams());
      } else if (modelLayoutParams != null) {
        stubData.viewGroup.addView(modelView, stubData.position, modelLayoutParams);
      } else {
        stubData.viewGroup.addView(modelView, stubData.position);
      }

      return modelView;
    }

    private ViewStubData getNextViewStubPosition(ViewGroup viewGroup) {
      int childCount = viewGroup.getChildCount();
      for (int i = 0; i < childCount; i++) {
        View child = viewGroup.getChildAt(i);

        if (child instanceof ViewGroup) {
          ViewStubData nestedResult = getNextViewStubPosition((ViewGroup) child);
          if (nestedResult != null) {
            return nestedResult;
          }
        } else if (child instanceof ViewStub) {
          return new ViewStubData(viewGroup, (ViewStub) child, i);
        }
      }

      return null;
    }
  }

  private static class ViewStubData {

    private final ViewGroup viewGroup;
    private final ViewStub viewStub;
    private final int position;

    private ViewStubData(ViewGroup viewGroup, ViewStub viewStub, int position) {
      this.viewGroup = viewGroup;
      this.viewStub = viewStub;
      this.position = position;
    }
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
