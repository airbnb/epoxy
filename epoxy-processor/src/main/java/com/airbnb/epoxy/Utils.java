package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

class Utils {
  private static final Pattern PATTERN_STARTS_WITH_SET = Pattern.compile("set[A-Z]\\w*");

  static final String EPOXY_MODEL_TYPE = "com.airbnb.epoxy.EpoxyModel<?>";
  static final String UNTYPED_EPOXY_MODEL_TYPE = "com.airbnb.epoxy.EpoxyModel";
  static final String EPOXY_MODEL_WITH_HOLDER_TYPE = "com.airbnb.epoxy.EpoxyModelWithHolder<?>";
  static final String EPOXY_VIEW_HOLDER_TYPE = "com.airbnb.epoxy.EpoxyViewHolder";
  static final String EPOXY_HOLDER_TYPE = "com.airbnb.epoxy.EpoxyHolder";
  static final String ANDROID_VIEW_TYPE = "android.view.View";
  static final String EPOXY_CONTROLLER_TYPE = "com.airbnb.epoxy.EpoxyController";
  static final String VIEW_CLICK_LISTENER_TYPE = "android.view.View.OnClickListener";
  static final String GENERATED_MODEL_INTERFACE = "com.airbnb.epoxy.GeneratedModel";
  static final String MODEL_CLICK_LISTENER_TYPE = "com.airbnb.epoxy.OnModelClickListener";
  static final String ON_BIND_MODEL_LISTENER_TYPE = "com.airbnb.epoxy.OnModelBoundListener";
  static final String ON_UNBIND_MODEL_LISTENER_TYPE = "com.airbnb.epoxy.OnModelUnboundListener";
  static final String WRAPPED_LISTENER_TYPE = "com.airbnb.epoxy.WrappedEpoxyModelClickListener";
  static final String DATA_BINDING_MODEL_TYPE = "com.airbnb.epoxy.DataBindingEpoxyModel";

  static void throwError(String msg, Object... args)
      throws EpoxyProcessorException {
    throw new EpoxyProcessorException(String.format(msg, args));
  }

  static Class<?> getClass(ClassName name) {
    try {
      return Class.forName(name.reflectionName());
    } catch (ClassNotFoundException | NoClassDefFoundError e) {
      return null;
    }
  }

  static Class<? extends Annotation> getAnnotationClass(ClassName name) {
    try {
      return (Class<? extends Annotation>) getClass(name);
    } catch (ClassCastException e) {
      return null;
    }
  }

  static Element getElementByName(ClassName name, Elements elements, Types types) {
    try {
      return elements.getTypeElement(name.reflectionName());
    } catch (MirroredTypeException mte) {
      return types.asElement(mte.getTypeMirror());
    }
  }

  static Element getElementByName(String name, Elements elements, Types types) {
    try {
      return elements.getTypeElement(name);
    } catch (MirroredTypeException mte) {
      return types.asElement(mte.getTypeMirror());
    }
  }

  static ClassName getClassName(String className) {
    return ClassName.bestGuess(className);
  }

  static EpoxyProcessorException buildEpoxyException(String msg, Object... args) {
    return new EpoxyProcessorException(String.format(msg, args));
  }

  static boolean isViewClickListenerType(TypeMirror type) {
    return isSubtypeOfType(type, VIEW_CLICK_LISTENER_TYPE);
  }

  static boolean isIterableType(TypeElement element) {
    return isSubtypeOfType(element.asType(), "java.lang.Iterable<?>");
  }

  static boolean isEpoxyModel(TypeMirror type) {
    return isSubtypeOfType(type, EPOXY_MODEL_TYPE);
  }

  static boolean isController(TypeElement element) {
    return isSubtypeOfType(element.asType(), EPOXY_CONTROLLER_TYPE);
  }

  static boolean isEpoxyModel(TypeElement type) {
    return isEpoxyModel(type.asType());
  }

  static boolean isEpoxyModelWithHolder(TypeElement type) {
    return isSubtypeOfType(type.asType(), EPOXY_MODEL_WITH_HOLDER_TYPE);
  }

