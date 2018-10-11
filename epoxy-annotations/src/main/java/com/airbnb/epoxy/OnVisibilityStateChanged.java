package com.airbnb.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This can be used to annotate methods inside classes with a {@link ModelView} annotation. Methods
 * with this annotation will be called when the visibility state is changed.
 * <p>
 * Annotated methods should follow this signature :
 * `@OnVisibilityStateChanged
 *  public void method(
 *    float percentVisibleHeight, float percentVisibleWidth: Float,
 *    int visibleHeight, int visibleWidth
 *  )`
 * <p>
 * Possible States are declared in {@link com.airbnb.epoxy.OnModelVisibilityStateChangedListener}.
 * <p>
 * The equivalent methods on the model is
 * {@link com.airbnb.epoxy.EpoxyModel#onVisibilityStateChanged}
 * <p>
 * @see OnModelVisibilityStateChangedListener
 */
@SuppressWarnings("JavadocReference")
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface OnVisibilityStateChanged {
}
