package com.airbnb.epoxy;

import android.widget.TextView;

import com.airbnb.epoxy.integrationtest.BuildConfig;
import com.airbnb.epoxy.integrationtest.Model_;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@LooperMode(LooperMode.Mode.LEGACY)
public class BindModelIntegrationTest {

  private Model_ model;

  class TestAdapter extends BaseEpoxyAdapter {

    private boolean diffPayloadsEnabled;
    private List<EpoxyModel<?>> models;

    TestAdapter(boolean diffPayloadsEnabled) {
      this.diffPayloadsEnabled = diffPayloadsEnabled;
      this.models = new ArrayList<>();
      models.add(model);
    }

    @Override
    List<EpoxyModel<?>> getCurrentModels() {
      return models;
    }

    @Override
    boolean diffPayloadsEnabled() {
      return diffPayloadsEnabled;
    }
  }

  @Before
  public void before() {
    model = spy(new Model_()).id(1);
  }

  @Test
  public void bindNoPayloads() {
    TestAdapter adapter = new TestAdapter(false);
    EpoxyViewHolder viewHolder = ControllerLifecycleHelper.createViewHolder(adapter, 0);
    adapter.onBindViewHolder(viewHolder, 0);

    verify(model).bind((TextView) viewHolder.itemView);
    verify(model, never()).bind(any(TextView.class), any(List.class));
    verify(model, never()).bind(any(TextView.class), any(EpoxyModel.class));
  }

  @Test
  public void bindWithPayloads() {
    TestAdapter adapter = new TestAdapter(false);
    EpoxyViewHolder viewHolder = ControllerLifecycleHelper.createViewHolder(adapter, 0);

    ArrayList<Object> payloads = new ArrayList<>();
    payloads.add("hello");

    adapter.onBindViewHolder(viewHolder, 0, payloads);

    verify(model).bind((TextView) viewHolder.itemView, payloads);
    // This is called if the payloads bind call isn't implemented
    verify(model).bind(any(TextView.class));
    verify(model, never()).bind(any(TextView.class), any(EpoxyModel.class));
  }

  @Test
  public void bindWithDiffPayload() {
    TestAdapter adapter = new TestAdapter(true);
    EpoxyViewHolder viewHolder = ControllerLifecycleHelper.createViewHolder(adapter, 0);

    Model_ originallyBoundModel = new Model_();
    originallyBoundModel.id(model.id());

    List<Object> payloads = DiffPayloadTestUtil.payloadsWithChangedModels(originallyBoundModel);
    adapter.onBindViewHolder(viewHolder, 0, payloads);

    verify(model).bind((TextView) viewHolder.itemView, originallyBoundModel);
    // This is called if the payloads bind call isn't implemented
    verify(model).bind(any(TextView.class));
    verify(model, never()).bind(any(TextView.class), any(List.class));
  }
}
