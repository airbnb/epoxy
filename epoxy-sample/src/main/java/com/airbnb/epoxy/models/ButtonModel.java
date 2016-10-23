package com.airbnb.epoxy.models;

import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.airbnb.epoxy.R;
import com.airbnb.epoxy.models.ButtonModel.ButtonHolder;

import butterknife.BindView;

/** This model class gives an example of how to use a view holder pattern with your models. */
public class ButtonModel extends EpoxyModelWithHolder<ButtonHolder> {
  @EpoxyAttribute @StringRes int text;
  @EpoxyAttribute (hash = false) OnClickListener clickListener;
  @EpoxyAttribute @ColorInt int backgroundColor;

  @Override
  protected int getDefaultLayout() {
    return R.layout.model_button;
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }

  @Override
  public void bind(ButtonHolder holder) {
    holder.button.setText(text);
    holder.button.setBackgroundColor(backgroundColor);
    holder.button.setOnClickListener(clickListener);
  }

  @Override
  protected ButtonHolder createNewHolder() {
    return new ButtonHolder();
  }

  static class ButtonHolder extends BaseEpoxyHolder {
    @BindView(R.id.button) Button button;
  }
}
