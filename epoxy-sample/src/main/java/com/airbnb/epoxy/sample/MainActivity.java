package com.airbnb.epoxy.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.RecycledViewPool;
import android.view.View;

import com.airbnb.epoxy.OnModelClickListener;
import com.airbnb.epoxy.R;
import com.airbnb.epoxy.sample.SampleController.AdapterCallbacks;
import com.airbnb.epoxy.sample.models.ColorModel.ColorHolder;
import com.airbnb.epoxy.sample.models.ColorModel_;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Example activity usage for {@link com.airbnb.epoxy.EpoxyAdapter}. Allows you to create a list of
 * colored blocks and modify it in different ways.
 */
public class MainActivity extends AppCompatActivity implements AdapterCallbacks,
    OnModelClickListener<ColorModel_, ColorHolder> {
  private static final Random RANDOM = new Random();
  private static final String CAROUSEL_DATA_KEY = "carousel_data_key";
  public static final int SPAN_COUNT = 2;

  @BindView(R.id.recycler_view) RecyclerView recyclerView;
  private List<CarouselData> carousels = new ArrayList<>();
  private final RecycledViewPool recycledViewPool = new RecycledViewPool();
  private SampleController controller = new SampleController(this, this, recycledViewPool);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    // Many carousels and color models are shown on screen at once. The default recycled view
    // pool size is only 5, so we manually set the pool size to avoid constantly creating new views
    recycledViewPool.setMaxRecycledViews(R.layout.model_color, Integer.MAX_VALUE);
    recycledViewPool.setMaxRecycledViews(R.layout.model_carousel_group, Integer.MAX_VALUE);
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

    updateAdapter();
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

  private void updateAdapter() {
    controller.setData(carousels);
  }

  private void addColorToCarousel(CarouselData carousel) {
    List<ColorData> colors = carousel.getColors();
    colors.add(0, new ColorData(randomColor(), colors.size()));
  }

  @Override
  public void onAddCarouselClicked() {
    CarouselData carousel = new CarouselData(carousels.size(), new ArrayList<>());
    addColorToCarousel(carousel);
    carousels.add(0, carousel);
    updateAdapter();
  }

  @Override
  public void onClearCarouselsClicked() {
    carousels.clear();
    updateAdapter();
  }

  @Override
  public void onShuffleCarouselsClicked() {
    Collections.shuffle(carousels);
    updateAdapter();
  }

  @Override
  public void onChangeAllColorsClicked() {
    for (CarouselData carouselData : carousels) {
      for (ColorData colorData : carouselData.getColors()) {
        colorData.setColorInt(randomColor());
      }
    }

    updateAdapter();
  }

  @Override
  public void onAddColorToCarouselClicked(CarouselData carousel) {
    addColorToCarousel(carousel);
    updateAdapter();
  }

  @Override
  public void onClearCarouselClicked(CarouselData carousel) {
    carousel.getColors().clear();
    updateAdapter();
  }

  @Override
  public void onShuffleCarouselColorsClicked(CarouselData carousel) {
    Collections.shuffle(carousel.getColors());
    updateAdapter();
  }

  @Override
  public void onChangeCarouselColorsClicked(CarouselData carousel) {
    for (ColorData colorData : carousel.getColors()) {
      colorData.setColorInt(randomColor());
    }

    updateAdapter();
  }

  @Override
  public void onClick(ColorModel_ model, ColorHolder parentView, View clickedView, int position) {
    // This is used as an example of a model click listener, to get the model, view, and position
    // that was clicked.
    ColorData colorData = carousels.get(model.carousel()).getColors().get(position);
    colorData.setPlayAnimation(!colorData.shouldPlayAnimation());
    updateAdapter();
  }

  private static int randomColor() {
    int r = RANDOM.nextInt(256);
    int g = RANDOM.nextInt(256);
    int b = RANDOM.nextInt(256);

    return Color.rgb(r, g, b);
  }
}
