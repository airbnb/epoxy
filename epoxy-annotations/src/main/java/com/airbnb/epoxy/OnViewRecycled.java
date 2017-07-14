package com.airbnb.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This can be used to annotate methods inside classes with a {@link com.airbnb.epoxy.ModelView}
 * annotation. Methods with this annotation will be called when the view is recycled by the
 * RecyclerView.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface OnViewRecycled {
}
