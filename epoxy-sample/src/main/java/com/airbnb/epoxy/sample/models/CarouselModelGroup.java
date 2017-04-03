package com.airbnb.epoxy.sample.models;

import android.support.v7.widget.RecyclerView.RecycledViewPool;

import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelGroup;
import com.airbnb.epoxy.R;
import com.airbnb.epoxy.sample.CarouselData;
import com.airbnb.epoxy.sample.ColorData;
import com.airbnb.epoxy.sample.SampleController.AdapterCallbacks;

import java.util.ArrayList;
import java.util.List;

public class CarouselModelGroup extends EpoxyModelGroup {
  public CarouselModelGroup(CarouselData carousel, AdapterCallbacks callbacks,
      RecycledViewPool recycledViewPool) {
    super(R.layout.model_carousel_group, buildModels(carousel, callbacks, recycledViewPool));
    id(carousel.getId());
  }

  private static List<EpoxyModel> buildModels(CarouselData carousel, AdapterCallbacks callbacks,
      RecycledViewPool recycledViewPool) {
    List<ColorData> colors = carousel.getColors();
    ArrayList<EpoxyModel> models = new ArrayList<>();

    models.add(new ButtonModel_()
        .text(R.string.button_add)
        .clickListener(v -> callbacks.onAddColorToCarouselClicked(carousel)));

    models.add(new ButtonModel_()
        .text(R.string.button_change)
        .clickListener(v -> callbacks.onChangeCarouselColorsClicked(carousel))
        .show(colors.size() > 0));

    models.add(new ButtonModel_()
        .text(R.string.button_shuffle)
        .clickListener(v -> callbacks.onShuffleCarouselColorsClicked(carousel))
        .show(colors.size() > 1));

    models.add(new ButtonModel_()
        .text(R.string.button_clear)
        .clickListener(v -> callbacks.onClearCarouselClicked(carousel))
        .show(colors.size() > 0));

    List<ColorModel_> colorModels = new ArrayList<>();
    for (ColorData colorData : colors) {
      colorModels.add(new ColorModel_()
          .id(colorData.getId(), carousel.getId())
          .color(colorData.getColorInt()));
    }

    models.add(new CarouselModel_()
        .recycledViewPool(recycledViewPool)
        .models(colorModels));

    return models;
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }
}
