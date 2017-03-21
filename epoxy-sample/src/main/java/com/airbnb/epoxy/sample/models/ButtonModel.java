package com.airbnb.epoxy.sample.models;

import android.support.annotation.StringRes;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.airbnb.epoxy.R;
import com.airbnb.epoxy.sample.models.ButtonModel.ButtonHolder;

import butterknife.BindView;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

/** This model class gives an example of how to use a view holder pattern with your models. */
@EpoxyModelClass(layout = R.layout.model_button)
public abstract class ButtonModel extends EpoxyModelWithHolder<ButtonHolder> {
  @EpoxyAttribute @StringRes int text;
  @EpoxyAttribute(DoNotHash) OnClickListener clickListener;

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }

  @Override
  public void bind(ButtonHolder holder) {
    holder.button.setText(text);
    holder.button.setOnClickListener(clickListener);
  }

  public static class ButtonHolder extends BaseEpoxyHolder {
    @BindView(R.id.button) Button button;
  }
}
