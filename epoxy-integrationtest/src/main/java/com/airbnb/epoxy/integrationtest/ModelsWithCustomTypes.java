package com.airbnb.epoxy.integrationtest;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.EpoxyModelClass;

/**
 * This tests that the annotation processor looks up the correct bound object type on the model when
 * a subclass defines a new type. In this test the generated model should correctly use TextView as
 * the model type instead of the ImageView type of the subclass.
 */
class ModelsWithCustomTypes {

  @EpoxyModelClass
  abstract static class ModelWithCustomType extends BaseModelWithCustomType<ImageView> {

    @Override
    protected int getDefaultLayout() {
      return 0;
    }
  }

  abstract static class BaseModelWithCustomType<U extends View> extends EpoxyModel<TextView> {

    public void testMethod(U param) {

    }

    @Override
    protected int getDefaultLayout() {
      return 0;
    }
  }
}
