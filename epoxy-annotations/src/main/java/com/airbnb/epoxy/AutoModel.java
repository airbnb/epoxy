package com.airbnb.epoxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate model fields in an EpoxyController. Model fields annotated with this should not
 * be assigned a value directly; a model will automatically be created for them. A stable ID will
 * also be generated and assigned to the model. This ID will be the same across all instances of the
 * adapter, so it can be used for saving state of a model.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface AutoModel {

}
