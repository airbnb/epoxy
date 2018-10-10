package com.airbnb.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This can be used to annotate methods inside classes with a {@link ModelView} annotation. Methods
 * with this annotation will be called when the visibility state is changed.
 *
 * Possible States are declared in {@link com.airbnb.epoxy.OnModelVisibilityStateChangedListener}.
 *
 * The equivalent methods on the model is
 * {@link com.airbnb.epoxy.EpoxyModel#onVisibilityStateChanged}
 *
 * Inspired from Litho: https://fblitho.com/docs/visibility-handling
 */
@SuppressWarnings("JavadocReference")
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface OnVisibilityStateChanged {
}
