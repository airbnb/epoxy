package com.airbnb.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation on any class in your module to specify default behavior for the Epoxy
 * annotation processor for that module. You can only have one instance of this annotation per
 * module.
 * <p>
 * If an instance of this annotation is not found in a module then the default values are used.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ModuleEpoxyConfig {
  boolean REQUIRE_HASHCODE_DEFAULT = false;
  boolean REQUIRE_ABSTRACT_MODELS = false;
  /**
   * If true, all fields marked with {@link com.airbnb.epoxy.EpoxyAttribute} must have a type that
   * implements hashCode, or the attribute must set hash=false.
   * <p>
   * Setting this to true is useful for ensuring that all model attributes correctly implement
   * hashCode, or use hash=false (eg for click listeners). It is a common mistake to miss these,
   * which leads to invalid model state and incorrect diffing.
   * <p>
   * The check is done at compile time and compilation will fail if a hashCode validation fails.
   * <p>
   * Since it is done at compile time this can only check the direct type of the field. Interfaces
   * will fail the check even if an implementation of that interface which does implement hashCode
   * is used at runtime.
   * <p>
   * If an attribute is an Iterable or Array then that collection type must implement hashCode.
   * <p>
   * If the attribute type is a class that uses Google AutoValue then the hashCode check will
   * succeed, since it is assumed that a generated subclass of that type will be used.
   */
  boolean requireHashCode() default REQUIRE_HASHCODE_DEFAULT;
  /**
   * If true, all classes that contains {@link com.airbnb.epoxy.EpoxyAttribute} or {@link
   * com.airbnb.epoxy.EpoxyModelClass} annotations in your project must be abstract. Otherwise
   * compilation will fail.
   * <p>
   * Forcing models to be abstract can prevent the mistake of using the original model class instead
   * of the generated class.
   */
  boolean requireAbstractModels() default REQUIRE_ABSTRACT_MODELS;
}
