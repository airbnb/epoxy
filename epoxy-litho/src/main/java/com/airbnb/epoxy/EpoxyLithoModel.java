package com.airbnb.epoxy;

import android.os.Looper;
import android.view.ViewGroup;

import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.ComponentLifecycle;
import com.facebook.litho.ComponentTree;
import com.facebook.litho.LithoView;

import java.util.List;

/**
 * Experiment support for Facebook's Litho library. Any litho component specs will automatically
 * have a model generated for them that extends this class.
 */
public abstract class EpoxyLithoModel<T extends ComponentLifecycle>
    extends EpoxyModelWithView<LithoView> {

  private Component<T> component;

  protected abstract Component<T> buildComponent(ComponentContext context);

  public Component<T> getComponent(ComponentContext context) {
    // TODO: (eli_hart 4/23/17) verify added to controller first?
    if (component == null) {
      // TODO: (eli_hart 4/23/17) reuse component context?
      component = buildComponent(context);
    }
    return component;
  }

  public void clearComponent() {
    // TODO: (eli_hart 4/23/17) Do this at some point to avoid leaking the context?
    component = null;
  }

  @Override
  public void bind(LithoView view) {
    Component<T> component = getComponent(view.getComponentContext());
    if (view.getComponentTree() == null) {
      view.setComponentTree(ComponentTree.create(view.getComponentContext(), component)
          .asyncStateUpdates(false) // does this need to be off?
          .layoutThreadLooper(Looper.getMainLooper())
          .incrementalMount(
              false) // this needs to be off for recyclerview to work. Should we manually
          // incrementally mount views in a scroll listener?
          .layoutDiffing(false) // should we use this?
          .build());
    }
    view.setComponent(component);
  }

  @Override
  public void bind(LithoView view, List<Object> payloads) {
    if (payloads.isEmpty()) {
      bind(view);
    } else {
      view.getComponentTree().setRoot(getComponent(view.getComponentContext()), true);
    }
  }

  @Override
  public void bind(LithoView view, EpoxyModel<?> previouslyBoundModel) {
    view.getComponentTree().setRoot(getComponent(view.getComponentContext()), true);
  }

  @Override
  public void onViewAttachedToWindow(LithoView view) {
//    view.rebind(); ?????
  }

  @Override
  public void onViewDetachedFromWindow(LithoView view) {
//    view.unbind(); ???
  }

  @Override
  public void unbind(LithoView view) {
    // The litho view is already unbound when it is detached from the window
  }

  @Override
  protected LithoView buildView(ViewGroup parent) {
    return new LithoView(parent.getContext());
  }
}
