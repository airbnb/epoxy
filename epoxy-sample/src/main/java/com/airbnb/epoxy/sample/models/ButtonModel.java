package com.airbnb.epoxy.sample.models;

import android.support.annotation.StringRes;
import android.view.View.OnClickListener;

import com.airbnb.epoxy.DataBindingEpoxyModel;
import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.R;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

/** This model shows an example of using data binding. */
@EpoxyModelClass(layout = R.layout.model_button)
public abstract class ButtonModel extends DataBindingEpoxyModel {
  @EpoxyAttribute @StringRes int textRes;
  @EpoxyAttribute(DoNotHash) OnClickListener clickListener;
}
