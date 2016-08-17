package com.airbnb.epoxy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.airbnb.epoxy.models.HeaderModel_;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.recycler_view) RecyclerView recyclerView;
  private Adapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    recyclerView.setHasFixedSize(true);
    adapter = new Adapter();
    recyclerView.setAdapter(adapter);
  }

  static class Adapter extends EpoxyAdapter {
    Adapter() {
      addModels(new HeaderModel_()
          .title("Title")
          .caption("caption"));
    }
  }
}