package com.airbnb.epoxy;

import android.view.View;

import com.airbnb.epoxy.models.ButtonModel.ButtonHolder;
import com.airbnb.epoxy.models.ButtonModel_;
import com.airbnb.epoxy.models.ColorModel_;
import com.airbnb.epoxy.models.HeaderModel_;

import java.util.List;

class SampleAdapter extends TypedAutoEpoxyAdapter<List<ColorData>> {
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
        .addTo(this);

    clearButton
        .text(R.string.button_clear)
        .clickListener((model, view, adapterPosition) -> {

        })
        .addIf(colors.size() > 0, this);

    shuffleButton
        .text(R.string.button_shuffle)
        .addIf(colors.size() > 1, this);

    changeColorsButton
        .text(R.string.button_change)
        .addIf(colors.size() > 0, this);

    for (ColorData color : colors) {
      add(new ColorModel_(color));
    }
  }
}
