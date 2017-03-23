package com.airbnb.epoxy;

import android.view.View;

import com.airbnb.epoxy.EpoxyController.Interceptor;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class EpoxyModelValidationTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  static class Controller extends EpoxyController {

    @Override
    protected void buildModels() {

    }
  }

  @Test
  public void testModelCannotBeAddedTwice() {
    thrown.expect(IllegalEpoxyUsage.class);
    thrown.expectMessage("This model was already added to the controller at position 0");
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        Model model = new Model_().id(1);
        model.addTo(this);
        model.addTo(this);
      }
    };

    controller.requestModelBuild();
  }

  @Test
  public void addToOnlyValidInsideBuildModels() {
    thrown.expect(IllegalEpoxyUsage.class);
    thrown.expectMessage("You can only add models inside the `buildModels` methods");

    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
      }
    };

    new Model_()
        .id(1)
        .addTo(controller);
  }

  @Test
  public void addModelOnlyValidInsideBuildModels() {
    thrown.expect(IllegalEpoxyUsage.class);
    thrown.expectMessage("You can only add models inside the `buildModels` methods");

    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
      }
    };

    controller.add(new Model_().id(1));
  }

  @Test
  public void cannotCallBuildModelsDirectly() {
    thrown.expect(IllegalEpoxyUsage.class);
    thrown.expectMessage("Call `requestModelBuild` instead");

    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        new Model_()
            .id(1)
            .addTo(this);
      }
    };

    controller.buildModels();
  }

  @Test
  public void mustSpecifyModelIdInController() {
    thrown.expect(IllegalEpoxyUsage.class);
    thrown.expectMessage("You must set an id on a model");

    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        new Model_()
            .addTo(this);
      }
    };

    controller.requestModelBuild();
  }

  @Test
  public void hideModelNotAllowedInController() {
    thrown.expect(IllegalEpoxyUsage.class);
    thrown.expectMessage("You cannot hide a model");

    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        new Model_()
            .id(1)
            .hide()
            .addTo(this);
      }
    };

    controller.requestModelBuild();
  }

  @Test
  public void cannotChangeIdAfterAddingModelToController() {
    thrown.expect(IllegalEpoxyUsage.class);
    thrown.expectMessage("Cannot change a model's id");

    final Model model = new Model_().id(1);
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        add(model);
      }
    };

    controller.requestModelBuild();

    model.id(2);
  }

  @Test
  public void mutationNotAllowedAfterModelIsAdded_reset() {
    thrown.expect(ImmutableModelException.class);
    thrown.expectMessage("A model cannot be changed");

    final Model model = new Model_().id(1);
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        add(model);
      }
    };

    controller.requestModelBuild();

    model.reset();
  }

  @Test
  public void mutationNotAllowedAfterModelIsAdded_bindListener() {
    thrown.expect(ImmutableModelException.class);
    thrown.expectMessage("A model cannot be changed");

    final Model_ model = new Model_().id(1);
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        add(model);
      }
    };

    controller.requestModelBuild();

    model.onBind(null);
  }

  @Test
  public void mutationNotAllowedAfterModelIsAdded_unbindListener() {
    thrown.expect(ImmutableModelException.class);
    thrown.expectMessage("A model cannot be changed");

    final Model_ model = new Model_().id(1);
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        add(model);
      }
    };

    controller.requestModelBuild();

    model.onUnbind(null);
  }

  @Test
  public void mutationNotAllowedAfterModelIsAdded_setter() {
    thrown.expect(ImmutableModelException.class);
    thrown.expectMessage("A model cannot be changed");

    final Model_ model = new Model_().id(1);
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        add(model);
      }
    };

    controller.requestModelBuild();

    model.value(2);
  }

  @Test
  public void mutationNotAllowedAfterModelIsAdded_layout() {
    thrown.expect(ImmutableModelException.class);
    thrown.expectMessage("A model cannot be changed");

    final Model model = new Model_().id(1);
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        add(model);
      }
    };

    controller.requestModelBuild();

    model.layout(R.layout.view_holder_empty_view);
  }

  @Test
  public void mutationNotAllowedAfterModelIsAdded_hide() {
    thrown.expect(ImmutableModelException.class);
    thrown.expectMessage("A model cannot be changed");

    final Model model = new Model_().id(1);
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        add(model);
      }
    };

    controller.requestModelBuild();

    model.hide();
  }

  @Test
  public void mutationAllowedDuringInterceptorCall() {
    final Model model = new Model_().id(1);
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        add(model);
      }
    };

    controller.addInterceptor(new Interceptor() {
      @Override
      public void intercept(List<EpoxyModel<?>> models) {
        model.reset();
      }
    });

    controller.requestModelBuild();
  }

  @Test
  public void hashChangeThrows_beforeBind() {
    thrown.expect(ImmutableModelException.class);
    thrown.expectMessage("A model cannot be changed");

    final Model model = new Model_().id(1);
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        add(model);
      }
    };

    controller.requestModelBuild();
    model.value = 3;
    new ControllerLifecycleHelper().bindModels(controller);
  }

  static class ModelChangesDuringBind extends EpoxyModel<View> {
    @EpoxyAttribute int value;

    @Override
    protected int getDefaultLayout() {
      return R.layout.model_with_click_listener;
    }

    @Override
    public void bind(View view) {
      super.bind(view);
      value = 3;
    }
  }

  @Test
  public void hashChangeThrows_duringBind() {
    thrown.expect(ImmutableModelException.class);
    thrown.expectMessage("A model cannot be changed");

    final EpoxyModelValidationTest$ModelChangesDuringBind_ model =
        new EpoxyModelValidationTest$ModelChangesDuringBind_().id(1);
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        add(model);
      }
    };

    new ControllerLifecycleHelper().buildModelsAndBind(controller);
  }

  @Test
  public void hashChangeThrows_beforeNextModelBuild() {
    // This only works for controllers with AutoModels since only those have generated helpers
    thrown.expect(ImmutableModelException.class);
    thrown.expectMessage("A model cannot be changed");

    ControllerWithAutoModel controller = new ControllerWithAutoModel();

    controller.requestModelBuild();
    Model model = (Model) controller.getAdapter().getCopyOfModels().get(0);
    model.value = 3;

    controller.requestModelBuild();
  }

  @Test
  public void hashChangeDuringInterceptorIsAllowed() {
    final Model_ model = new Model_().id(1);
    EpoxyController controller = new EpoxyController() {

      @Override
      protected void buildModels() {
        add(model);
      }
    };

    controller.addInterceptor(new Interceptor() {
      @Override
      public void intercept(List<EpoxyModel<?>> models) {
        model.value(3);
      }
    });

    controller.requestModelBuild();
  }
}