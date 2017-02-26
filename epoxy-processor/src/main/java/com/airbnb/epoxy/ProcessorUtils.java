package com.airbnb.epoxy;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;

class ProcessorUtils {

  static final String EPOXY_MODEL_TYPE = "com.airbnb.epoxy.EpoxyModel<?>";
  static final String EPOXY_MODEL_HOLDER_TYPE = "com.airbnb.epoxy.EpoxyModelWithHolder<?>";
  static final String EPOXY_AUTO_ADAPTER_TYPE = "com.airbnb.epoxy.AutoEpoxyAdapter";

  static void throwError(String msg, Object... args)
      throws EpoxyProcessorException {
    throw new EpoxyProcessorException(String.format(msg, args));
  }

  static EpoxyProcessorException buildEpoxyException(String msg, Object... args) {
    return new EpoxyProcessorException(String.format(msg, args));
  }

  static boolean isIterableType(TypeElement element) {
    return isSubtypeOfType(element.asType(), "java.lang.Iterable<?>");
  }

  static boolean isEpoxyModel(TypeMirror type) {
    return isSubtypeOfType(type, EPOXY_MODEL_TYPE);
  }

  static boolean isDiffAdapter(TypeElement element) {
    return isSubtypeOfType(element.asType(), EPOXY_AUTO_ADAPTER_TYPE);
  }

  static boolean isEpoxyModel(TypeElement type) {
    return isEpoxyModel(type.asType());
  }

  static boolean isEpoxyModelWithHolder(TypeElement type) {
    return isSubtypeOfType(type.asType(), EPOXY_MODEL_HOLDER_TYPE);
  }

  static boolean isSubtypeOfType(TypeMirror typeMirror, String otherType) {
    if (otherType.equals(typeMirror.toString())) {
      return true;
    }
    if (typeMirror.getKind() != TypeKind.DECLARED) {
      return false;
    }
    DeclaredType declaredType = (DeclaredType) typeMirror;
    List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
    if (typeArguments.size() > 0) {
      StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
      typeString.append('<');
      for (int i = 0; i < typeArguments.size(); i++) {
        if (i > 0) {
          typeString.append(',');
        }
        typeString.append('?');
      }
      typeString.append('>');
      if (typeString.toString().equals(otherType)) {
        return true;
      }
    }
    Element element = declaredType.asElement();
    if (!(element instanceof TypeElement)) {
      return false;
    }
    TypeElement typeElement = (TypeElement) element;
    TypeMirror superType = typeElement.getSuperclass();
    if (isSubtypeOfType(superType, otherType)) {
      return true;
    }
    for (TypeMirror interfaceType : typeElement.getInterfaces()) {
      if (isSubtypeOfType(interfaceType, otherType)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return True if the clazz (or one of its superclasses) implements the given method. Returns
   * false if the method doesn't exist anywhere in the class hierarchy or it is abstract.
   */
  static boolean implementsMethod(TypeElement clazz, MethodSpec method, Types typeUtils) {
    ExecutableElement methodOnClass = getMethodOnClass(clazz, method, typeUtils);
    if (methodOnClass == null) {
      return false;
    }

    Set<Modifier> modifiers = methodOnClass.getModifiers();
    return !modifiers.contains(Modifier.ABSTRACT);
  }

  static TypeMirror getMethodReturnType(TypeElement clazz, MethodSpec method, Types typeUtils) {
    ExecutableElement methodOnClass = getMethodOnClass(clazz, method, typeUtils);

    if (methodOnClass == null) {
      return null;
    }

    return methodOnClass.getReturnType();
  }

  /**
   * @return The first element matching the given method in the class's hierarchy, or null if there
   * is no match.
   */
  static ExecutableElement getMethodOnClass(TypeElement clazz, MethodSpec method, Types typeUtils) {
    if (clazz.asType().getKind() != TypeKind.DECLARED) {
      return null;
    }

    for (Element subElement : clazz.getEnclosedElements()) {
      if (subElement.getKind() == ElementKind.METHOD) {
        ExecutableElement methodElement = ((ExecutableElement) subElement);
        if (!methodElement.getSimpleName().toString().equals(method.name)) {
          continue;
        }

        if (!areParamsTheSame(methodElement, method)) {
          continue;
        }

        return methodElement;
      }
    }

    TypeElement superClazz = (TypeElement) typeUtils.asElement(clazz.getSuperclass());
    if (superClazz == null) {
      return null;
    }
    return getMethodOnClass(superClazz, method, typeUtils);
  }

  private static boolean areParamsTheSame(ExecutableElement method1, MethodSpec method2) {
    List<? extends VariableElement> params1 = method1.getParameters();
    List<ParameterSpec> params2 = method2.parameters;

    if (params1.size() != params2.size()) {
      return false;
    }

    for (int i = 0; i < params1.size(); i++) {
      VariableElement param1 = params1.get(i);
      ParameterSpec param2 = params2.get(i);

      if (!TypeName.get(param1.asType()).equals(param2.type)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Returns the type of the Epoxy model.
   * <p>
   * Eg for "class MyModel extends EpoxyModel<TextView>" it would return TextView.
   */
  static TypeMirror getEpoxyObjectType(TypeElement clazz, Types typeUtils) {
    if (clazz.asType().getKind() != TypeKind.DECLARED) {
      return null;
    }

    DeclaredType superclass = (DeclaredType) clazz.getSuperclass();
    List<? extends TypeMirror> superTypeArguments = superclass.getTypeArguments();

    if (superTypeArguments.isEmpty()) {
      return getEpoxyObjectType((TypeElement) typeUtils.asElement(superclass), typeUtils);
    }

    // TODO: (eli_hart 2/2/17) If the class added additional types this won't be correct. That
    // should be rare, but it would be nice to handle.
    return superTypeArguments.get(0);
  }

  static void validateFieldAccessibleViaGeneratedCode(Element fieldElement,
      Class<?> annotationClass, ErrorLogger errorLogger) {
    TypeElement enclosingElement = (TypeElement) fieldElement.getEnclosingElement();

    // Verify method modifiers.
    Set<Modifier> modifiers = fieldElement.getModifiers();
    if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
      errorLogger.logError(
          "%s annotations must not be on private or static fields. (class: %s, field: %s)",
          annotationClass.getSimpleName(),
          enclosingElement.getSimpleName(), fieldElement.getSimpleName());
    }

    // Nested classes must be static
    if (enclosingElement.getNestingKind().isNested()) {
      if (!enclosingElement.getModifiers().contains(STATIC)) {
        errorLogger.logError(
            "Nested classes with %s annotations must be static. (class: %s, field: %s)",
            annotationClass.getSimpleName(),
            enclosingElement.getSimpleName(), fieldElement.getSimpleName());
      }
    }

    // Verify containing type.
    if (enclosingElement.getKind() != CLASS) {
      errorLogger
          .logError("%s annotations may only be contained in classes. (class: %s, field: %s)",
              annotationClass.getSimpleName(),
              enclosingElement.getSimpleName(), fieldElement.getSimpleName());
    }

    // Verify containing class visibility is not private.
    if (enclosingElement.getModifiers().contains(PRIVATE)) {
      errorLogger.logError(
          "%s annotations may not be contained in private classes. (class: %s, field: %s)",
          annotationClass.getSimpleName(),
          enclosingElement.getSimpleName(), fieldElement.getSimpleName());
    }
  }
}
