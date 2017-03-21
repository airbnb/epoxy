package com.airbnb.epoxy.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import com.airbnb.epoxy.R;
import com.airbnb.epoxy.sample.SampleController.AdapterCallbacks;

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
public class MainActivity extends AppCompatActivity implements AdapterCallbacks {
  private static final Random RANDOM = new Random();

  @BindView(R.id.recycler_view) RecyclerView recyclerView;
  private final SampleController controller = new SampleController(this);
  private final List<ColorData> colors = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    int spanCount = getSpanCount();

    // We are using a multi span grid to show many color models in each row. To set this up we need
    // to set our span count on the controller and then get the span size lookup object from
    // the controller. This look up object will delegate span size lookups to each model.
    controller.setSpanCount(spanCount);
    GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount);
    gridLayoutManager.setSpanSizeLookup(controller.getSpanSizeLookup());

    recyclerView.setLayoutManager(gridLayoutManager);
    recyclerView.setHasFixedSize(true);
    recyclerView.setItemAnimator(new SampleItemAnimator());
//    recyclerView.addItemDecoration(new VerticalGridCardSpacingDecoration());
    recyclerView.setAdapter(controller.getAdapter());
    updateAdapter();

    // Many color models are shown on screen at once. The default recycled view pool size is
    // only 5, so we manually set the pool size to avoid constantly creating new views when
    // the colors are randomized
    recyclerView.getRecycledViewPool().setMaxRecycledViews(R.layout.model_color, 50);
  }

  private void updateAdapter() {
    controller.setData(colors);
  }

  private int getSpanCount() {
    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
    return (int) (dpWidth / 100);
  }

  @Override
  public void onAddClicked() {
    colors.add(0, new ColorData(randomColor(), colors.size()));
    updateAdapter();
  }

  @Override
  public void onClearClicked() {
    colors.clear();
    updateAdapter();
  }

  @Override
  public void onShuffleClicked() {
    Collections.shuffle(colors);
    updateAdapter();
  }

  @Override
  public void onChangeColorsClicked() {
    for (ColorData color : colors) {
      color.setColorInt(randomColor());
    }
    updateAdapter();
  }

  private static int randomColor() {
    int r = RANDOM.nextInt(256);
    int g = RANDOM.nextInt(256);
    int b = RANDOM.nextInt(256);

    return Color.rgb(r, g, b);
  }
}
