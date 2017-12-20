package com.airbnb.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A convenient replacement for {@link ModelProp} when the prop represents a callback or listener.
 * <p>
 * This is the same as using {@link ModelProp} with the options {@link
 * com.airbnb.epoxy.ModelProp.Option#NullOnRecycle} and
 * {@link com.airbnb.epoxy.ModelProp.Option#DoNotHash}
 * <p>
 * This can only be used on setters who's parameter is marked as nullable. The prop will be set to
 * null when the view is recycled to ensure that the listener is not leaked.
 * <p>
 * Be aware that since this applies the option {@link com.airbnb.epoxy.ModelProp.Option#DoNotHash}
 * changing the value of the listener will not trigger an update to the view.
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface CallbackProp {
}