  static boolean isDataBindingModel(TypeElement type) {
    return isSubtypeOfType(type.asType(), DATA_BINDING_MODEL_TYPE);
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
   * Checks if two classes belong to the same package
   */
  static boolean belongToTheSamePackage(TypeElement class1, TypeElement class2, Elements elements) {
    Name package1 = elements.getPackageOf(class1).getQualifiedName();
    Name package2 = elements.getPackageOf(class2).getQualifiedName();
    return package1.equals(package2);
  }

  static boolean isSubtype(TypeElement e1, TypeElement e2, Types types) {
    return isSubtype(e1.asType(), e2.asType(), types);
  }

  static boolean isSubtype(TypeMirror e1, TypeMirror e2, Types types) {
    return types.isSubtype(e1, types.erasure(e2));
  }

  /**
   * Checks if the given field has package-private visibility
   */
  static boolean isFieldPackagePrivate(Element element) {
    Set<Modifier> modifiers = element.getModifiers();
    return !modifiers.contains(PUBLIC)
        && !modifiers.contains(PROTECTED)
        && !modifiers.contains(PRIVATE);
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
    if (clazz.getSuperclass().getKind() != TypeKind.DECLARED) {
      return null;
    }

    DeclaredType superclass = (DeclaredType) clazz.getSuperclass();
    TypeMirror recursiveResult =
        getEpoxyObjectType((TypeElement) typeUtils.asElement(superclass), typeUtils);

    if (recursiveResult != null && recursiveResult.getKind() != TypeKind.TYPEVAR) {
      // Use the type on the parent highest in the class hierarchy so we can find the original type.
      // We don't allow TypeVar since that is just type letter (eg T).
      return recursiveResult;
    }

    List<? extends TypeMirror> superTypeArguments = superclass.getTypeArguments();

    if (superTypeArguments.size() == 1) {
      // If there is only one type then we use that
      return superTypeArguments.get(0);
    }

    for (TypeMirror superTypeArgument : superTypeArguments) {
      // The user might have added additional types to their class which makes it more difficult
      // to figure out the base model type. We just look for the first type that is a view or
      // view holder.
      if (isSubtypeOfType(superTypeArgument, ANDROID_VIEW_TYPE)
          || isSubtypeOfType(superTypeArgument, EPOXY_HOLDER_TYPE)) {
        return superTypeArgument;
      }
    }

    return null;
  }

  static void validateFieldAccessibleViaGeneratedCode(Element fieldElement,
      Class<?> annotationClass, ErrorLogger errorLogger, boolean skipPrivateFieldCheck) {
    TypeElement enclosingElement = (TypeElement) fieldElement.getEnclosingElement();

    // Verify method modifiers.
    Set<Modifier> modifiers = fieldElement.getModifiers();
    if ((modifiers.contains(PRIVATE) && !skipPrivateFieldCheck) || modifiers.contains(STATIC)) {
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

  static void validateFieldAccessibleViaGeneratedCode(Element fieldElement,
      Class<?> annotationClass, ErrorLogger errorLogger) {
    validateFieldAccessibleViaGeneratedCode(fieldElement, annotationClass, errorLogger, false);
  }

  static String capitalizeFirstLetter(String original) {
    if (original == null || original.isEmpty()) {
      return original;
    }
    return original.substring(0, 1).toUpperCase() + original.substring(1);
  }

  static String decapitalizeFirstLetter(String original) {
    if (original == null || original.isEmpty()) {
      return original;
    }
    return original.substring(0, 1).toLowerCase() + original.substring(1);
  }

  static boolean startsWithIs(String original) {
    return original.startsWith("is") && original.length() > 2
        && Character.isUpperCase(original.charAt(2));
  }

  static boolean isSetterMethod(Element element) {
    if (element.getKind() != ElementKind.METHOD) {
      return false;
    }

    ExecutableElement method = (ExecutableElement) element;
    String methodName = method.getSimpleName().toString();
    return PATTERN_STARTS_WITH_SET.matcher(methodName).matches()
        && method.getParameters().size() == 1;
  }

  static String removeSetPrefix(String string) {
    return String.valueOf(string.charAt(3)).toLowerCase() + string.substring(4);
  }
}
