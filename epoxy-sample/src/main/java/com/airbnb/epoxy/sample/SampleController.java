package com.airbnb.epoxy.sample;

import android.support.v7.widget.RecyclerView.RecycledViewPool;
import android.view.View;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.OnModelClickListener;
import com.airbnb.epoxy.R;
import com.airbnb.epoxy.TypedEpoxyController;
import com.airbnb.epoxy.sample.models.ButtonModel_;
import com.airbnb.epoxy.sample.models.CarouselModelGroup;
import com.airbnb.epoxy.sample.models.ColorModel_;
import com.airbnb.epoxy.sample.models.HeaderModel_;

import java.util.List;

public class SampleController extends TypedEpoxyController<List<CarouselData>> {
  public interface AdapterCallbacks {
    void onAddCarouselClicked();
    void onClearCarouselsClicked();
    void onShuffleCarouselsClicked();
    void onChangeAllColorsClicked();
    void onAddColorToCarouselClicked(CarouselData carousel);
    void onClearCarouselClicked(CarouselData carousel);
    void onShuffleCarouselColorsClicked(CarouselData carousel);
    void onChangeCarouselColorsClicked(CarouselData carousel);
  }

  @AutoModel HeaderModel_ header;
  @AutoModel ButtonModel_ addButton;
  @AutoModel ButtonModel_ clearButton;
  @AutoModel ButtonModel_ shuffleButton;
  @AutoModel ButtonModel_ changeColorsButton;

  private final AdapterCallbacks callbacks;
  private final OnModelClickListener<ColorModel_, View> colorClickListener;
  private final RecycledViewPool recycledViewPool;

  SampleController(AdapterCallbacks callbacks,
      OnModelClickListener<ColorModel_, View> colorClickListener,
      RecycledViewPool recycledViewPool) {
    this.callbacks = callbacks;
    this.colorClickListener = colorClickListener;
    this.recycledViewPool = recycledViewPool;
    setDebugLoggingEnabled(true);
  }

  // TODO: (eli_hart 2/26/17) Carousel with shared view pools, model groups
  // TODO: (eli_hart 2/27/17) Save colors state
  // TODO: (eli_hart 2/27/17) Shuffle color on click square

  @Override
  protected void buildModels(List<CarouselData> carousels) {
    header
        .title(R.string.epoxy)
        .caption(R.string.header_subtitle)
        .addTo(this);

    addButton
        .text(R.string.button_add)
        .clickListener((model, parentView, clickedView, position) -> {
          callbacks.onAddCarouselClicked();
        })
        .addTo(this);

    clearButton
        .text(R.string.button_clear)
        .clickListener(v -> callbacks.onClearCarouselsClicked())
        .addIf(carousels.size() > 0, this);

    shuffleButton
        .text(R.string.button_shuffle)
        .clickListener(v -> callbacks.onShuffleCarouselsClicked())
        .addIf(carousels.size() > 1, this);

    changeColorsButton
        .text(R.string.button_change)
        .clickListener(v -> callbacks.onChangeAllColorsClicked())
        .addIf(carousels.size() > 0, this);

    for (int i = 0; i < carousels.size(); i++) {
      CarouselData carousel = carousels.get(i);
      add(new CarouselModelGroup(carousel, i, callbacks, colorClickListener, recycledViewPool));
    }
  }
}
