package com.airbnb.epoxy;

import com.airbnb.epoxy.ModelProp.Option;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.AnnotationSpec.Builder;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.airbnb.epoxy.Utils.capitalizeFirstLetter;
import static com.airbnb.epoxy.Utils.removeSetPrefix;

class ViewAttributeInfo extends AttributeInfo {
  private final ModelViewInfo modelInfo;
  final String propName;
  final String viewSetterMethodName;
  String constantFieldNameForDefaultValue;
  boolean resetWithNull;

  ViewAttributeInfo(ModelViewInfo modelInfo, ExecutableElement setterMethod, Types types,
      Elements elements, ErrorLogger errorLogger) {
    this(modelInfo, setterMethod.getParameters().get(0).asType(),
        setterMethod.getSimpleName().toString());

    ModelProp annotation = setterMethod.getAnnotation(ModelProp.class);
    groupKey = annotation.group();

    VariableElement paramElement = setterMethod.getParameters().get(0);
    parseAnnotations(paramElement, types);

    defaultValue = getDefaultValue(annotation, errorLogger, types);
    assignNullability(paramElement);

    setJavaDocString(elements.getDocComment(setterMethod));

    Set<Option> options = new HashSet<>(Arrays.asList(annotation.options()));
    validatePropOptions(errorLogger, options, types, elements);

    useInHash = !options.contains(Option.DoNotHash);
    ignoreRequireHashCode = options.contains(Option.IgnoreRequireHashCode);
    resetWithNull = options.contains(Option.ResetWithNull);
  }

  private void assignNullability(VariableElement paramElement) {
    if (!typeName.isPrimitive()) {
      // Default to not nullable
      isNullable = false;
    }

    // Set to nullable if we find a @Nullable annotation
    for (AnnotationMirror annotationMirror : paramElement.getAnnotationMirrors()) {
      // There are multiple packages/frameworks that define a Nullable annotation and we want to
      // support all of them. We just check for a class named Nullable and ignore the package.
      if (annotationMirror.getAnnotationType().asElement().getSimpleName().toString()
          .equals("Nullable")) {

        isNullable = true;
        if (defaultValue == null) {
          defaultValue = CodeBlock.of("null");
        }
        break;
      }
    }
  }

  private CodeBlock getDefaultValue(ModelProp annotation,
      ErrorLogger errorLogger, Types types) {

    String constantName = annotation.defaultValue();
    if (constantName.isEmpty()) {
      return null;
    }

    for (Element element : modelInfo.viewElement.getEnclosedElements()) {
      if (element.getKind() == ElementKind.FIELD
          && element.getSimpleName().toString().equals(constantName)) {

        Set<Modifier> modifiers = element.getModifiers();
        if (!modifiers.contains(Modifier.FINAL)
            || !modifiers.contains(Modifier.STATIC)
            || modifiers.contains(Modifier.PRIVATE)) {

          errorLogger.logError(
              "Default values for view props must be static, final, and not private. (%s#%s)",
              modelInfo.viewElement.getSimpleName(), viewSetterMethodName);
          return null;
        }

        // Make sure that the type of the default value is a valid type for the prop
        if (!types.isAssignable(element.asType(), typeMirror)) {
          errorLogger.logError(
              "The default value for (%s#%s) must be a %s.",
              modelInfo.viewElement.getSimpleName(), viewSetterMethodName, typeMirror);
          return null;
        }
        constantFieldNameForDefaultValue = constantName;
        return CodeBlock.of("$T.$L", ClassName.get(modelInfo.viewElement), constantName);
      }
    }

    errorLogger.logError(
        "The default value for (%s#%s) could not be found. It must be a constant field in the "
            + "view class.",
        modelInfo.viewElement.getSimpleName(), viewSetterMethodName);
    return null;
  }

  ViewAttributeInfo(ModelViewInfo modelInfo, TypeMirror type, String viewSetterMethodName) {
    this.modelInfo = modelInfo;
    typeMirror = type;
    typeName = TypeName.get(type);

    this.viewSetterMethodName = viewSetterMethodName;
    propName = removeSetPrefix(viewSetterMethodName);

    // Suffix the field name with the type to prevent collisions from overloaded setter methods
    this.fieldName = propName + "_" + getSimpleName(typeName);
    modelName = modelInfo.getGeneratedName().simpleName();
    modelPackageName = modelInfo.generatedClassName.packageName();
    generateSetter = true;
    generateGetter = true;
    hasFinalModifier = false;
    packagePrivate = false;
    isGenerated = true;
  }

