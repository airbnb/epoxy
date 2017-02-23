package com.airbnb.epoxy;

import com.airbnb.epoxy.models.ButtonModel_;
import com.airbnb.epoxy.models.ColorModel_;
import com.airbnb.epoxy.models.HeaderModel_;

import java.util.List;

class SampleAdapter extends TypedDiffAdapter<List<ColorData>> {
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

  SampleAdapter(AdapterCallbacks callbacks) {
    this.callbacks = callbacks;
  }

  @Override
  protected void buildModels(List<ColorData> colors) {
    header
        .title(R.string.epoxy)
        .caption(R.string.header_subtitle)
        .addTo(this);

    addButton
        .text(R.string.button_add)
        .clickListener(v -> callbacks.onAddClicked())
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

  protected void build(List<ColorData> colors, List<EpoxyModel<?>> models) {
    models.add(header
        .title(R.string.epoxy)
        .caption(R.string.header_subtitle));

    models.add(addButton
        .text(R.string.button_add)
        .clickListener(v -> callbacks.onAddClicked()));

    if (colors.size() > 0) {
      models.add(clearButton
          .text(R.string.button_clear)
          .clickListener(v -> callbacks.onClearClicked()));
    }

    if (colors.size() > 1) {
      models.add(shuffleButton
          .text(R.string.button_shuffle)
          .clickListener(v -> callbacks.onShuffleClicked()));
    }

    if (colors.size() > 0) {
      models.add(changeColorsButton
          .text(R.string.button_change)
          .clickListener(v -> callbacks.onChangeColorsClicked()));
    }

    for (ColorData color : colors) {
      models.add(new ColorModel_(color));
    }
  }
}
