package com.airbnb.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This can be used to annotate methods inside classes with a {@link com.airbnb.epoxy.ModelView}
 * annotation. Methods with this annotation will be called after a view instance  is bound to a
 * model and all model props have been set. This is useful if you need to wait until multiple props
 * are set before doing certain initialization.
 * <p>
 * Methods with this annotation will be called after both the initial bind when the view comes on
 * screen, and after partial binds when an onscreen view is updated.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface AfterPropsSet {
}

