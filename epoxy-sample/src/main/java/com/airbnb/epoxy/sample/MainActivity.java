package com.airbnb.epoxy.sample;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.RecycledViewPool;
import android.view.View;

import com.airbnb.epoxy.EpoxyTouchHelper;
import com.airbnb.epoxy.EpoxyTouchHelper.DragCallback;
import com.airbnb.epoxy.R;
import com.airbnb.epoxy.sample.SampleController.AdapterCallbacks;
import com.airbnb.epoxy.sample.models.CarouselModelGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static android.animation.ValueAnimator.ofObject;

/**
 * Example activity usage for {@link com.airbnb.epoxy.EpoxyController}.
 */
public class MainActivity extends AppCompatActivity implements AdapterCallbacks {
  private static final Random RANDOM = new Random();
  private static final String CAROUSEL_DATA_KEY = "carousel_data_key";
  private static final int SPAN_COUNT = 2;

  private final RecycledViewPool recycledViewPool = new RecycledViewPool();
  private final SampleController controller = new SampleController(this, recycledViewPool);
  private List<CarouselData> carousels = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Many carousels and color models are shown on screen at once. The default recycled view
    // pool size is only 5, so we manually set the pool size to avoid constantly creating new views
    // We also use a shared view pool so that carousels can recycle items between themselves.
    recycledViewPool.setMaxRecycledViews(R.layout.model_color, Integer.MAX_VALUE);
    recycledViewPool.setMaxRecycledViews(R.layout.model_carousel_group, Integer.MAX_VALUE);
    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    recyclerView.setRecycledViewPool(recycledViewPool);

    // We are using a multi span grid to allow two columns of buttons. To set this up we need
    // to set our span count on the controller and then get the span size lookup object from
    // the controller. This look up object will delegate span size lookups to each model.
    controller.setSpanCount(SPAN_COUNT);
    GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
    gridLayoutManager.setSpanSizeLookup(controller.getSpanSizeLookup());
    recyclerView.setLayoutManager(gridLayoutManager);

    recyclerView.setHasFixedSize(true);
    recyclerView.addItemDecoration(new VerticalGridCardSpacingDecoration());
    recyclerView.setItemAnimator(new SampleItemAnimator());
    recyclerView.setAdapter(controller.getAdapter());

    if (savedInstanceState != null) {
      carousels = savedInstanceState.getParcelableArrayList(CAROUSEL_DATA_KEY);
    }

    initTouch(recyclerView);

