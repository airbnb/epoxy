package com.airbnb.epoxy;

import android.support.v7.widget.RecyclerView.AdapterDataObserver;

import com.airbnb.epoxy.EpoxyController.Interceptor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class EpoxyControllerTest {

  List<EpoxyModel<?>> savedModels;
  boolean noExceptionsDuringBasicBuildModels = true;

  @Test
  public void basicBuildModels() {
    AdapterDataObserver observer = mock(AdapterDataObserver.class);
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        new TestModel()
            .addTo(this);
      }

      @Override
      protected void onExceptionSwallowed(RuntimeException exception) {
        noExceptionsDuringBasicBuildModels = false;
      }
    };

    controller.getAdapter().registerAdapterDataObserver(observer);
    controller.requestModelBuild();

    assertTrue(noExceptionsDuringBasicBuildModels);
    assertEquals(1, controller.getAdapter().getItemCount());
    verify(observer).onItemRangeInserted(0, 1);
    verifyNoMoreInteractions(observer);
  }

  @Test(expected = IllegalStateException.class)
  public void addingSameModelTwiceThrows() {
    final TestModel model = new TestModel();

    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        add(model);
        add(model);
      }
    };

    controller.requestModelBuild();
  }

  @Test
  public void filterDuplicates() {
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        new TestModel()
            .id(1)
            .addTo(this);

        new TestModel()
            .id(1)
            .addTo(this);
      }
    };

    controller.setFilterDuplicates(true);
    controller.requestModelBuild();

    assertEquals(1, controller.getAdapter().getItemCount());
  }

  @Test(expected = IllegalStateException.class)
  public void throwOnDuplicatesIfNotFiltering() {
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        new TestModel()
            .id(1)
            .addTo(this);

        new TestModel()
            .id(1)
            .addTo(this);
      }
    };

    controller.requestModelBuild();
  }

  boolean exceptionSwallowed;

  @Test
  public void exceptionSwallowedWhenDuplicateFiltered() {
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        new TestModel()
            .id(1)
            .addTo(this);

        new TestModel()
            .id(1)
            .addTo(this);
      }

      @Override
      protected void onExceptionSwallowed(RuntimeException exception) {
        exceptionSwallowed = true;
      }
    };

    controller.setFilterDuplicates(true);
    controller.requestModelBuild();

    assertTrue(exceptionSwallowed);
  }

  boolean interceptorCalled;

  @Test
  public void interceptorRunsAfterBuildModels() {
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        new TestModel()
            .addTo(this);
      }
    };

    controller.addInterceptor(new Interceptor() {
      @Override
      public void intercept(List<EpoxyModel<?>> models) {
        assertEquals(1, models.size());
        interceptorCalled = true;
      }
    });

    controller.requestModelBuild();

    assertTrue(interceptorCalled);
    assertEquals(1, controller.getAdapter().getItemCount());
  }

  @Test
  public void interceptorCanAddModels() {
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        new TestModel()
            .addTo(this);
      }
    };

    controller.addInterceptor(new Interceptor() {
      @Override
      public void intercept(List<EpoxyModel<?>> models) {
        models.add(new TestModel());
      }
    });

    controller.requestModelBuild();

    assertEquals(2, controller.getAdapter().getItemCount());
  }

  @Test(expected = IllegalStateException.class)
  public void savedModelsCannotBeAddedToLater() {
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        new TestModel()
            .addTo(this);
      }
    };

    controller.addInterceptor(new Interceptor() {
      @Override
      public void intercept(List<EpoxyModel<?>> models) {
        savedModels = models;
      }
    });

    controller.requestModelBuild();

    savedModels.add(new TestModel());
  }

  @Test
  public void interceptorCanModifyModels() {
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        new TestModel()
            .addTo(this);
      }
    };

    controller.addInterceptor(new Interceptor() {
      @Override
      public void intercept(List<EpoxyModel<?>> models) {
        TestModel model = ((TestModel) models.get(0));
        model.value(model.value() + 1);
      }
    });

    controller.requestModelBuild();
  }

  @Test
  public void interceptorsRunInOrderAdded() {
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        new TestModel()
            .addTo(this);
      }
    };

    controller.addInterceptor(new Interceptor() {
      @Override
      public void intercept(List<EpoxyModel<?>> models) {
        assertEquals(1, models.size());
        models.add(new TestModel());
      }
    });

    controller.addInterceptor(new Interceptor() {
      @Override
      public void intercept(List<EpoxyModel<?>> models) {
        assertEquals(2, models.size());
        models.add(new TestModel());
      }
    });

    controller.requestModelBuild();

    assertEquals(3, controller.getAdapter().getItemCount());
  }

  @Test
  public void moveModel() {
    AdapterDataObserver observer = mock(AdapterDataObserver.class);
    final List<TestModel> testModels = new ArrayList<>();
    testModels.add(new TestModel(1));
    testModels.add(new TestModel(2));
    testModels.add(new TestModel(3));

    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        add(testModels);
      }
    };

    EpoxyControllerAdapter adapter = controller.getAdapter();
    adapter.registerAdapterDataObserver(observer);
    controller.requestModelBuild();

    verify(observer).onItemRangeInserted(0, 3);

    testModels.add(0, testModels.remove(1));

    controller.moveModel(1, 0);
    verify(observer).onItemRangeMoved(1, 0, 1);

    assertEquals(testModels, adapter.getCurrentModels());

    controller.requestModelBuild();
    assertEquals(testModels, adapter.getCurrentModels());
    verifyNoMoreInteractions(observer);
  }

  @Test
  public void moveModelOtherWay() {
    AdapterDataObserver observer = mock(AdapterDataObserver.class);
    final List<TestModel> testModels = new ArrayList<>();
    testModels.add(new TestModel(1));
    testModels.add(new TestModel(2));
    testModels.add(new TestModel(3));

    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        add(testModels);
      }
    };

    EpoxyControllerAdapter adapter = controller.getAdapter();
    adapter.registerAdapterDataObserver(observer);
    controller.requestModelBuild();

    verify(observer).onItemRangeInserted(0, 3);

    testModels.add(2, testModels.remove(1));

    controller.moveModel(1, 2);
    verify(observer).onItemRangeMoved(1, 2, 1);

    assertEquals(testModels, adapter.getCurrentModels());

    controller.requestModelBuild();
    assertEquals(testModels, adapter.getCurrentModels());
    verifyNoMoreInteractions(observer);
  }

  @Test
  public void multipleMoves() {
    AdapterDataObserver observer = mock(AdapterDataObserver.class);
    final List<TestModel> testModels = new ArrayList<>();
    testModels.add(new TestModel(1));
    testModels.add(new TestModel(2));
    testModels.add(new TestModel(3));

    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        add(testModels);
      }
    };

    EpoxyControllerAdapter adapter = controller.getAdapter();
    adapter.registerAdapterDataObserver(observer);
    controller.requestModelBuild();

    testModels.add(0, testModels.remove(1));
    controller.moveModel(1, 0);
    verify(observer).onItemRangeMoved(1, 0, 1);

    testModels.add(2, testModels.remove(1));
    controller.moveModel(1, 2);
    verify(observer).onItemRangeMoved(1, 2, 1);

    assertEquals(testModels, adapter.getCurrentModels());
    controller.requestModelBuild();
    assertEquals(testModels, adapter.getCurrentModels());
  }

  @Test
  public void testDuplicateFilteringDisabledByDefault() {
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {

      }
    };

    assertFalse(controller.isDuplicateFilteringEnabled());
  }

  @Test
  public void testDuplicateFilteringCanBeToggled() {
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {

      }
    };

    assertFalse(controller.isDuplicateFilteringEnabled());

    controller.setFilterDuplicates(true);
    assertTrue(controller.isDuplicateFilteringEnabled());

    controller.setFilterDuplicates(false);
    assertFalse(controller.isDuplicateFilteringEnabled());
  }

  @Test
  public void testGlobalDuplicateFilteringDefault() {
    EpoxyController.setGlobalDuplicateFilteringDefault(true);

    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {

      }
    };

    assertTrue(controller.isDuplicateFilteringEnabled());

    controller.setFilterDuplicates(false);
    assertFalse(controller.isDuplicateFilteringEnabled());

    controller.setFilterDuplicates(true);
    assertTrue(controller.isDuplicateFilteringEnabled());

    // Reset static field for future tests
    EpoxyController.setGlobalDuplicateFilteringDefault(false);
  }

  @Test
  public void testDebugLoggingCanBeToggled() {
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {

      }
    };

    assertFalse(controller.isDebugLoggingEnabled());

    controller.setDebugLoggingEnabled(true);
    assertTrue(controller.isDebugLoggingEnabled());

    controller.setDebugLoggingEnabled(false);
    assertFalse(controller.isDebugLoggingEnabled());
  }

  @Test
  public void testGlobalDebugLoggingDefault() {
    EpoxyController.setGlobalDebugLoggingEnabled(true);

    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {

      }
    };

    assertTrue(controller.isDebugLoggingEnabled());

    controller.setDebugLoggingEnabled(false);
    assertFalse(controller.isDebugLoggingEnabled());

    controller.setDebugLoggingEnabled(true);
    assertTrue(controller.isDebugLoggingEnabled());

    // Reset static field for future tests
    EpoxyController.setGlobalDebugLoggingEnabled(false);
  }
}