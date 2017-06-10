package com.airbnb.epoxy;

import android.view.View;
import android.widget.FrameLayout;

import org.robolectric.RuntimeEnvironment;

import java.util.Collections;
import java.util.List;

class ControllerLifecycleHelper {
  private EpoxyViewHolder viewHolder;

  void buildModelsAndBind(EpoxyController controller) {
    controller.requestModelBuild();
    bindModels(controller);
  }

  void bindModels(EpoxyController controller) {
    bindModels(controller.getAdapter());
  }

  void bindModels(BaseEpoxyAdapter adapter) {
    List<EpoxyModel<?>> models = adapter.getCurrentModels();
    for (int i = 0; i < models.size(); i++) {
      viewHolder = createViewHolder(adapter, i);
      adapter.onBindViewHolder(viewHolder, i);
    }
  }

  public View bindModel(EpoxyModel<?> model) {
    SimpleEpoxyController controller = new SimpleEpoxyController();
    controller.setModels(Collections.singletonList(model));
    bindModels(controller);
    return viewHolder.itemView;
  }

  static EpoxyViewHolder createViewHolder(BaseEpoxyAdapter adapter, int position) {
    int itemViewType = adapter.getItemViewType(position);
    return adapter.onCreateViewHolder(
        new FrameLayout(RuntimeEnvironment.application),
        itemViewType
    );
  }

  void recycleLastBoundModel(EpoxyController controller) {
    controller.getAdapter().onViewRecycled(viewHolder);
  }
}
