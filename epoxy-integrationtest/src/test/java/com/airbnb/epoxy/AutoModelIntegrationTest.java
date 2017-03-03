package com.airbnb.epoxy;

import android.app.Activity;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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

  static class AdapterClickListenerIsUsedInHash extends AutoEpoxyAdapter {

    private OnModelClickListener<ModelWithClickListener_, View>
        onModelClickListener = new OnModelClickListener<ModelWithClickListener_, View>() {

          @Override
          public void onClick(ModelWithClickListener_ model, View view, int position) {

          }
        };

    @Override
    protected void buildModels() {
      new ModelWithClickListener_()
          .id(1)
          .clickListener(onModelClickListener)
          .addTo(this);
    }
  }

  @Test
  public void modelClickListenerIsUsedInHash() {
    // Internally we wrap the model click listener with an anonymous click listener. We can't hash
    // the anonymous click listener since that changes the model state, instead our anonymous click
    // listener should use the hashCode of the user's click listener

    AdapterClickListenerIsUsedInHash adapter = new AdapterClickListenerIsUsedInHash();

    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    adapter.registerAdapterDataObserver(observerMock);

    adapter.requestModelUpdate();
    verify(observerMock).onItemRangeInserted(0, 1);

    // The second update shouldn't cause a item change
    adapter.requestModelUpdate();
    verifyNoMoreInteractions(observerMock);
  }
}