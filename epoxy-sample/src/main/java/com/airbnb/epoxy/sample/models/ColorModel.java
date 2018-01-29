package com.airbnb.epoxy.sample.models;

import android.animation.Animator;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.View;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.airbnb.epoxy.sample.R;
import com.airbnb.epoxy.sample.models.ColorModel.ColorHolder;
import com.airbnb.lottie.LottieAnimationView;

import butterknife.BindView;

import static com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash;

/**
 * This is an example of using {@link com.airbnb.epoxy.SimpleEpoxyModel}, which is useful if you
 * don't need to do anything special in onBind. You can also instantiate {@link
 * com.airbnb.epoxy.SimpleEpoxyModel} directly instead of subclassing it if you don't need to do
 * anything in onBind.
 */
@EpoxyModelClass(layout = R.layout.model_color)
public abstract class ColorModel extends EpoxyModelWithHolder<ColorHolder> {
  @EpoxyAttribute @ColorInt int color;
  @EpoxyAttribute boolean playAnimation;
  @EpoxyAttribute(DoNotHash) View.OnClickListener clickListener;

  @Override
  public void bind(@NonNull ColorHolder holder) {
    holder.cardView.setBackgroundColor(color);
    holder.cardView.setOnClickListener(clickListener);
  }

  @Override
  public void bind(@NonNull ColorHolder holder, @NonNull EpoxyModel<?> previouslyBoundModel) {
    // When this model changes we get a bind call with the previously bound model, so we can see
    // what changed and update accordingly.
    ColorModel previousModel = (ColorModel) previouslyBoundModel;
    if (previousModel.playAnimation != playAnimation) {
      toggleAnimation(holder.lottieView, playAnimation);
    } else {
      bind(holder);
    }
  }

  private void toggleAnimation(final LottieAnimationView lottieView, boolean playAnimation) {
    if (!playAnimation) {
      if (lottieView.isAnimating()) {
        // Reverse it just for fun
        lottieView.resumeReverseAnimation();
      } else {
        cancelAnimation(lottieView);
      }
      return;
    }

    lottieView.addAnimatorListener(new SimpleAnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {
        lottieView.setVisibility(View.VISIBLE);
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        lottieView.removeAnimatorListener(this);
        cancelAnimation(lottieView);
      }

      @Override
      public void onAnimationCancel(Animator animation) {
        lottieView.removeAnimatorListener(this);
        cancelAnimation(lottieView);
      }
    });

    lottieView.playAnimation();
  }

  private void cancelAnimation(LottieAnimationView lottieView) {
    lottieView.cancelAnimation();
    lottieView.setProgress(0);
    lottieView.setVisibility(View.GONE);
  }

  @Override
  public void unbind(@NonNull ColorHolder holder) {
    // Don't leak the click listener when this view goes back in the view pool
    holder.cardView.setOnClickListener(null);
    cancelAnimation(holder.lottieView);
  }

  public static class ColorHolder extends BaseEpoxyHolder {
    @BindView(R.id.card_view) View cardView;
    @BindView(R.id.lottie_view) LottieAnimationView lottieView;
  }
}
