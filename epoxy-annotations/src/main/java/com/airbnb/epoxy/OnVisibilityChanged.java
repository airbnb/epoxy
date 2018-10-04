package com.airbnb.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This can be used to annotate methods inside classes with a {@link ModelView} annotation. Methods
 * with this annotation will be called when the viewport is
 * changed.
 *
 * Inspired from Litho : https://fblitho.com/docs/visibility-handling
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface OnVisibilityChanged {
}
