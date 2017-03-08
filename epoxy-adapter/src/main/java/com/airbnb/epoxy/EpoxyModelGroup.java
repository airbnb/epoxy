package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.airbnb.epoxy.EpoxyModelGroup.Holder;
import com.airbnb.viewmodeladapter.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * An {@link EpoxyModel} that contains other models, and allows you to combine those models in
 * whatever view configuration you want.
 * <p>
 * The constructors take a list of models and a layout file. The layout file should contain a {@link
 * ViewStub} for each of the models in the list. Each view stub must have a specific view id to
 * associate it with the model it belongs to. These ids follow the form
 * R.id.epoxy_model_group_view_stub_1, R.id.epoxy_model_group_view_stub_2,
 * R.id.epoxy_model_group_view_stub_3, etc.
 * <p>
 * R.id.epoxy_model_group_view_stub_1 maps to the first model (index 0 of the provided list),
 * R.id.epoxy_model_group_view_stub_2 maps to the second model, and so on.
 * <p>
 * The layout can be of any view type, and it can arrange the view stubs however is needed.
 * <p>
 * A maximum of {@link #MAX_MODELS_SUPPORTED} can be added to a group.
 * <p>
 * By default this model inherits the same id as the first model in the list. Call {@link #id(long)}
 * to override that if needed.
 * <p>
 * Any layout param options set on the view stubs will be transferred to the corresponding model
 * view.
 */
@SuppressWarnings("rawtypes")
public class EpoxyModelGroup extends EpoxyModelWithHolder<Holder> {
  /**
   * We have a hard cap on number of models since we have hardcoded ids for simplicity, clarity, and
   * speed.
   */
  private static final int MAX_MODELS_SUPPORTED = 5;

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

    if (models.size() > MAX_MODELS_SUPPORTED) {
      throw new IllegalArgumentException(
          "Too many models. Only " + MAX_MODELS_SUPPORTED + " models are supported");
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
      // TODO: (eli_hart 3/8/17) better error handling and messaging around the viewgroup
      // expectation
      ViewGroup groupView = (ViewGroup) itemView;

      int modelCount = models.size();
      views = new ArrayList<>(modelCount);
      holders = new ArrayList<>(modelCount);

      for (int i = 0; i < modelCount; i++) {
        EpoxyModel<?> model = models.get(i);
        View view = createAndAddView(groupView, i, model);

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

    private View createAndAddView(ViewGroup groupView, int i, EpoxyModel<?> model) {
      ViewStub viewStub = getViewStub(groupView, i, model);

      // TODO: (eli_hart 3/8/17) Test this and update documentation
      if (model instanceof EpoxyModelWithView) {
        final int index = groupView.indexOfChild(viewStub);
        groupView.removeViewInLayout(viewStub);

        View modelView = model.buildView(groupView);
        final ViewGroup.LayoutParams layoutParams = viewStub.getLayoutParams();
        if (layoutParams != null) {
          groupView.addView(modelView, index, layoutParams);
        } else {
          groupView.addView(modelView, index);
        }

        return groupView;
      } else {
        viewStub.setLayoutResource(model.getLayout());
        return viewStub.inflate();
      }
    }

    private ViewStub getViewStub(View itemView, int i, EpoxyModel<?> model) {
      View stub = itemView.findViewById(getIdForIndex(i));

      if (stub == null) {
        throw new IllegalStateException(
            "The expected view for your model " + model + " at position " + i
                + " wasn't found. Are you using the correct stub id?");
      }

      if (stub instanceof ViewStub) {
        return (ViewStub) stub;
      }

      throw new IllegalStateException(
          "Your layout should provide a ViewStub. See the layout() method javadoc for more info.");
    }

    private int getIdForIndex(int modelIndex) {
      switch (modelIndex) {
        case 0:
          return R.id.epoxy_model_group_view_stub_1;
        case 1:
          return R.id.epoxy_model_group_view_stub_2;
        case 2:
          return R.id.epoxy_model_group_view_stub_3;
        case 3:
          return R.id.epoxy_model_group_view_stub_4;
        case 4:
          return R.id.epoxy_model_group_view_stub_5;
        default:
          throw new IllegalStateException(
              "No support for more than " + MAX_MODELS_SUPPORTED + " models");
      }
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
