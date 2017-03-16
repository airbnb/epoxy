package com.airbnb.epoxy;

import android.support.v7.widget.RecyclerView.AdapterDataObserver;

import com.airbnb.epoxy.EpoxyController.Interceptor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@Config(sdk = 21, manifest = TestRunner.MANIFEST_PATH)
@RunWith(TestRunner.class)
public class EpoxyControllerTest {

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
  public void interceptorCanChangeModels() {
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

  List<EpoxyModel<?>> savedModels;

  @Test(expected = IllegalStateException.class)
  public void interceptorCannotAddModelsLater() {
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
}