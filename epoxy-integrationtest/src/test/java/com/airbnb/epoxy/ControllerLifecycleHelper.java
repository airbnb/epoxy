package com.airbnb.epoxy;

import android.widget.FrameLayout;

import org.robolectric.RuntimeEnvironment;

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
      int itemViewType = adapter.getItemViewType(i);
      viewHolder =
          adapter.onCreateViewHolder(
              new FrameLayout(RuntimeEnvironment.application),
              itemViewType
          );

      adapter.onBindViewHolder(viewHolder, i);
    }
  }

  void recycleLastBoundModel(EpoxyController controller) {
    controller.getAdapter().onViewRecycled(viewHolder);
  }
}
