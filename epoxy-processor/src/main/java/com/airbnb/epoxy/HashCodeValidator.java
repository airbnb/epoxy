package com.airbnb.epoxy;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import java.util.Arrays;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import static com.airbnb.epoxy.ProcessorUtils.getMethodOnClass;
import static com.airbnb.epoxy.ProcessorUtils.isIterableType;
import static com.airbnb.epoxy.ProcessorUtils.isSubtypeOfType;
import static com.airbnb.epoxy.ProcessorUtils.throwError;

/** Validates that an attribute implements hashCode. */
class HashCodeValidator {
  /**
   * Common types that can be assumed will have implementations at runtime that implement hashCode,
   * but that don't have it by default.
   */
  private static final List<String> WHITE_LISTED_TYPES = Arrays.asList(
      "java.lang.CharSequence"
  );

  private static final MethodSpec HASH_CODE_METHOD = MethodSpec.methodBuilder("hashCode")
      .returns(TypeName.INT)
      .build();

  private final Types typeUtils;

  HashCodeValidator(Types typeUtils) {
    this.typeUtils = typeUtils;
  }

  void validate(AttributeInfo attribute) throws EpoxyProcessorException {
    try {
      validateImplementsHashCode(attribute.getAttributeElement().asType());
    } catch (EpoxyProcessorException e) {
      // Append information about the attribute and class to the existing exception
      throwError(e.getMessage() + " (Attribute: %s Class: %s)",
          attribute.getName(),
          attribute.getClassElement().getSimpleName().toString());
    }
  }

  private void validateImplementsHashCode(TypeMirror mirror) throws EpoxyProcessorException {
    if (mirror.getKind() == TypeKind.ERROR) {
      // The class type cannot be resolved. This may be because it is a generated epoxy model and
      // the class hasn't been built yet.
      // We just assume that the class will implement hashCode at runtime.
      return;
    }

    if (TypeName.get(mirror).isPrimitive()) {
      return;
    }

    if (mirror.getKind() == TypeKind.ARRAY) {
      validateArrayType((ArrayType) mirror);
      return;
    }

    if (!(mirror instanceof DeclaredType)) {
      return;
    }

    DeclaredType declaredType = (DeclaredType) mirror;
    Element element = typeUtils.asElement(mirror);
    TypeElement clazz = (TypeElement) element;

    if (isIterableType(clazz)) {
      validateIterableType(declaredType);
      return;
    }

    if (isAutoValueType(element)) {
      return;
    }

    if (isWhiteListedType(element)) {
      return;
    }

    if (!hasHashCodeInClassHierarchy(clazz)) {
      throwError("Attribute does not implement hashCode");
    }
  }

  private boolean hasHashCodeInClassHierarchy(TypeElement clazz) {
    ExecutableElement methodOnClass = getMethodOnClass(clazz, HASH_CODE_METHOD, typeUtils);
    if (methodOnClass == null) {
      return false;
    }

    Element implementingClass = methodOnClass.getEnclosingElement();
    if (implementingClass.getSimpleName().toString().equals("Object")) {
      // Don't count default implementation on Object class
      return false;
    }

    // We don't care if the method is abstract or not, as long as it exists and it isn't the Object
    // implementation then the runtime value will implement it to some degree (hopefully
    // correctly :P)
    return true;
  }

  private void validateArrayType(ArrayType mirror) throws EpoxyProcessorException {
    // Check that the type of the array implements hashCode
    TypeMirror arrayType = mirror.getComponentType();
    try {
      validateImplementsHashCode(arrayType);
    } catch (EpoxyProcessorException e) {
      throwError("Type in array does not implement hashCode. Type: %s",
          arrayType.toString());
    }
  }

  private void validateIterableType(DeclaredType declaredType) throws EpoxyProcessorException {
    for (TypeMirror typeParameter : declaredType.getTypeArguments()) {
      // check that the type implements hashCode
      try {
        validateImplementsHashCode(typeParameter);
      } catch (EpoxyProcessorException e) {
        throwError("Type in Iterable does not implement hashCode. Type: %s",
            typeParameter.toString());
      }
    }

    // Assume that the iterable class implements hashCode and just return
  }

  private boolean isWhiteListedType(Element element) {
    for (String whiteListedType : WHITE_LISTED_TYPES) {
      if (isSubtypeOfType(element.asType(), whiteListedType)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Only works for classes in the module since AutoValue has a retention of Source so it is
   * discarded after compilation.
   */
  private boolean isAutoValueType(Element element) {
    for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
      DeclaredType annotationType = annotationMirror.getAnnotationType();
      boolean isAutoValue = isSubtypeOfType(annotationType, "com.google.auto.value.AutoValue");
      if (isAutoValue) {
        return true;
      }
    }
    return false;
  }
}
