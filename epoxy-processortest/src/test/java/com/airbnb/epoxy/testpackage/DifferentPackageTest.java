package com.airbnb.epoxy.testpackage;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.ProcessorTestModel;

import org.junit.Test;

/**
 * A test to check that a model subclass in a different package from its super class does not
 * include package private fields from its super class on its generated model.
 */
public class DifferentPackageTest {

  @Test
  public void differentPackageUsage() {
    new DifferentPackageTest$Model_()
        .publicValue(1)
        .protectedValue(1)
        .subclassValue(1);
  }

  public static class Model extends ProcessorTestModel {
    @EpoxyAttribute int subclassValue;
  }
}
