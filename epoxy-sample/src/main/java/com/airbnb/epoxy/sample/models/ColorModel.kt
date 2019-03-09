package com.airbnb.epoxy.sample.models

import android.animation.Animator
import android.view.View
import androidx.annotation.ColorInt
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyAttribute.Option.DoNotHash
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.airbnb.epoxy.sample.R
import com.airbnb.epoxy.sample.models.ColorModel.ColorHolder
import com.airbnb.lottie.LottieAnimationView

/**
 * This is an example of using [com.airbnb.epoxy.SimpleEpoxyModel], which is useful if you
 * don't need to do anything special in onBind. You can also instantiate [ ] directly instead of subclassing it if you don't need to do
 * anything in onBind.
 */
@EpoxyModelClass(layout = R.layout.model_color)
abstract class ColorModel : EpoxyModelWithHolder<ColorHolder>() {
    @EpoxyAttribute @ColorInt var color: Int = 0
    @EpoxyAttribute var playAnimation: Boolean = false
    @EpoxyAttribute(DoNotHash) var clickListener: View.OnClickListener? = null

    override fun bind(holder: ColorHolder) {
        holder.cardView.setBackgroundColor(color)
        holder.cardView.setOnClickListener(clickListener)
    }

    override fun bind(holder: ColorHolder, previouslyBoundModel: EpoxyModel<*>) {
        // When this model changes we get a bind call with the previously bound model, so we can see
        // what changed and update accordingly.
        val previousModel = previouslyBoundModel as ColorModel
        if (previousModel.playAnimation != playAnimation) {
            toggleAnimation(holder.lottieView, playAnimation)
        } else {
            bind(holder)
        }
    }

    private fun toggleAnimation(lottieView: LottieAnimationView, playAnimation: Boolean) {
        if (!playAnimation) {
            if (lottieView.isAnimating) {
                // Reverse it just for fun
                lottieView.speed = -1f
                lottieView.resumeAnimation()
            } else {
                cancelAnimation(lottieView)
            }
            return
        }

        lottieView.addAnimatorListener(object : SimpleAnimatorListener() {
            override fun onAnimationStart(animation: Animator) {
                lottieView.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {
                lottieView.removeAnimatorListener(this)
                cancelAnimation(lottieView)
            }

            override fun onAnimationCancel(animation: Animator) {
                lottieView.removeAnimatorListener(this)
                cancelAnimation(lottieView)
            }
        })

        lottieView.speed = 1f
        lottieView.playAnimation()
    }

    private fun cancelAnimation(lottieView: LottieAnimationView) {
        lottieView.cancelAnimation()
        lottieView.progress = 0f
        lottieView.visibility = View.GONE
    }

    override fun unbind(holder: ColorHolder) {
        // Don't leak the click listener when this view goes back in the view pool
        holder.cardView.setOnClickListener(null)
        cancelAnimation(holder.lottieView)
    }

    class ColorHolder : BaseEpoxyHolder() {
        val cardView: View by bind(R.id.card_view)
        val lottieView: LottieAnimationView by bind(R.id.lottie_view)
    }
}
