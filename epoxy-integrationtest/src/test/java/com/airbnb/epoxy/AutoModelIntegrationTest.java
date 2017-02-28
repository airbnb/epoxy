package com.airbnb.epoxy;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class AutoModelIntegrationTest {

  static class TestModel extends EpoxyModel<View> {

    @Override
    protected int getDefaultLayout() {
      return 0;
    }
  }

  static class BasicAutoModelsAdapter extends AutoEpoxyAdapter {

    @AutoModel TestModel model1;
    @AutoModel TestModel model2;

    @Override
    protected void buildModels() {
      add(model1.id(1));
      add(model2.id(2));
    }
  }

  @Test
  public void basicAutoModels() {
    BasicAutoModelsAdapter testAdapter = new BasicAutoModelsAdapter();
    testAdapter.requestModelUpdate();

    List<EpoxyModel<?>> models = testAdapter.getCurrentModels();

    assertEquals("Models size", 2, models.size());
    assertEquals("First model", TestModel.class, models.get(0).getClass());
    assertEquals("Second model", TestModel.class, models.get(1).getClass());
  }

  static class AdapterWithFieldAssigned extends AutoEpoxyAdapter {

    @AutoModel TestModel model1 = new TestModel();

    @Override
    protected void buildModels() {
      add(model1);
    }
  }

  @Test(expected = IllegalStateException.class)
  public void assigningValueToFieldFails() {
    AdapterWithFieldAssigned testAdapter = new AdapterWithFieldAssigned();
    testAdapter.requestModelUpdate();
  }

  static class AdapterWithModelClickListener extends AutoEpoxyAdapter {
    boolean clicked;

    @Override
    protected void buildModels() {
      new ModelWithClickListener_()
          .id(1)
          .clickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
              // This view click listener should be overridden by the model click listener
              // and not called.

              // We also want to make sure the setter is still generated on the class
              // in case a view click listener is wanted.
            }
          })
          .clickListener(new OnModelClickListener<ModelWithClickListener_, View>() {

            @Override
            public void onClick(ModelWithClickListener_ model, View view, int position) {
              clicked = true;
            }
          })
          .addTo(this);
    }
  }

  @Test
  public void modelClickListenerLifecycle() {
    Activity activity = Robolectric.setupActivity(Activity.class);
    AdapterWithModelClickListener adapter = new AdapterWithModelClickListener();
    adapter.requestModelUpdate();

    ModelWithClickListener_ model = (ModelWithClickListener_) adapter.getCurrentModels().get(0);

    model.clickListener.onClick(new View(activity));
    // Click listener should not call back since the model isn't bound yet
    assertFalse(adapter.clicked);

    // We can create the model and bind it
    EpoxyViewHolder viewHolder = adapter.onCreateViewHolder(new FrameLayout(activity),
        model.getLayout());

    adapter.onBindViewHolder(viewHolder, 0);

    // Click listener works correctly once bound
    model.clickListener.onClick(new View(activity));
    assertTrue(adapter.clicked);

    adapter.onViewRecycled(viewHolder);

    // Make sure calling the view click listener after unbind doesn't call through to the model
    // click listener
    adapter.clicked = false;
    model.clickListener.onClick(new View(activity));
    // Click listener should not call back since the model isn't bound yet
    assertFalse(adapter.clicked);
  }
}