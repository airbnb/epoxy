package com.airbnb.epoxy;

import android.view.View;

import com.airbnb.epoxy.integrationtest.BuildConfig;
import com.airbnb.epoxy.integrationtest.ModelWithClickListener_;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@LooperMode(LooperMode.Mode.LEGACY)
public class OnModelBindListenerTest {

  private ControllerLifecycleHelper lifecycleHelper = new ControllerLifecycleHelper();

  static class TestController extends EpoxyController {

    private EpoxyModel model;

    @Override
    protected void buildModels() {
      add(model);
    }

    void setModel(EpoxyModel model) {
      this.model = model.id(1);
    }

    void buildWithModel(EpoxyModel model) {
      setModel(model);
      requestModelBuild();
    }
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

  @Test
  public void onBindListenerGetsCalled() {
    TestController controller = new TestController();

    BindListener bindListener = new BindListener();
    ModelWithClickListener_ model = new ModelWithClickListener_().onBind(bindListener);
    controller.setModel(model);

    assertFalse(bindListener.called);
    lifecycleHelper.buildModelsAndBind(controller);
    assertTrue(bindListener.called);
  }

  @Test
  public void onUnbindListenerGetsCalled() {
    TestController controller = new TestController();

    ModelWithClickListener_ model = new ModelWithClickListener_();
    controller.setModel(model);

    UnbindListener unbindlistener = new UnbindListener();
    model.onUnbind(unbindlistener);

    assertFalse(unbindlistener.called);
    lifecycleHelper.buildModelsAndBind(controller);
    assertFalse(unbindlistener.called);

    lifecycleHelper.recycleLastBoundModel(controller);
    assertTrue(unbindlistener.called);
  }

  @Test
  public void bindListenerChangesHashCode() {
    TestController controller = new TestController();

    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    controller.getAdapter().registerAdapterDataObserver(observerMock);

    ModelWithClickListener_ model = new ModelWithClickListener_();
    controller.buildWithModel(model);
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    // shouldn't change
    model = new ModelWithClickListener_();
    model.onBind(null);
    controller.buildWithModel(model);
    verify(observerMock, never()).onItemRangeChanged(eq(0), eq(1), any());

    model = new ModelWithClickListener_();
    BindListener listener1 = new BindListener();
    model.onBind(listener1);
    controller.buildWithModel(model);
    verify(observerMock, times(1)).onItemRangeChanged(eq(0), eq(1), any());

    model = new ModelWithClickListener_();
    model.onBind(listener1);
    controller.buildWithModel(model);
    verify(observerMock, times(1)).onItemRangeChanged(eq(0), eq(1), any());
  }

  @Test
  public void nullBindListenerChangesHashCode() {
    TestController controller = new TestController();

    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    controller.getAdapter().registerAdapterDataObserver(observerMock);

    ModelWithClickListener_ model = new ModelWithClickListener_();
    controller.buildWithModel(model);
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    model = new ModelWithClickListener_();
    model.onBind(new BindListener());
    controller.buildWithModel(model);

    model = new ModelWithClickListener_();
    model.onBind(null);
    controller.buildWithModel(model);

    verify(observerMock, times(2)).onItemRangeChanged(eq(0), eq(1), any());
  }

  @Test
  public void newBindListenerDoesNotChangeHashCode() {
    TestController controller = new TestController();

    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    controller.getAdapter().registerAdapterDataObserver(observerMock);

    ModelWithClickListener_ model = new ModelWithClickListener_();
    controller.buildWithModel(model);
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    model = new ModelWithClickListener_();
    model.onBind(new BindListener());
    controller.buildWithModel(model);

    model = new ModelWithClickListener_();
    model.onBind(new BindListener());
    controller.buildWithModel(model);

    verify(observerMock).onItemRangeChanged(eq(0), eq(1), any());
  }

  @Test
  public void unbindListenerChangesHashCode() {
    TestController controller = new TestController();

    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    controller.getAdapter().registerAdapterDataObserver(observerMock);

    ModelWithClickListener_ model = new ModelWithClickListener_();
    controller.buildWithModel(model);
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    // shouldn't change
    model = new ModelWithClickListener_();
    model.onUnbind(null);
    controller.buildWithModel(model);
    verify(observerMock, never()).onItemRangeChanged(eq(0), eq(1), any());

    model = new ModelWithClickListener_();
    UnbindListener listener1 = new UnbindListener();
    model.onUnbind(listener1);
    controller.buildWithModel(model);
    verify(observerMock, times(1)).onItemRangeChanged(eq(0), eq(1), any());

    model = new ModelWithClickListener_();
    model.onUnbind(listener1);
    controller.buildWithModel(model);
    verify(observerMock, times(1)).onItemRangeChanged(eq(0), eq(1), any());
  }

  @Test
  public void nullUnbindListenerChangesHashCode() {
    TestController controller = new TestController();

    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    controller.getAdapter().registerAdapterDataObserver(observerMock);

    ModelWithClickListener_ model = new ModelWithClickListener_();
    controller.buildWithModel(model);
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    model = new ModelWithClickListener_();
    model.onUnbind(new UnbindListener());
    controller.buildWithModel(model);

    model = new ModelWithClickListener_();
    model.onUnbind(null);
    controller.buildWithModel(model);

    verify(observerMock, times(2)).onItemRangeChanged(eq(0), eq(1), any());
  }

  @Test
  public void newUnbindListenerDoesNotChangHashCode() {
    TestController controller = new TestController();

    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    controller.getAdapter().registerAdapterDataObserver(observerMock);

    ModelWithClickListener_ model = new ModelWithClickListener_();
    controller.buildWithModel(model);
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    model = new ModelWithClickListener_();
    model.onUnbind(new UnbindListener());
    controller.buildWithModel(model);

    model = new ModelWithClickListener_();
    model.onUnbind(new UnbindListener());
    controller.buildWithModel(model);

    verify(observerMock).onItemRangeChanged(eq(0), eq(1), any());
  }
}
