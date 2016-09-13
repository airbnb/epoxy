package com.airbnb.epoxy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Example activity usage for {@link com.airbnb.epoxy.EpoxyAdapter}. Allows you to create a list of
 * colored blocks and modify it in different ways.
 */
public class MainActivity extends AppCompatActivity {

  @BindView(R.id.recycler_view) RecyclerView recyclerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    int spanCount = getSpanCount();

    SampleAdapter adapter = new SampleAdapter();

    // We are using a multi span grid to show many color models in each row. To set this up we need
    // to set our span count on the adapter and then get the span size lookup object from
    // the adapter. This look up object will delegate span size lookups to each model.
    adapter.setSpanCount(spanCount);
    GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount);
    gridLayoutManager.setSpanSizeLookup(adapter.getSpanSizeLookup());

    recyclerView.setLayoutManager(gridLayoutManager);
    recyclerView.setHasFixedSize(true);
    recyclerView.addItemDecoration(new VerticalGridCardSpacingDecoration());
    recyclerView.setAdapter(adapter);

    // Many color models are shown on screen at once. The default recycled view pool size is
    // only 5, so we manually set the pool size to avoid constantly creating new views when
    // the colors are randomized
    recyclerView.getRecycledViewPool().setMaxRecycledViews(R.layout.model_color, 50);
  }

  private int getSpanCount() {
    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
    return (int) (dpWidth / 100);
  }
}
