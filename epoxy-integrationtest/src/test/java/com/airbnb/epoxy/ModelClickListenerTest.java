package com.airbnb.epoxy;

import android.app.Activity;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class ModelClickListenerTest {
  final Activity activity = Robolectric.setupActivity(Activity.class);
  final AdapterWithModelClickListener adapter = new AdapterWithModelClickListener();

  private void buildModelsAndBind() {
    adapter.requestModelUpdate();
    // We can create the model and bind it
    EpoxyViewHolder viewHolder = adapter.onCreateViewHolder(new FrameLayout(activity),
        adapter.model.getLayout());

    adapter.onBindViewHolder(viewHolder, 0);
  }

  static class ModelClickListener implements OnModelClickListener<ModelWithClickListener_, View> {
    boolean clicked;

    @Override
    public void onClick(ModelWithClickListener_ model, View view, View v, int position) {
      clicked = true;
    }
  }

  static class ViewClickListener implements OnClickListener {
    boolean clicked;

    @Override
    public void onClick(View v) {
      clicked = true;
    }
  }

  static class AdapterWithModelClickListener extends AutoEpoxyAdapter {
    final ModelWithClickListener_ model = new ModelWithClickListener_().id(1);

    @Override
    protected void buildModels() {
      add(model);
    }
  }

  @Test
  public void basicModelClickListener() {
    ModelClickListener modelClickListener = spy(new ModelClickListener());
    adapter.model.clickListener(modelClickListener);
    assertNull(adapter.model.clickListener());

    buildModelsAndBind();
    assertNotNull(adapter.model.clickListener());

    View view = new View(activity);
    adapter.model.clickListener().onClick(view);
    assertTrue(modelClickListener.clicked);

    verify(modelClickListener).onClick(eq(adapter.model), any(View.class), nullable(View.class), anyInt());
  }

  @Test
  public void modelClickListenerOverridesViewClickListener() {
    ViewClickListener viewClickListener = new ViewClickListener();
    adapter.model.clickListener(viewClickListener);
    assertNotNull(adapter.model.clickListener());

    ModelClickListener modelClickListener = new ModelClickListener();
    adapter.model.clickListener(modelClickListener);
    assertNull(adapter.model.clickListener());

    buildModelsAndBind();
    assertNotNull(adapter.model.clickListener());

    adapter.model.clickListener().onClick(null);
    assertTrue(modelClickListener.clicked);
    assertFalse(viewClickListener.clicked);
  }

  @Test
  public void viewClickListenerOverridesModelClickListener() {
    ModelClickListener modelClickListener = new ModelClickListener();
    adapter.model.clickListener(modelClickListener);
    assertNull(adapter.model.clickListener());

    ViewClickListener viewClickListener = new ViewClickListener();
    adapter.model.clickListener(viewClickListener);
    assertNotNull(adapter.model.clickListener());

    buildModelsAndBind();
    assertNotNull(adapter.model.clickListener());

    adapter.model.clickListener().onClick(null);
    assertTrue(viewClickListener.clicked);
    assertFalse(modelClickListener.clicked);
  }

  @Test
  public void resetClearsModelClickListener() {
    ModelClickListener modelClickListener = spy(new ModelClickListener());
    adapter.model.clickListener(modelClickListener);
    adapter.model.reset();

    buildModelsAndBind();
    assertNull(adapter.model.clickListener());
  }

  @Test
  public void modelClickListenerIsHashed() {
    // Internally we wrap the model click listener with an anonymous click listener. We can't hash
    // the anonymous click listener since that changes the model state, instead our anonymous
    // click listener should use the hashCode of the user's click listener

    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    adapter.registerAdapterDataObserver(observerMock);

    adapter.requestModelUpdate();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    ModelClickListener modelClickListener = new ModelClickListener();
    adapter.model.clickListener(modelClickListener);
    adapter.requestModelUpdate();

    // The second update shouldn't cause a item change
    adapter.requestModelUpdate();

    ViewClickListener viewClickListener = new ViewClickListener();
    adapter.model.clickListener(viewClickListener);
    adapter.requestModelUpdate();

    verify(observerMock, times(2)).onItemRangeChanged(eq(0), eq(1), eq(null));
    verifyNoMoreInteractions(observerMock);
  }

  @Test
  public void viewClickListenerIsHashed() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    adapter.registerAdapterDataObserver(observerMock);

    adapter.requestModelUpdate();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    ViewClickListener viewClickListener = new ViewClickListener();
    adapter.model.clickListener(viewClickListener);
    adapter.requestModelUpdate();

    // The second update shouldn't cause a item change
    adapter.requestModelUpdate();

    ModelClickListener modelClickListener = new ModelClickListener();
    adapter.model.clickListener(modelClickListener);
    adapter.requestModelUpdate();

    verify(observerMock, times(2)).onItemRangeChanged(eq(0), eq(1), eq(null));
    verifyNoMoreInteractions(observerMock);
  }

  @Test
  public void clearingModelClickListenerAfterBindStillWorks() {
    ModelClickListener modelClickListener = spy(new ModelClickListener());
    adapter.model.clickListener(modelClickListener);

    buildModelsAndBind();
    // Clearing the model click listener and calling the view click listener
    // should still call the original model click listener
    View.OnClickListener viewClickListener = adapter.model.clickListener();
    adapter.model.clickListener((ModelClickListener) null);
    viewClickListener.onClick(null);

    // The original click listener is still called since it was the one bound.
    assertTrue(modelClickListener.clicked);
    verify(modelClickListener).onClick(eq(adapter.model), any(View.class), nullable(View.class), anyInt());
  }
}
