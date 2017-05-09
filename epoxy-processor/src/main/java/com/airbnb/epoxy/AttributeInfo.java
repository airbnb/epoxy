package com.airbnb.epoxy;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.type.TypeMirror;

import static com.airbnb.epoxy.Utils.isViewClickListenerType;

abstract class AttributeInfo {

  protected String name;
  protected TypeName typeName;
  protected TypeMirror typeMirror;
  protected String modelName;
  protected String modelPackageName;
  protected boolean useInHash;
  protected boolean ignoreRequireHashCode;
  protected boolean generateSetter;
  protected List<AnnotationSpec> setterAnnotations = new ArrayList<>();
  protected boolean generateGetter;
  protected List<AnnotationSpec> getterAnnotations = new ArrayList<>();
  protected boolean hasFinalModifier;
  protected boolean packagePrivate;
  /**
   * Track whether there is a setter method for this attribute on a super class so that we can call
   * through to super.
   */
  protected boolean hasSuperSetter;

  // for private fields (Kotlin case)
  protected boolean isPrivate;
  protected String getterMethodName;
  protected String setterMethodName;

  /**
   * True if this attribute is completely generated as a field on the generated model. False if it
   * exists as a user defined attribute in a model super class.
   */
  protected boolean isGenerated;

  String getName() {
    return name;
  }

  TypeName getTypeName() {
    return typeName;
  }

  public TypeMirror getTypeMirror() {
    return typeMirror;
  }

  boolean useInHash() {
    return useInHash;
  }

  boolean ignoreRequireHashCode() {
    return ignoreRequireHashCode;
  }

  boolean generateSetter() {
    return generateSetter;
  }

  List<AnnotationSpec> getSetterAnnotations() {
    return setterAnnotations;
  }

  boolean generateGetter() {
    return generateGetter;
  }

  List<AnnotationSpec> getGetterAnnotations() {
    return getterAnnotations;
  }

  boolean hasSuperSetterMethod() {
    return hasSuperSetter;
  }

  boolean hasFinalModifier() {
    return hasFinalModifier;
  }

  boolean isPackagePrivate() {
    return packagePrivate;
  }

  String getterCode() {
    return isPrivate ? getterMethodName + "()" : name;
  }

  String setterCode() {
    return (isGenerated ? "this." : "super.")
        + (isPrivate ? setterMethodName + "($L)" : name + " = $L");
  }

  @Override
  public String toString() {
    return "ModelAttributeData{"
        + "name='" + name + '\''
        + ", type=" + typeName
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AttributeInfo)) {
      return false;
    }

    AttributeInfo that = (AttributeInfo) o;

    if (!name.equals(that.name)) {
      return false;
    }
    return typeName.equals(that.typeName);
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + typeName.hashCode();
    return result;
  }

  boolean isViewClickListener() {
    return isViewClickListenerType(getTypeMirror());
  }

  String getModelClickListenerName() {
    return getName() + GeneratedModelWriter.GENERATED_FIELD_SUFFIX;
  }

  String getModelName() {
    return modelName;
  }

  String getPackageName() {
    return modelPackageName;
  }
}
