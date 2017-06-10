package com.airbnb.epoxy;

import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.airbnb.epoxy.DataBindingEpoxyModel.DataBindingHolder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DataBindingModelIntegrationTest {

  @Test
  public void createDataBindingModel() {
    SimpleEpoxyController controller = new SimpleEpoxyController();
    TestDataBindingModel_ firstModel = new TestDataBindingModel_()
        .stringValue("hello")
        .id(1);

    controller.setModels(Collections.singletonList(firstModel));

    ControllerLifecycleHelper lifecycleHelper = new ControllerLifecycleHelper();
    EpoxyViewHolder viewHolder = lifecycleHelper.createViewHolder(controller.getAdapter(), 0);
    controller.getAdapter().onBindViewHolder(viewHolder, 0);

    DataBindingHolder dataBindingHolder = ((DataBindingHolder) viewHolder.objectToBind());
    assertNotNull(dataBindingHolder.getDataBinding());

    // Check that the requiredText was set on the view
    assertEquals(firstModel.stringValue(), ((Button) viewHolder.itemView).getText());

    TestDataBindingModel_ secondModel = new TestDataBindingModel_()
        .stringValue("hello again")
        .id(1);

    controller.setModels(Collections.singletonList(secondModel));
    List<Object> payloads = DiffPayloadTestUtil.payloadsWithChangedModels(firstModel);
    controller.getAdapter().onBindViewHolder(viewHolder, 0, payloads);

    // Check that the requiredText was updated after the change payload
    assertEquals(secondModel.stringValue(), ((Button) viewHolder.itemView).getText());
  }

  @Test
  public void fullyCreateDataBindingModel() {
    SimpleEpoxyController controller = new SimpleEpoxyController();
    ModelWithDataBindingBindingModel_ firstModel = new ModelWithDataBindingBindingModel_()
        .stringValue("hello")
        .id(1);

    controller.setModels(Collections.singletonList(firstModel));

    ControllerLifecycleHelper lifecycleHelper = new ControllerLifecycleHelper();
    EpoxyViewHolder viewHolder = lifecycleHelper.createViewHolder(controller.getAdapter(), 0);
    controller.getAdapter().onBindViewHolder(viewHolder, 0);

    DataBindingHolder dataBindingHolder = ((DataBindingHolder) viewHolder.objectToBind());
    assertNotNull(dataBindingHolder.getDataBinding());

    // Check that the requiredText was set on the view
    assertEquals(firstModel.stringValue(), ((Button) viewHolder.itemView).getText());

    ModelWithDataBindingBindingModel_ secondModel = new ModelWithDataBindingBindingModel_()
        .stringValue("hello again")
        .id(1);

    controller.setModels(Collections.singletonList(secondModel));
    List<Object> payloads = DiffPayloadTestUtil.payloadsWithChangedModels(firstModel);
    controller.getAdapter().onBindViewHolder(viewHolder, 0, payloads);

    // Check that the requiredText was updated after the change payload
    assertEquals(secondModel.stringValue(), ((Button) viewHolder.itemView).getText());
  }

  @Test
  public void typesWithOutHashCodeAreNotDiffed() {
    SimpleEpoxyController controller = new SimpleEpoxyController();
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    controller.getAdapter().registerAdapterDataObserver(observerMock);

    ModelWithDataBindingBindingModel_ firstModel = new ModelWithDataBindingBindingModel_()
        .clickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {

          }
        })
        .id(1);

    controller.setModels(Collections.singletonList(firstModel));
    verify(observerMock).onItemRangeInserted(0, 1);

    ModelWithDataBindingBindingModel_ secondModel = new ModelWithDataBindingBindingModel_()
        .clickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {

          }
        })
        .id(1);

    controller.setModels(Collections.singletonList(secondModel));
    verifyNoMoreInteractions(observerMock);
  }

  @Test
  public void typesWithHashCodeAreDiffed() {
    SimpleEpoxyController controller = new SimpleEpoxyController();
    AdapterDataObserver observerMock = mock(AdapterDataObserver.class);
    controller.getAdapter().registerAdapterDataObserver(observerMock);

    ModelWithDataBindingBindingModel_ firstModel = new ModelWithDataBindingBindingModel_()
        .stringValue("value1")
        .id(1);

    controller.setModels(Collections.singletonList(firstModel));
    verify(observerMock).onItemRangeInserted(0, 1);

    ModelWithDataBindingBindingModel_ secondModel = new ModelWithDataBindingBindingModel_()
        .stringValue("value2")
        .id(1);

    controller.setModels(Collections.singletonList(secondModel));
    verify(observerMock).onItemRangeChanged(eq(0), eq(1), any());
    verifyNoMoreInteractions(observerMock);
  }
}
