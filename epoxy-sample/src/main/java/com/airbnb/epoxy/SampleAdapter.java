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

  private final ButtonModel_ clearButton = new ButtonModel_();
  private final ButtonModel_ shuffleButton = new ButtonModel_();
  private final ButtonModel_ changeColorsButton = new ButtonModel_();

  SampleAdapter() {
    enableDiffing();

    HeaderModel headerModel = new HeaderModel_()
        .title("Epoxy")
        .caption("Composing views with ease");

    ButtonModel addButton = new ButtonModel_()
        .text("Add")
        .clickListener(onAddClicked);

    clearButton.text("Clear")
        .clickListener(onClearClicked);

    shuffleButton.text("Shuffle")
        .clickListener(onShuffleClicked);

    changeColorsButton.text("Change")
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
