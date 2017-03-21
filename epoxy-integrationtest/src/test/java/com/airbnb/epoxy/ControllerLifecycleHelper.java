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
    EpoxyControllerAdapter adapter = controller.getAdapter();

    List<EpoxyModel<?>> models = adapter.getCopyOfModels();
    for (int i = 0; i < models.size(); i++) {
      EpoxyModel<?> model = models.get(i);
      viewHolder =
          adapter.onCreateViewHolder(
              new FrameLayout(RuntimeEnvironment.application),
              model.getLayout()
          );

      adapter.onBindViewHolder(viewHolder, i);
    }
  }

  void recycleLastBoundModel(EpoxyController controller) {
    controller.getAdapter().onViewRecycled(viewHolder);
  }
}
