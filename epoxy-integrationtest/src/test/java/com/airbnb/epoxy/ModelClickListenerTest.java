package com.airbnb.epoxy;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;

import com.airbnb.epoxy.integrationtest.BuildConfig;
import com.airbnb.epoxy.integrationtest.ModelWithCheckedChangeListener_;
import com.airbnb.epoxy.integrationtest.ModelWithClickListener_;
import com.airbnb.epoxy.integrationtest.ModelWithLongClickListener_;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@LooperMode(LooperMode.Mode.LEGACY)
public class ModelClickListenerTest {

  private ControllerLifecycleHelper lifecycleHelper = new ControllerLifecycleHelper();

  static class TestController extends EpoxyController {
    private EpoxyModel<?> model;

    @Override
    protected void buildModels() {
      add(model.id(1));
    }

    void setModel(EpoxyModel<?> model) {
      this.model = model;
    }
  }

  static class ModelClickListener implements OnModelClickListener<ModelWithClickListener_, View> {
    boolean clicked;

    @Override
    public void onClick(ModelWithClickListener_ model, View view, View v, int position) {
      clicked = true;
    }
  }

  static class ModelLongClickListener
      implements OnModelLongClickListener<ModelWithLongClickListener_, View> {
    boolean clicked;

    @Override
    public boolean onLongClick(ModelWithLongClickListener_ model, View view, View v, int position) {
      clicked = true;
      return true;
    }
  }

  static class ModelCheckedChangeListener
      implements OnModelCheckedChangeListener<ModelWithCheckedChangeListener_, View> {
    boolean checked;

    @Override
    public void onChecked(ModelWithCheckedChangeListener_ model, View parentView,
        CompoundButton checkedView, boolean isChecked, int position) {
      checked = true;
    }
  }

  static class ViewClickListener implements OnClickListener {
    boolean clicked;

    @Override
    public void onClick(View v) {
      clicked = true;
    }
  }

  @Test
  public void basicModelClickListener() {
    final ModelWithClickListener_ model = new ModelWithClickListener_();
    ModelClickListener modelClickListener = spy(new ModelClickListener());
    model.clickListener(modelClickListener);

    TestController controller = new TestController();
    controller.setModel(model);

    lifecycleHelper.buildModelsAndBind(controller);

    View viewMock = mockModelForClicking(model);

    model.clickListener().onClick(viewMock);
    assertTrue(modelClickListener.clicked);

    verify(modelClickListener).onClick(eq(model), any(View.class), eq(viewMock), eq(1));
  }

  private View mockModelForClicking(EpoxyModel model) {
    View mockedView = mock(View.class);
    RecyclerView recyclerMock = mock(RecyclerView.class);
    EpoxyViewHolder holderMock = mock(EpoxyViewHolder.class);

    when(holderMock.getAdapterPosition()).thenReturn(1);
    doReturn(recyclerMock).when(mockedView).getParent();
    doReturn(holderMock).when(recyclerMock).findContainingViewHolder(mockedView);
    doReturn(model).when(holderMock).getModel();

    when(mockedView.getParent()).thenReturn(recyclerMock);
    when(recyclerMock.findContainingViewHolder(mockedView)).thenReturn(holderMock);
    when(holderMock.getAdapterPosition()).thenReturn(1);
    when(holderMock.getModel()).thenReturn(model);

    View parentView = mock(View.class);
    when(holderMock.objectToBind()).thenReturn(parentView);
    doReturn(parentView).when(holderMock).objectToBind();
    return mockedView;
  }

  @Test
  public void basicModelLongClickListener() {
    final ModelWithLongClickListener_ model = new ModelWithLongClickListener_();
    ModelLongClickListener modelClickListener = spy(new ModelLongClickListener());
    model.clickListener(modelClickListener);

    TestController controller = new TestController();
    controller.setModel(model);

    lifecycleHelper.buildModelsAndBind(controller);

    View viewMock = mockModelForClicking(model);

    model.clickListener().onLongClick(viewMock);
    assertTrue(modelClickListener.clicked);

    verify(modelClickListener).onLongClick(eq(model), any(View.class), eq(viewMock), eq(1));
  }

  @Test
  public void basicModelCheckedChangeListener() {
    final ModelWithCheckedChangeListener_ model = new ModelWithCheckedChangeListener_();
    ModelCheckedChangeListener modelCheckedChangeListener = spy(new ModelCheckedChangeListener());
    model.checkedChangeListener(modelCheckedChangeListener);

    TestController controller = new TestController();
    controller.setModel(model);

    lifecycleHelper.buildModelsAndBind(controller);

    CompoundButton compoundMock = mockCompoundButtonForClicking(model);

    model.checkedChangeListener().onCheckedChanged(compoundMock, true);
    assertTrue(modelCheckedChangeListener.checked);

    verify(modelCheckedChangeListener).onChecked(eq(model), any(View.class), any(CompoundButton.class), eq(true), eq(1));
  }

