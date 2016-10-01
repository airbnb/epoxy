package com.airbnb.epoxy;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.DeclaredType;

public class AttributeInfo {

  private final List<AnnotationSpec> setterAnnotations = new ArrayList<>();
  private final List<AnnotationSpec> getterAnnotations = new ArrayList<>();
  private final String name;
  private final TypeName type;
  private final boolean useInHash;
  private final boolean generateSetter;
  private final boolean hasFinalModifier;
  private final boolean packagePrivate;
  /**
   * Track whether there is a setter method for this attribute on a super class so that we can call
   * through to super.
   */
  private final boolean hasSuperSetter;

  AttributeInfo(String name, TypeName type,
      List<? extends AnnotationMirror> annotationMirrors, EpoxyAttribute annotation,
      boolean hasSuperSetter, boolean hasFinalModifier, boolean packagePrivate) {
    this.name = name;
    this.type = type;
    this.hasSuperSetter = hasSuperSetter;
    this.hasFinalModifier = hasFinalModifier;
    this.packagePrivate = packagePrivate;
    useInHash = annotation.hash();
    generateSetter = annotation.setter();
    buildAnnotationLists(annotationMirrors);
  }

  /**
   * Keeps track of annotations on the attribute so that they can be used in the generated setter
   * and getter method. Setter and getter annotations are stored separately since the annotation may
   * not target both method and parameter types.
   */
  private void buildAnnotationLists(List<? extends AnnotationMirror> annotationMirrors) {
    for (AnnotationMirror annotationMirror : annotationMirrors) {
      if (!annotationMirror.getElementValues().isEmpty()) {
        // Not supporting annotations with values for now
        continue;
      }

      ClassName annotationClass =
          ClassName.bestGuess(annotationMirror.getAnnotationType().toString());
      if (annotationClass.equals(ClassName.get(EpoxyAttribute.class))) {
        // Don't include our own annotation
        continue;
      }

      DeclaredType annotationType = annotationMirror.getAnnotationType();
      // A target may exist on an annotation type to specify where the annotation can
      // be used, for example fields, methods, or parameters.
      Target targetAnnotation = annotationType.asElement().getAnnotation(Target.class);

      // Allow all target types if no target was specified on the annotation
      List<ElementType> elementTypes =
          Arrays.asList(targetAnnotation == null ? ElementType.values() : targetAnnotation.value());

      AnnotationSpec annotationSpec = AnnotationSpec.builder(annotationClass).build();
      if (elementTypes.contains(ElementType.PARAMETER)) {
        setterAnnotations.add(annotationSpec);
      }

      if (elementTypes.contains(ElementType.METHOD)) {
        getterAnnotations.add(annotationSpec);
      }
    }
  }

  public String getName() {
    return name;
  }

  public TypeName getType() {
    return type;
  }

  public boolean useInHash() {
    return useInHash;
  }

  public boolean generateSetter() {
    return generateSetter;
  }

  public List<AnnotationSpec> getSetterAnnotations() {
    return setterAnnotations;
  }

  public List<AnnotationSpec> getGetterAnnotations() {
    return getterAnnotations;
  }

  public boolean hasSuperSetterMethod() {
    return hasSuperSetter;
  }

  public boolean hasFinalModifier() {
    return hasFinalModifier;
  }

  public boolean isPackagePrivate() {
    return packagePrivate;
  }

  @Override
  public String toString() {
    return "ModelAttributeData{"
        + "name='" + name + '\''
        + ", type=" + type
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
    return type.equals(that.type);
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + type.hashCode();
    return result;
  }
}