  private void validatePropOptions(ErrorLogger errorLogger, Set<Option> options, Types types,
      Elements elements) {
    if (options.contains(Option.IgnoreRequireHashCode) && options.contains(Option.DoNotHash)) {
      errorLogger
          .logError("Illegal to use both %s and %s options in an %s annotation. (%s#%s)",
              Option.DoNotHash, Option.IgnoreRequireHashCode,
              ModelProp.class.getSimpleName(), modelName, viewSetterMethodName);
    }

    if (options.contains(Option.GenerateStringOverloads)
        && !types.isAssignable(Utils.getTypeMirror(String.class, elements), typeMirror)) {
      errorLogger
          .logError("Setters with %s option must have a type that is assignable to String. (%s#%s)",
              Option.GenerateStringOverloads, modelName, viewSetterMethodName);
    }

    if (options.contains(Option.ResetWithNull) && (isNullable == null || !isNullable)) {
      errorLogger
          .logError(
              "Setters with %s option must have a type that is annotated with @Nullable. (%s#%s)",
              Option.ResetWithNull, modelName, viewSetterMethodName);
    }
  }

  /** Tries to return the simple name of the given type. */
  private static String getSimpleName(TypeName name) {
    if (name.isPrimitive()) {
      return capitalizeFirstLetter(name.toString());
    }

    if (name instanceof ClassName) {
      return ((ClassName) name).simpleName();
    }

    if (name instanceof ArrayTypeName) {
      return getSimpleName(((ArrayTypeName) name).componentType) + "Array";
    }

    if (name instanceof ParameterizedTypeName) {
      return getSimpleName(((ParameterizedTypeName) name).rawType);
    }

    if (name instanceof TypeVariableName) {
      return capitalizeFirstLetter(((TypeVariableName) name).name);
    }

    // Don't expect this to happen
    return name.toString().replace(".", "");
  }

  private void parseAnnotations(VariableElement paramElement, Types types) {
    for (AnnotationMirror annotationMirror : paramElement.getAnnotationMirrors()) {
      Element annotationElement = types.asElement(annotationMirror.getAnnotationType());
      Builder builder = AnnotationSpec.builder(ClassName.get(((TypeElement) annotationElement)));

      for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror
          .getElementValues().entrySet()) {
        String paramName = entry.getKey().getSimpleName().toString();
        String paramValue = entry.getValue().getValue().toString();
        builder.addMember(paramName, paramValue);
      }

      AnnotationSpec annotationSpec = builder.build();
      setterAnnotations.add(annotationSpec);
      getterAnnotations.add(annotationSpec);
    }
  }

  @Override
  protected void setJavaDocString(String docComment) {
    super.setJavaDocString(docComment);

    if (javaDoc == null) {
      javaDoc = CodeBlock.of("");
    }

    CodeBlock.Builder builder = javaDoc.toBuilder();

    if (!javaDoc.isEmpty()) {
      builder.add("\n<p>\n");
    }

    if (isRequired()) {
      builder.add("<i>Required.</i>");
    } else {
      builder.add("<i>Optional</i>: ");
      if (constantFieldNameForDefaultValue == null) {
        builder.add("Default value is null");
      } else {
        builder.add("Default value is <b>{@value $T#$L}</b>", ClassName.get(modelInfo.viewElement),
            constantFieldNameForDefaultValue);
      }
    }

    builder.add("\n\n@see $T#$L($T)", modelInfo.viewElement.asType(), viewSetterMethodName,
        typeMirror);

    javaDoc = builder
        .add("\n").build();
  }

  @Override
  String generatedSetterName() {
    return propName;
  }

  @Override
  String generatedGetterName() {
    if (isOverload()) {
      // Avoid method name collisions for overloaded method by appending the return type
      return propName + getSimpleName(typeName);
    }

    return propName;
  }

  @Override
  public String toString() {
    return "View Prop {"
        + "view='" + modelInfo.viewElement.getSimpleName() + '\''
        + ", name='" + viewSetterMethodName + '\''
        + ", type=" + typeName
        + '}';
  }
}
