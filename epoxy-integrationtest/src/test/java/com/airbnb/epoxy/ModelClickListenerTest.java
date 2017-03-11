package com.airbnb.epoxy;

import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
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

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ModelClickListenerTest {
  final AdapterWithModelClickListener controller = new AdapterWithModelClickListener();

  private void buildModelsAndBind() {
    controller.requestModelBuild();
    // We can create the model and bind it
    Adapter adapter = controller.getAdapter();
    EpoxyViewHolder viewHolder =
        ((EpoxyViewHolder) adapter
            .onCreateViewHolder(new FrameLayout(RuntimeEnvironment.application),
                controller.model.getLayout()));

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

  static class AdapterWithModelClickListener extends EpoxyController {
    final ModelWithClickListener_ model = new ModelWithClickListener_().id(1);

    @Override
    protected void buildModels() {
      add(model);
    }
  }

  @Test
  public void basicModelClickListener() {
    ModelClickListener modelClickListener = spy(new ModelClickListener());
    controller.model.clickListener(modelClickListener);
    assertNull(controller.model.clickListener());

    buildModelsAndBind();
    assertNotNull(controller.model.clickListener());

    View view = new View(RuntimeEnvironment.application);
    controller.model.clickListener().onClick(view);
    assertTrue(modelClickListener.clicked);

    verify(modelClickListener)
        .onClick(eq(controller.model), any(View.class), nullable(View.class), anyInt());
  }

  @Test
  public void modelClickListenerOverridesViewClickListener() {
    ViewClickListener viewClickListener = new ViewClickListener();
    controller.model.clickListener(viewClickListener);
    assertNotNull(controller.model.clickListener());

    ModelClickListener modelClickListener = new ModelClickListener();
    controller.model.clickListener(modelClickListener);
    assertNull(controller.model.clickListener());

    buildModelsAndBind();
    assertNotNull(controller.model.clickListener());

    controller.model.clickListener().onClick(null);
    assertTrue(modelClickListener.clicked);
    assertFalse(viewClickListener.clicked);
  }

  @Test
  public void viewClickListenerOverridesModelClickListener() {
    ModelClickListener modelClickListener = new ModelClickListener();
    controller.model.clickListener(modelClickListener);
    assertNull(controller.model.clickListener());

    ViewClickListener viewClickListener = new ViewClickListener();
    controller.model.clickListener(viewClickListener);
    assertNotNull(controller.model.clickListener());

    buildModelsAndBind();
    assertNotNull(controller.model.clickListener());

    controller.model.clickListener().onClick(null);
    assertTrue(viewClickListener.clicked);
    assertFalse(modelClickListener.clicked);
  }

  @Test
  public void resetClearsModelClickListener() {
    ModelClickListener modelClickListener = spy(new ModelClickListener());
    controller.model.clickListener(modelClickListener);
    controller.model.reset();

    buildModelsAndBind();
    assertNull(controller.model.clickListener());
  }

  @Test
  public void modelClickListenerIsHashed() {
    // Internally we wrap the model click listener with an anonymous click listener. We can't hash
    // the anonymous click listener since that changes the model state, instead our anonymous
    // click listener should use the hashCode of the user's click listener

    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    controller.getAdapter().registerAdapterDataObserver(observerMock);

    controller.requestModelBuild();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    ModelClickListener modelClickListener = new ModelClickListener();
    controller.model.clickListener(modelClickListener);
    controller.requestModelBuild();

    // The second update shouldn't cause a item change
    controller.requestModelBuild();

    ViewClickListener viewClickListener = new ViewClickListener();
    controller.model.clickListener(viewClickListener);
    controller.requestModelBuild();

    verify(observerMock, times(2)).onItemRangeChanged(eq(0), eq(1), eq(null));
    verifyNoMoreInteractions(observerMock);
  }

  @Test
  public void viewClickListenerIsHashed() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    controller.getAdapter().registerAdapterDataObserver(observerMock);

    controller.requestModelBuild();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    ViewClickListener viewClickListener = new ViewClickListener();
    controller.model.clickListener(viewClickListener);
    controller.requestModelBuild();

    // The second update shouldn't cause a item change
    controller.requestModelBuild();

    ModelClickListener modelClickListener = new ModelClickListener();
    controller.model.clickListener(modelClickListener);
    controller.requestModelBuild();

    verify(observerMock, times(2)).onItemRangeChanged(eq(0), eq(1), eq(null));
    verifyNoMoreInteractions(observerMock);
  }

  @Test
  public void clearingModelClickListenerAfterBindStillWorks() {
    ModelClickListener modelClickListener = spy(new ModelClickListener());
    controller.model.clickListener(modelClickListener);

    buildModelsAndBind();
    // Clearing the model click listener and calling the view click listener
    // should still call the original model click listener
    View.OnClickListener viewClickListener = controller.model.clickListener();
    controller.model.clickListener((ModelClickListener) null);
    viewClickListener.onClick(null);

    // The original click listener is still called since it was the one bound.
    assertTrue(modelClickListener.clicked);
    verify(modelClickListener)
        .onClick(eq(controller.model), any(View.class), nullable(View.class), anyInt());
  }
}
