package com.airbnb.epoxy.testpackage;

import org.junit.Test;
import com.airbnb.integrationtest.processortest.differentpackage.Model_;

/**
 * A test to check that a model subclass in a different package from its super class does not
 * include package private fields from its super class on its generated model.
 * <p>
 * This also tests that model superclasses in different modules have their attributes included in
 * the generated class too.
 */
public class DifferentPackageTest {

  @Test
  public void differentPackageUsage() {
    new Model_()
        .subclassValue(1)
        // Attributes from super class in same module, different package.
        .publicValue(1)
        .protectedValue(1)
        // Inherited attributes from a subclass in a different module
        .processorTest2ValueProtected(3)
        .processorTest2ValuePublic(3);
  }
}
