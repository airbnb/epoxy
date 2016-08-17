package com.airbnb.epoxy;

import android.widget.TextView;

import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;

import java.util.Random;

public class NumberModel extends EpoxyModel<TextView> {
  private static final Random RANDOM = new Random(10);
  @EpoxyAttribute int value;

  public NumberModel() {
    randomizeValue();
    id(value);
  }

  public void randomizeValue() {
    value = RANDOM.nextInt();
  }

  @Override
  public void bind(TextView view) {
    super.bind(view);
    view.setText("" + value);
  }

  @Override
  public int getDefaultLayout() {
    return R.layout.number_view;
  }
}