  private CompoundButton mockCompoundButtonForClicking(EpoxyModel model) {
    CompoundButton mockedView = mock(CompoundButton.class);
    RecyclerView recyclerMock = mock(RecyclerView.class);
    EpoxyViewHolder holderMock = mock(EpoxyViewHolder.class);

    when(holderMock.getAdapterPosition()).thenReturn(1);
    doReturn(recyclerMock).when(mockedView).getParent();
    doReturn(holderMock).when(recyclerMock).findContainingViewHolder(mockedView);
    doReturn(model).when(holderMock).getModel();

    when(mockedView.getParent()).thenReturn(recyclerMock);
    when(recyclerMock.findContainingViewHolder(mockedView)).thenReturn(holderMock);
    when(holderMock.getAdapterPosition()).thenReturn(1);
    when(holderMock.getModel()).thenReturn(model);

    View parentView = mock(View.class);
    when(holderMock.objectToBind()).thenReturn(parentView);
    doReturn(parentView).when(holderMock).objectToBind();
    return mockedView;
  }

  @Test
  public void modelClickListenerOverridesViewClickListener() {
    final ModelWithClickListener_ model = new ModelWithClickListener_();

    TestController controller = new TestController();
    controller.setModel(model);

    ViewClickListener viewClickListener = new ViewClickListener();
    model.clickListener(viewClickListener);
    assertNotNull(model.clickListener());

    ModelClickListener modelClickListener = new ModelClickListener();
    model.clickListener(modelClickListener);
    assertNotSame(model.clickListener(), viewClickListener);

    lifecycleHelper.buildModelsAndBind(controller);
    mockModelForClicking(model);
    assertNotNull(model.clickListener());
    View viewMock = mockModelForClicking(model);

    model.clickListener().onClick(viewMock);
    assertTrue(modelClickListener.clicked);
    assertFalse(viewClickListener.clicked);
  }

  @Test
  public void viewClickListenerOverridesModelClickListener() {
    final ModelWithClickListener_ model = new ModelWithClickListener_();

    TestController controller = new TestController();
    controller.setModel(model);

    ModelClickListener modelClickListener = new ModelClickListener();
    model.clickListener(modelClickListener);

    ViewClickListener viewClickListener = new ViewClickListener();
    model.clickListener(viewClickListener);

    lifecycleHelper.buildModelsAndBind(controller);
    assertNotNull(model.clickListener());

    model.clickListener().onClick(null);
    assertTrue(viewClickListener.clicked);
    assertFalse(modelClickListener.clicked);
  }

  @Test
  public void resetClearsModelClickListener() {
    final ModelWithClickListener_ model = new ModelWithClickListener_();

    TestController controller = new TestController();
    controller.setModel(model);

    ModelClickListener modelClickListener = spy(new ModelClickListener());
    model.clickListener(modelClickListener);
    model.reset();

    lifecycleHelper.buildModelsAndBind(controller);
    assertNull(model.clickListener());
  }

  @Test
  public void modelClickListenerIsDiffed() {
    // Internally we wrap the model click listener with an anonymous click listener. We can't hash
    // the anonymous click listener since that changes the model state, instead our anonymous
    // click listener should use the hashCode of the user's click listener

    ModelClickListener modelClickListener = new ModelClickListener();
    ViewClickListener viewClickListener = new ViewClickListener();

    TestController controller = new TestController();

    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    controller.getAdapter().registerAdapterDataObserver(observerMock);

    ModelWithClickListener_ model = new ModelWithClickListener_();
    controller.setModel(model);
    controller.requestModelBuild();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    model = new ModelWithClickListener_();
    model.clickListener(modelClickListener);
    controller.setModel(model);
    lifecycleHelper.buildModelsAndBind(controller);

    // The second update shouldn't cause a item change
    model = new ModelWithClickListener_();
    model.clickListener(modelClickListener);
    controller.setModel(model);
    lifecycleHelper.buildModelsAndBind(controller);

    model = new ModelWithClickListener_();
    model.clickListener(viewClickListener);
    controller.setModel(model);
    lifecycleHelper.buildModelsAndBind(controller);

    verify(observerMock, times(2)).onItemRangeChanged(eq(0), eq(1), any());
    verifyNoMoreInteractions(observerMock);
  }

  @Test
  public void viewClickListenerIsDiffed() {
    TestController controller = new TestController();

    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    controller.getAdapter().registerAdapterDataObserver(observerMock);

    ModelWithClickListener_ model = new ModelWithClickListener_();
    controller.setModel(model);
    controller.requestModelBuild();
    verify(observerMock).onItemRangeInserted(eq(0), eq(1));

    ViewClickListener viewClickListener = new ViewClickListener();
    model = new ModelWithClickListener_();
    model.clickListener(viewClickListener);
    controller.setModel(model);
    controller.requestModelBuild();

    // The second update shouldn't cause a item change
    model = new ModelWithClickListener_();
    model.clickListener(viewClickListener);
    controller.setModel(model);
    controller.requestModelBuild();

    ModelClickListener modelClickListener = new ModelClickListener();
    model = new ModelWithClickListener_();
    model.clickListener(modelClickListener);
    controller.setModel(model);
    controller.requestModelBuild();

    verify(observerMock, times(2)).onItemRangeChanged(eq(0), eq(1), any());
    verifyNoMoreInteractions(observerMock);
  }
}
