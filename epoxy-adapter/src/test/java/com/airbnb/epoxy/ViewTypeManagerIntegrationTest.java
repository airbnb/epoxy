package com.airbnb.epoxy;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class ViewTypeManagerIntegrationTest {

  @Before
  public void resetViewTypeMap() {
    new ViewTypeManager().resetMapForTesting();
  }

  static class TestModel extends EpoxyModelWithView<View> {
    @Override
    public View buildView(@NonNull ViewGroup parent) {
      return new FrameLayout(ApplicationProvider.getApplicationContext());
    }
  }

  static class ModelWithViewType extends TestModel {

    @Override
    protected int getViewType() {
      return 1;
    }
  }

  static class ModelWithViewType2 extends TestModel {

    @Override
    protected int getViewType() {
      return 2;
    }
  }

  @Test
  public void modelWithLayout() {
    SimpleEpoxyAdapter adapter = new SimpleEpoxyAdapter();
    adapter.addModel(new ModelWithViewType());
    adapter.addModel(new ModelWithViewType2());

    // The view type should be the value declared in the model
    assertEquals(1, adapter.getItemViewType(0));
    assertEquals(2, adapter.getItemViewType(1));
  }

  static class ModelWithoutViewType extends TestModel {}

  static class ModelWithoutViewType2 extends TestModel {}

  static class ModelWithoutViewType3 extends TestModel {}

  @Test
  public void modelsWithoutLayoutHaveViewTypesGenerated() {
    SimpleEpoxyAdapter adapter = new SimpleEpoxyAdapter();

    adapter.addModel(new ModelWithoutViewType());
    adapter.addModel(new ModelWithoutViewType2());
    adapter.addModel(new ModelWithoutViewType3());

    adapter.addModel(new ModelWithoutViewType());
    adapter.addModel(new ModelWithoutViewType2());
    adapter.addModel(new ModelWithoutViewType3());

    // Models with view type 0 should have a view type generated for them
    assertEquals(-1, adapter.getItemViewType(0));
    assertEquals(-2, adapter.getItemViewType(1));
    assertEquals(-3, adapter.getItemViewType(2));
    // Models of same class should share the same generated view type
    assertEquals(-1, adapter.getItemViewType(3));
    assertEquals(-2, adapter.getItemViewType(4));
    assertEquals(-3, adapter.getItemViewType(5));
  }

  @Test
  public void fastModelLookupOfLastModel() {
    SimpleEpoxyAdapter adapter = spy(new SimpleEpoxyAdapter());
    TestModel modelToAdd = spy(new ModelWithoutViewType());
    adapter.addModel(modelToAdd);

    int itemViewType = adapter.getItemViewType(0);

    adapter.onCreateViewHolder(null, itemViewType);

    // onExceptionSwallowed is called if the fast model look up failed
    verify(adapter, never()).onExceptionSwallowed(any(RuntimeException.class));
    verify(modelToAdd).buildView(null);
  }

  @Test
  public void fallbackLookupOfUnknownModel() {
    SimpleEpoxyAdapter adapter = spy(new SimpleEpoxyAdapter());
    TestModel modelToAdd = spy(new ModelWithViewType());
    adapter.addModel(modelToAdd);

    // If we pass a view type that hasn't been looked up recently it should fallback to searching
    // through all models to find a match.
    adapter.onCreateViewHolder(null, 1);

    // onExceptionSwallowed is called when the fast model look up fails
    verify(adapter).onExceptionSwallowed(any(RuntimeException.class));
    verify(modelToAdd).buildView(null);
  }

  @Test
  public void viewTypesSharedAcrossAdapters() {
    SimpleEpoxyAdapter adapter1 = new SimpleEpoxyAdapter();
    SimpleEpoxyAdapter adapter2 = new SimpleEpoxyAdapter();

    adapter1.addModel(new ModelWithoutViewType());
    adapter1.addModel(new ModelWithoutViewType2());

    adapter2.addModel(new ModelWithoutViewType());
    adapter2.addModel(new ModelWithoutViewType2());

    assertEquals(adapter1.getItemViewType(0), adapter2.getItemViewType(0));
    assertEquals(adapter1.getItemViewType(1), adapter2.getItemViewType(1));
  }
}
