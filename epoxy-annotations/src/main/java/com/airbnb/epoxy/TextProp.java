package com.airbnb.epoxy;

import android.support.annotation.StringRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A convenient replacement for {@link ModelProp} when the prop represents text.
 * <p>
 * This can only be used when the setter parameter is a {@link CharSequence}
 * <p>
 * This is the same as using {@link ModelProp} with the option {@link
 * com.airbnb.epoxy.ModelProp.Option#GenerateStringOverloads}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface TextProp {

  @StringRes int defaultRes() default 0;
}
