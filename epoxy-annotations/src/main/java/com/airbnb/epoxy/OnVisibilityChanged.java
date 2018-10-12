package com.airbnb.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This can be used to annotate methods inside classes with a {@link ModelView} annotation. Methods
 * with this annotation will be called when visibility part of the view change.
 * <p>
 * Annotated methods should follow this signature :
 * `@OnVisibilityStateChange
 * public void method(@VisibilityState int state)`
 * <p>
 * The equivalent methods on the model is {@link EpoxyModel#onVisibilityChanged}
 * @see OnModelVisibilityChangedListener
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface OnVisibilityChanged {
}
