package com.airbnb.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface TextProp {

  // TODO: (eli_hart 8/10/17) support a default value from a string resource
//  @StringRes int defaultRes() default 0;
}