    updateController();
  }

  private void initTouch(final RecyclerView recyclerView) {
    // Swiping is not used since it interferes with the carousels, but here is an example of
    // how we would set it up.

//    EpoxyTouchHelper.initSwiping(recyclerView)
//        .leftAndRight()
//        .withTarget(CarouselModelGroup.class)
//        .andCallbacks(new SwipeCallbacks<CarouselModelGroup>() {
//
//          @Override
//          public void onSwipeProgressChanged(CarouselModelGroup model, View itemView,
//              float swipeProgress) {
//
    // Fades a background color in the further you swipe. A different color is used
    // for swiping left vs right.
//            int alpha = (int) (Math.abs(swipeProgress) * 255);
//            if (swipeProgress > 0) {
//              itemView.setBackgroundColor(Color.argb(alpha, 0, 255, 0));
//            } else {
//              itemView.setBackgroundColor(Color.argb(alpha, 255, 0, 0));
//            }
//          }
//
//          @Override
//          public void onSwipeCompleted(CarouselModelGroup model, View itemView, int position,
//              int direction) {
//            carousels.remove(model.data);
//            updateController();
//          }
//
//          @Override
//          public void clearView(CarouselModelGroup model, View itemView) {
//            itemView.setBackgroundColor(Color.WHITE);
//          }
//        });

    EpoxyTouchHelper.initDragging(controller)
        .withRecyclerView(recyclerView)
        .forVerticalList()
        .withTarget(CarouselModelGroup.class)
        .andCallbacks(new DragCallback<CarouselModelGroup>() {
          @ColorInt final int selectedBackgroundColor = Color.argb(200, 200, 200, 200);
          ValueAnimator backgroundAnimator = null;

          @Override
          public void onModelMoved(int fromPosition, int toPosition,
              CarouselModelGroup modelBeingMoved, View itemView) {

            int carouselIndex = carousels.indexOf(modelBeingMoved.data);
            carousels
                .add(carouselIndex + (toPosition - fromPosition), carousels.remove(carouselIndex));
          }

          @Override
          public void onDragStarted(CarouselModelGroup model, View itemView, int adapterPosition) {
            backgroundAnimator = ValueAnimator
                .ofObject(new ArgbEvaluator(), Color.WHITE, selectedBackgroundColor);
            backgroundAnimator.addUpdateListener(
                animator -> itemView.setBackgroundColor((int) animator.getAnimatedValue())
            );

            backgroundAnimator.start();

            itemView
                .animate()
                .scaleX(1.05f)
                .scaleY(1.05f);
          }

          @Override
          public void onDragReleased(CarouselModelGroup model, View itemView) {
            if (backgroundAnimator != null) {
              backgroundAnimator.cancel();
            }

            backgroundAnimator =
                ofObject(new ArgbEvaluator(), ((ColorDrawable) itemView.getBackground()).getColor(),
                    Color.WHITE);
            backgroundAnimator.addUpdateListener(
                animator -> itemView.setBackgroundColor((int) animator.getAnimatedValue())
            );

            backgroundAnimator.start();

            itemView
                .animate()
                .scaleX(1f)
                .scaleY(1f);
          }

          @Override
          public void clearView(CarouselModelGroup model, View itemView) {
            onDragReleased(model, itemView);
          }
        });
  }

  @Override
  protected void onSaveInstanceState(Bundle state) {
    super.onSaveInstanceState(state);
    state.putParcelableArrayList(CAROUSEL_DATA_KEY, (ArrayList<? extends Parcelable>) carousels);
    controller.onSaveInstanceState(state);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    controller.onRestoreInstanceState(savedInstanceState);
  }

  private void updateController() {
    controller.setData(carousels);
  }

  @Override
  public void onAddCarouselClicked() {
    CarouselData carousel = new CarouselData(carousels.size(), new ArrayList<>());
    addColorToCarousel(carousel);
    carousels.add(0, carousel);
    updateController();
  }

  private void addColorToCarousel(CarouselData carousel) {
    List<ColorData> colors = carousel.getColors();
    colors.add(0, new ColorData(randomColor(), colors.size()));
  }

  @Override
  public void onClearCarouselsClicked() {
    carousels.clear();
    updateController();
  }

  @Override
  public void onShuffleCarouselsClicked() {
    Collections.shuffle(carousels);
    updateController();
  }

  @Override
  public void onChangeAllColorsClicked() {
    for (CarouselData carouselData : carousels) {
      for (ColorData colorData : carouselData.getColors()) {
        colorData.setColorInt(randomColor());
      }
    }

    updateController();
  }

  @Override
  public void onAddColorToCarouselClicked(CarouselData carousel) {
    addColorToCarousel(carousel);
    updateController();
  }

  @Override
  public void onClearCarouselClicked(CarouselData carousel) {
    carousel.getColors().clear();
    updateController();
  }

  @Override
  public void onShuffleCarouselColorsClicked(CarouselData carousel) {
    Collections.shuffle(carousel.getColors());
    updateController();
  }

  @Override
  public void onChangeCarouselColorsClicked(CarouselData carousel) {
    for (ColorData colorData : carousel.getColors()) {
      colorData.setColorInt(randomColor());
    }

    updateController();
  }

  @Override
  public void onColorClicked(CarouselData carousel, int colorPosition) {
    int carouselPosition = carousels.indexOf(carousel);
    ColorData colorData = carousels.get(carouselPosition).getColors().get(colorPosition);
    colorData.setPlayAnimation(!colorData.shouldPlayAnimation());

    updateController();
  }

  private static int randomColor() {
    int r = RANDOM.nextInt(256);
    int g = RANDOM.nextInt(256);
    int b = RANDOM.nextInt(256);

    return Color.rgb(r, g, b);
  }
}
