package com.airbnb.epoxy.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.RecycledViewPool;

import com.airbnb.epoxy.R;
import com.airbnb.epoxy.sample.SampleController.AdapterCallbacks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

    updateController();
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
