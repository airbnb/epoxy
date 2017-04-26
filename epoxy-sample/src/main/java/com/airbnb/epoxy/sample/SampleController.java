package com.airbnb.epoxy.sample;

import android.support.v7.widget.RecyclerView.RecycledViewPool;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.R;
import com.airbnb.epoxy.TypedEpoxyController;
import com.airbnb.epoxy.sample.models.ButtonModel_;
import com.airbnb.epoxy.sample.models.CarouselModelGroup;
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
    void onColorClicked(CarouselData carousel, int colorPosition);
  }

  @AutoModel HeaderModel_ header;
  @AutoModel ButtonModel_ addButton;
  @AutoModel ButtonModel_ clearButton;
  @AutoModel ButtonModel_ shuffleButton;
  @AutoModel ButtonModel_ changeColorsButton;

  private final AdapterCallbacks callbacks;
  private final RecycledViewPool recycledViewPool;

  SampleController(AdapterCallbacks callbacks, RecycledViewPool recycledViewPool) {
    this.callbacks = callbacks;
    this.recycledViewPool = recycledViewPool;
    setDebugLoggingEnabled(true);
  }

  @Override
  protected void buildModels(List<CarouselData> carousels) {
    header
        .title(R.string.epoxy)
        .caption(R.string.header_subtitle);
    // "addTo" is not needed since implicit adding is enabled
    // (https://github.com/airbnb/epoxy/wiki/Epoxy-Controller#implicit-adding)

    addButton
        .textRes(R.string.button_add)
        .clickListener((model, parentView, clickedView, position) -> {
          callbacks.onAddCarouselClicked();
        });

    clearButton
        .textRes(R.string.button_clear)
        .clickListener(v -> callbacks.onClearCarouselsClicked())
        .addIf(carousels.size() > 0, this);

    shuffleButton
        .textRes(R.string.button_shuffle)
        .clickListener(v -> callbacks.onShuffleCarouselsClicked())
        .addIf(carousels.size() > 1, this);

    changeColorsButton
        .textRes(R.string.button_change)
        .clickListener(v -> callbacks.onChangeAllColorsClicked())
        .addIf(carousels.size() > 0, this);

    for (int i = 0; i < carousels.size(); i++) {
      CarouselData carousel = carousels.get(i);
      add(new CarouselModelGroup(carousel, callbacks, recycledViewPool));
    }
  }

  @Override
  protected void onExceptionSwallowed(RuntimeException exception) {
    // Best practice is to throw in debug so you are aware of any issues that Epoxy notices.
    // Otherwise Epoxy does its best to swallow these exceptions and continue gracefully
    throw exception;
  }
}
