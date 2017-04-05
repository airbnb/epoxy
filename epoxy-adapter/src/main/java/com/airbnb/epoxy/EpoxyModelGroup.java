package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStub;

import com.airbnb.epoxy.EpoxyModelGroup.Holder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * An {@link EpoxyModel} that contains other models, and allows you to combine those models in
 * whatever view configuration you want.
 * <p>
 * The constructors take a list of models and a layout file. The layout file should specify a
 * ViewGroup that contains a {@link ViewStub} for each of the models in the list. There should be at
 * least as many view stubs as models. Extra stubs will be ignored. Each model will be inflated into
 * a view stub in order of the view stub's position in the view group. That is, the view group's
 * children will be iterated through in order. The first view stub found will be used for the first
 * model in the models list, the second view stub will be used for the second model, and so on.
 * <p>
 * The layout can be of any ViewGroup subclass, and can have arbitrary other child views besides the
 * view stubs. It can arrange the views and view stubs however is needed.
 * <p>
 * Any layout param options set on the view stubs will be transferred to the corresponding model
 * view.
 * <p>
 * If an {@link EpoxyModelWithView} is used then the view created by that model will simply replace
 * its ViewStub instead of inflating the view stub with a resource. If layout params are set on the
 * view created by {@link EpoxyModelWithView#buildView(ViewGroup)} then those will be kept,
 * otherwise any layout params specified on the view stub will be transferred over as with normal
 * models.
 * <p>
 * By default this model inherits the same id as the first model in the list. Call {@link #id(long)}
 * to override that if needed.
 */
@SuppressWarnings("rawtypes")
public class EpoxyModelGroup extends EpoxyModelWithHolder<Holder> {

  protected final List<EpoxyModel> models;
  /** By default we save view state if any of the models need to save state. */
  private final boolean shouldSaveViewState;

  /**
   * @param layoutRes The layout to use with these models.
   * @param models    The models that will be used to bind the views in the given layout.
   */
  public EpoxyModelGroup(@LayoutRes int layoutRes, Collection<EpoxyModel> models) {
    this(layoutRes, new ArrayList<>(models));
  }

  /**
   * @param layoutRes The layout to use with these models.
   * @param models    The models that will be used to bind the views in the given layout.
   */
  public EpoxyModelGroup(@LayoutRes int layoutRes, EpoxyModel... models) {
    this(layoutRes, new ArrayList<>(Arrays.asList(models)));
  }

  /**
   * @param layoutRes The layout to use with these models.
   * @param models    The models that will be used to bind the views in the given layout.
   */
  private EpoxyModelGroup(@LayoutRes int layoutRes, List<EpoxyModel> models) {
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
  public final void bind(Holder holder) {
    iterateModels(holder, new IterateModelsCallback() {
      @Override
      public void onModel(EpoxyModel model, Object boundObject, View view) {
        setViewVisibility(model, view);
        //noinspection unchecked
        model.bind(boundObject);
      }
    });
  }

  @Override
  public final void bind(Holder holder, final List<Object> payloads) {
    iterateModels(holder, new IterateModelsCallback() {
      @Override
      public void onModel(EpoxyModel model, Object boundObject, View view) {
        setViewVisibility(model, view);
        //noinspection unchecked
        model.bind(boundObject, payloads);
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

  @Override
  public final void unbind(Holder holder) {
    iterateModels(holder, new IterateModelsCallback() {
      @Override
      public void onModel(EpoxyModel model, Object boundObject, View view) {
        //noinspection unchecked
        model.unbind(boundObject);
      }
    });
  }

  @Override
  public void onViewAttachedToWindow(Holder holder) {
    iterateModels(holder, new IterateModelsCallback() {
      @Override
      public void onModel(EpoxyModel model, Object boundObject, View view) {
        //noinspection unchecked
        model.onViewAttachedToWindow(boundObject);
      }
    });
  }

  @Override
  public void onViewDetachedFromWindow(Holder holder) {
    iterateModels(holder, new IterateModelsCallback() {
      @Override
      public void onModel(EpoxyModel model, Object boundObject, View view) {
        //noinspection unchecked
        model.onViewDetachedFromWindow(boundObject);
      }
    });
  }

  private void iterateModels(Holder holder, IterateModelsCallback callback) {
    int modelCount = models.size();
    for (int i = 0; i < modelCount; i++) {
      EpoxyModel model = models.get(i);
      View view = holder.views.get(i);
      EpoxyHolder epoxyHolder = holder.holders.get(i);
      Object objectToBind = (model instanceof EpoxyModelWithHolder) ? epoxyHolder : view;

      callback.onModel(model, objectToBind, view);
    }
  }

  private interface IterateModelsCallback {
    void onModel(EpoxyModel model, Object boundObject, View view);
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    // Defaults to using the span size of the first model. Override this if you need to customize it
    return models.get(0).getSpanSize(totalSpanCount, position, itemCount);
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

  @Override
  protected final Holder createNewHolder() {
    return new Holder();
  }

  protected class Holder extends EpoxyHolder {
    private List<View> views;
    private List<EpoxyHolder> holders;

    @Override
    protected void bindView(View itemView) {
      if (!(itemView instanceof ViewGroup)) {
        throw new IllegalStateException(
            "The layout provided to EpoxyModelGroup must be a ViewGroup");
      }
      ViewGroup groupView = (ViewGroup) itemView;

      int modelCount = models.size();
      views = new ArrayList<>(modelCount);
      holders = new ArrayList<>(modelCount);

      for (EpoxyModel model : models) {
        View view = createAndAddView(groupView, model);

        if (model instanceof EpoxyModelWithHolder) {
          EpoxyHolder holder = ((EpoxyModelWithHolder) model).createNewHolder();
          holder.bindView(view);
          holders.add(holder);
        } else {
          holders.add(null);
        }

        views.add(view);
      }
    }

    private View createAndAddView(ViewGroup groupView, EpoxyModel<?> model) {
      ViewStubData stubData = getNextViewStubPosition(groupView);
      if (stubData == null) {
        throw new IllegalStateException(
            "Your layout should provide a ViewStub for each model to be inflated into.");
      }

      if (model instanceof EpoxyModelWithView) {
        stubData.viewGroup.removeView(stubData.viewStub);

        View modelView = model.buildView(groupView);
        LayoutParams modelLayoutParams = modelView.getLayoutParams();
        ViewGroup.LayoutParams viewStubLayoutParams = stubData.viewStub.getLayoutParams();
        // Carry over the stub id manually since we aren't actually inflating it
        modelView.setId(stubData.viewStub.getInflatedId());

        // Use layout params off the view created by the model if they exist.
        // Otherwise we fallback to any layout params on the view stub.
        // And lastly we fallback to no layout params, in which case default params are applied.
        if (modelLayoutParams != null) {
          groupView.addView(modelView, stubData.position, modelLayoutParams);
        } else if (viewStubLayoutParams != null) {
          groupView.addView(modelView, stubData.position, viewStubLayoutParams);
        } else {
          groupView.addView(modelView, stubData.position);
        }

        return modelView;
      } else {
        stubData.viewStub.setLayoutResource(model.getLayout());
        return stubData.viewStub.inflate();
      }
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
