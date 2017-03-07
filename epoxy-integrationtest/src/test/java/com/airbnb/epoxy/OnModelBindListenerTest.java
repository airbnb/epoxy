package com.airbnb.epoxy;

import android.app.Activity;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.view.View;
import android.widget.FrameLayout;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class OnModelBindListenerTest {
  final Activity activity = Robolectric.setupActivity(Activity.class);
  final AdapterWithModelClickListener controller = new AdapterWithModelClickListener();
  EpoxyViewHolder boundViewHolder;

  private void buildModelsAndBind() {
    controller.requestModelBuild();
    // We can create the model and bind it
    Adapter adapter = controller.getAdapter();
    boundViewHolder = ((EpoxyViewHolder) adapter.onCreateViewHolder(new FrameLayout(activity),
        controller.model.getLayout()));

    adapter.onBindViewHolder(boundViewHolder, 0);
  }

  private void recycleModel() {
    controller.getAdapter().onViewRecycled(boundViewHolder);
    boundViewHolder = null;
  }

  static class BindListener implements OnModelBoundListener<ModelWithClickListener_, View> {
    boolean called;

    @Override
    public void onModelBound(ModelWithClickListener_ model, View view, int position) {
      called = true;
    }
  }

  static class UnbindListener implements OnModelUnboundListener<ModelWithClickListener_, View> {
    boolean called;

    @Override
    public void onModelUnbound(ModelWithClickListener_ model, View view) {
      called = true;
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
  public void onBindListenerGetsCalled() {
    BindListener bindListener = new BindListener();
    controller.model.onBind(bindListener);

    assertFalse(bindListener.called);
    buildModelsAndBind();
    assertTrue(bindListener.called);
  }

  @Test
  public void onUnbindListenerGetsCalled() {
    UnbindListener unbindlistener = new UnbindListener();
    controller.model.onUnbind(unbindlistener);

    assertFalse(unbindlistener.called);
    buildModelsAndBind();
    assertFalse(unbindlistener.called);

    recycleModel();
    assertTrue(unbindlistener.called);
  }

  @Test
  public void bindListenerChangesHashCode() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    controller.registerAdapterDataObserver(observerMock);

    controller.requestModelBuild();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    // shouldn't change
    controller.model.onBind(null);
    controller.requestModelBuild();
    verify(observerMock, never()).onItemRangeChanged(eq(0), eq(1), eq(null));

    BindListener listener1 = new BindListener();
    controller.model.onBind(listener1);
    controller.requestModelBuild();
    verify(observerMock, times(1)).onItemRangeChanged(eq(0), eq(1), eq(null));

    controller.model.onBind(listener1);
    controller.requestModelBuild();
    verify(observerMock, times(1)).onItemRangeChanged(eq(0), eq(1), eq(null));
  }

  @Test
  public void nullBindListenerChangesHashCode() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    controller.registerAdapterDataObserver(observerMock);

    controller.requestModelBuild();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    controller.model.onBind(new BindListener());
    controller.requestModelBuild();

    controller.model.onBind(null);
    controller.requestModelBuild();

    verify(observerMock, times(2)).onItemRangeChanged(eq(0), eq(1), eq(null));
  }

  @Test
  public void newBindListenerDoesNotChangeHashCode() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    controller.registerAdapterDataObserver(observerMock);

    controller.requestModelBuild();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    controller.model.onBind(new BindListener());
    controller.requestModelBuild();

    controller.model.onBind(new BindListener());
    controller.requestModelBuild();

    verify(observerMock).onItemRangeChanged(eq(0), eq(1), eq(null));
  }

  @Test
  public void unbindListenerChangesHashCode() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    controller.registerAdapterDataObserver(observerMock);

    controller.requestModelBuild();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    // shouldn't change
    controller.model.onUnbind(null);
    controller.requestModelBuild();
    verify(observerMock, never()).onItemRangeChanged(eq(0), eq(1), eq(null));

    UnbindListener listener1 = new UnbindListener();
    controller.model.onUnbind(listener1);
    controller.requestModelBuild();
    verify(observerMock, times(1)).onItemRangeChanged(eq(0), eq(1), eq(null));

    controller.model.onUnbind(listener1);
    controller.requestModelBuild();
    verify(observerMock, times(1)).onItemRangeChanged(eq(0), eq(1), eq(null));
  }

  @Test
  public void nullUnbindListenerChangesHashCode() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    controller.registerAdapterDataObserver(observerMock);

    controller.requestModelBuild();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    controller.model.onUnbind(new UnbindListener());
    controller.requestModelBuild();

    controller.model.onUnbind(null);
    controller.requestModelBuild();

    verify(observerMock, times(2)).onItemRangeChanged(eq(0), eq(1), eq(null));
  }

  @Test
  public void newUnbindListenerDoesNotChangHashCode() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    controller.registerAdapterDataObserver(observerMock);

    controller.requestModelBuild();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    controller.model.onUnbind(new UnbindListener());
    controller.requestModelBuild();

    controller.model.onUnbind(new UnbindListener());
    controller.requestModelBuild();

    verify(observerMock).onItemRangeChanged(eq(0), eq(1), eq(null));
  }
}
