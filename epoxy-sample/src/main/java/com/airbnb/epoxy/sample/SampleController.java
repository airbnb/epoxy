package com.airbnb.epoxy.sample;

import com.airbnb.epoxy.AutoModel;
import com.airbnb.epoxy.TypedEpoxyController;
import com.airbnb.epoxy.sample.models.CarouselModelGroup;
import com.airbnb.epoxy.sample.views.HeaderViewModel_;

import java.util.List;

import static com.airbnb.epoxy.EpoxyAsyncUtil.getAsyncBackgroundHandler;

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

  @AutoModel HeaderViewModel_ header;
  @AutoModel ButtonBindingModel_ addButton;
  @AutoModel ButtonBindingModel_ clearButton;
  @AutoModel ButtonBindingModel_ shuffleButton;
  @AutoModel ButtonBindingModel_ changeColorsButton;

  private final AdapterCallbacks callbacks;

  SampleController(AdapterCallbacks callbacks) {
    // Demonstrating how model building and diffing can be done in the background.
    // You can control them separately by passing in separate handler, as shown below.
    super(getAsyncBackgroundHandler(), getAsyncBackgroundHandler());
//    super(new Handler(), BACKGROUND_HANDLER);
//    super(BACKGROUND_HANDLER, new Handler());

    this.callbacks = callbacks;
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
      add(new CarouselModelGroup(carousel, callbacks));
    }
  }

  @Override
  protected void onExceptionSwallowed(RuntimeException exception) {
    // Best practice is to throw in debug so you are aware of any issues that Epoxy notices.
    // Otherwise Epoxy does its best to swallow these exceptions and continue gracefully
    throw exception;
  }
}
