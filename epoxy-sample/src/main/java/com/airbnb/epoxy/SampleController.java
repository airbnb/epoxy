package com.airbnb.epoxy;

import com.airbnb.epoxy.models.ButtonModel_;
import com.airbnb.epoxy.models.ColorModel_;
import com.airbnb.epoxy.models.HeaderModel_;

import java.util.List;

class SampleController extends TypedEpoxyController<List<ColorData>> {
  interface AdapterCallbacks {
    void onAddClicked();
    void onClearClicked();
    void onShuffleClicked();
    void onChangeColorsClicked();
  }

  @AutoModel HeaderModel_ header;
  @AutoModel ButtonModel_ addButton;
  @AutoModel ButtonModel_ clearButton;
  @AutoModel ButtonModel_ shuffleButton;
  @AutoModel ButtonModel_ changeColorsButton;

  private final AdapterCallbacks callbacks;

  SampleController(AdapterCallbacks callbacks) {
    this.callbacks = callbacks;
    setDebugLoggingEnabled(true);
  }

  // TODO: (eli_hart 2/26/17) Carousel with shared view pools, model groups
  // TODO: (eli_hart 2/27/17) Save colors state
  // TODO: (eli_hart 2/27/17) Shuffle color on click square
  // TODO: (eli_hart 2/27/17) typed adapter integration test

  @Override
  protected void buildModels(List<ColorData> colors) {
    header
        .title(R.string.epoxy)
        .caption(R.string.header_subtitle)
        .addTo(this);

    addButton
        .text(R.string.button_add)
        .clickListener((model, parentView, clickedView, position) -> {
          callbacks.onAddClicked();
        })
        .addTo(this);

    clearButton
        .text(R.string.button_clear)
        .clickListener(v -> callbacks.onClearClicked())
        .addIf(colors.size() > 0, this);

    shuffleButton
        .text(R.string.button_shuffle)
        .clickListener(v -> callbacks.onShuffleClicked())
        .addIf(colors.size() > 1, this);

    changeColorsButton
        .text(R.string.button_change)
        .clickListener(v -> callbacks.onChangeColorsClicked())
        .addIf(colors.size() > 0, this);

    for (ColorData color : colors) {
      add(new ColorModel_(color));
    }
  }
}
