package com.airbnb.epoxy;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;

import com.airbnb.epoxy.models.ButtonModel;
import com.airbnb.epoxy.models.ButtonModel_;
import com.airbnb.epoxy.models.ColorModel_;
import com.airbnb.epoxy.models.HeaderModel;
import com.airbnb.epoxy.models.HeaderModel_;

import java.util.Collections;
import java.util.Random;

class SampleAdapter extends EpoxyAdapter {
  private static final Random RANDOM = new Random();

  // These models are saved as fields so they can easily be shown or hidden as needed
  private final ButtonModel_ clearButton = new ButtonModel_();
  private final ButtonModel_ shuffleButton = new ButtonModel_();
  private final ButtonModel_ changeColorsButton = new ButtonModel_();

  SampleAdapter() {
    // We are going to use automatic diffing, so we just have to enable it first
    enableDiffing();

    // We're using the generated subclasses of our models, which is indicated by the underscore
    // appended to the class name. These generated classes contain our setter methods, as well as
    // the hashcode methods that tell the diffing algorithm when a model has changed
    HeaderModel headerModel = new HeaderModel_()
        .title(R.string.epoxy)
        .caption(R.string.header_subtitle);

    ButtonModel addButton = new ButtonModel_()
        .text(R.string.button_add)
        .clickListener(onAddClicked);

    clearButton.text(R.string.button_clear)
        .clickListener(onClearClicked);

    shuffleButton.text(R.string.button_shuffle)
        .clickListener(onShuffleClicked);

    changeColorsButton.text(R.string.button_change)
        .clickListener(onChangeColorsClicked);

    addModels(
        headerModel,
        addButton,
        clearButton,
        shuffleButton,
        changeColorsButton
    );

    updateButtonVisibility();
  }

  private void updateButtonVisibility() {
    int colorCount = getAllModelsAfter(changeColorsButton).size();
    showModels(colorCount > 0, changeColorsButton, clearButton);
    showModels(colorCount > 1, shuffleButton);
  }

  private final OnClickListener onAddClicked = new OnClickListener() {
    @Override
    public void onClick(View v) {
      insertModelAfter(new ColorModel_(randomColor()), changeColorsButton);
      updateButtonVisibility();
    }
  };

  private final OnClickListener onClearClicked = new OnClickListener() {
    @Override
    public void onClick(View v) {
      removeAllAfterModel(changeColorsButton);
      updateButtonVisibility();
    }
  };

  private final OnClickListener onShuffleClicked = new OnClickListener() {
    @Override
    public void onClick(View v) {
      Collections.shuffle(getAllModelsAfter(changeColorsButton));
      notifyModelsChanged();
    }
  };

  private final OnClickListener onChangeColorsClicked = new OnClickListener() {
    @Override
    public void onClick(View v) {
      for (EpoxyModel<?> model : getAllModelsAfter(changeColorsButton)) {
        ((ColorModel_) model).color(randomColor());
      }
      notifyModelsChanged();
    }
  };

  private int randomColor() {
    int r = RANDOM.nextInt(256);
    int g = RANDOM.nextInt(256);
    int b = RANDOM.nextInt(256);

    return Color.rgb(r, g, b);
  }
}
