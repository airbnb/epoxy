package com.airbnb.epoxy;

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

    // Check that the text was set on the view
    assertEquals(firstModel.stringValue(), ((Button) viewHolder.itemView).getText());

    TestDataBindingModel_ secondModel = new TestDataBindingModel_()
        .stringValue("hello again")
        .id(1);

    controller.setModels(Collections.singletonList(secondModel));
    List<Object> payloads = DiffPayloadTestUtil.payloadsWithChangedModels(firstModel);
    controller.getAdapter().onBindViewHolder(viewHolder, 0, payloads);

    // Check that the text was updated after the change payload
    assertEquals(secondModel.stringValue(), ((Button) viewHolder.itemView).getText());
  }
}
