package com.airbnb.epoxy.sample.models;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.airbnb.epoxy.sample.R;
import com.airbnb.epoxy.sample.models.ImageButtonModel.ImageButtonHolder;

import butterknife.BindView;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

@EpoxyModelClass(layout = R.layout.model_image_button)
public abstract class ImageButtonModel extends EpoxyModelWithHolder<ImageButtonHolder> {
  @EpoxyAttribute @DrawableRes int imageRes;
  @EpoxyAttribute(DoNotHash) OnClickListener clickListener;

  @Override
  public void bind(@NonNull ImageButtonHolder holder) {
    holder.button.setImageResource(imageRes);
    holder.button.setOnClickListener(clickListener);
  }

  @Override
  public void unbind(@NonNull ImageButtonHolder holder) {
    // Release resources and don't leak listeners as this view goes back to the view pool
    holder.button.setOnClickListener(null);
    holder.button.setImageDrawable(null);
  }

  static class ImageButtonHolder extends BaseEpoxyHolder {
    @BindView(R.id.button) ImageView button;
  }
}
