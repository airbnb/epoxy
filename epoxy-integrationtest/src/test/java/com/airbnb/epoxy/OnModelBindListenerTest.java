package com.airbnb.epoxy;

import android.app.Activity;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.view.View;
import android.widget.FrameLayout;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class OnModelBindListenerTest {
  final Activity activity = Robolectric.setupActivity(Activity.class);
  final AdapterWithModelClickListener adapter = new AdapterWithModelClickListener();
  EpoxyViewHolder boundViewHolder;

  private void buildModelsAndBind() {
    adapter.requestModelUpdate();
    // We can create the model and bind it
    boundViewHolder = adapter.onCreateViewHolder(new FrameLayout(activity),
        adapter.model.getLayout());

    adapter.onBindViewHolder(boundViewHolder, 0);
  }

  private void recycleModel() {
    adapter.onViewRecycled(boundViewHolder);
    boundViewHolder = null;
  }

  static class BindListener implements OnModelBoundListener<ModelWithClickListener_, View> {
    boolean called;

    @Override
    public void onModelBound(ModelWithClickListener_ model, View view) {
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

  static class AdapterWithModelClickListener extends AutoEpoxyAdapter {
    final ModelWithClickListener_ model = new ModelWithClickListener_().id(1);

    @Override
    protected void buildModels() {
      add(model);
    }
  }

  @Test
  public void onBindListenerGetsCalled() {
    BindListener bindListener = new BindListener();
    adapter.model.onBind(bindListener);

    assertFalse(bindListener.called);
    buildModelsAndBind();
    assertTrue(bindListener.called);
  }

  @Test
  public void onUnbindListenerGetsCalled() {
    UnbindListener unbindlistener = new UnbindListener();
    adapter.model.onUnbind(unbindlistener);

    assertFalse(unbindlistener.called);
    buildModelsAndBind();
    assertFalse(unbindlistener.called);

    recycleModel();
    assertTrue(unbindlistener.called);
  }

  @Test
  public void bindListenerChangesHashCode() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    adapter.registerAdapterDataObserver(observerMock);

    adapter.requestModelUpdate();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    // shouldn't change
    adapter.model.onBind(null);
    adapter.requestModelUpdate();
    verify(observerMock, never()).onItemRangeChanged(eq(0), eq(1), eq(null));

    BindListener listener1 = new BindListener();
    adapter.model.onBind(listener1);
    adapter.requestModelUpdate();
    verify(observerMock, times(1)).onItemRangeChanged(eq(0), eq(1), eq(null));

    adapter.model.onBind(listener1);
    adapter.requestModelUpdate();
    verify(observerMock, times(1)).onItemRangeChanged(eq(0), eq(1), eq(null));
  }

  @Test
  public void nullBindListenerChangesHashCode() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    adapter.registerAdapterDataObserver(observerMock);

    adapter.requestModelUpdate();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    adapter.model.onBind(new BindListener());
    adapter.requestModelUpdate();

    adapter.model.onBind(null);
    adapter.requestModelUpdate();

    verify(observerMock, times(2)).onItemRangeChanged(eq(0), eq(1), eq(null));
  }

  @Test
  public void newBindListenerDoesNotChangeHashCode() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    adapter.registerAdapterDataObserver(observerMock);

    adapter.requestModelUpdate();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    adapter.model.onBind(new BindListener());
    adapter.requestModelUpdate();

    adapter.model.onBind(new BindListener());
    adapter.requestModelUpdate();

    verify(observerMock).onItemRangeChanged(eq(0), eq(1), eq(null));
  }

  @Test
  public void unbindListenerChangesHashCode() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    adapter.registerAdapterDataObserver(observerMock);

    adapter.requestModelUpdate();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    // shouldn't change
    adapter.model.onUnbind(null);
    adapter.requestModelUpdate();
    verify(observerMock, never()).onItemRangeChanged(eq(0), eq(1), eq(null));

    UnbindListener listener1 = new UnbindListener();
    adapter.model.onUnbind(listener1);
    adapter.requestModelUpdate();
    verify(observerMock, times(1)).onItemRangeChanged(eq(0), eq(1), eq(null));

    adapter.model.onUnbind(listener1);
    adapter.requestModelUpdate();
    verify(observerMock, times(1)).onItemRangeChanged(eq(0), eq(1), eq(null));
  }

  @Test
  public void nullUnbindListenerChangesHashCode() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    adapter.registerAdapterDataObserver(observerMock);

    adapter.requestModelUpdate();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    adapter.model.onUnbind(new UnbindListener());
    adapter.requestModelUpdate();

    adapter.model.onUnbind(null);
    adapter.requestModelUpdate();

    verify(observerMock, times(2)).onItemRangeChanged(eq(0), eq(1), eq(null));
  }

  @Test
  public void newUnbindListenerDoesNotChangHashCode() {
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    adapter.registerAdapterDataObserver(observerMock);

    adapter.requestModelUpdate();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    adapter.model.onUnbind(new UnbindListener());
    adapter.requestModelUpdate();

    adapter.model.onUnbind(new UnbindListener());
    adapter.requestModelUpdate();

    verify(observerMock).onItemRangeChanged(eq(0), eq(1), eq(null));
  }
}
