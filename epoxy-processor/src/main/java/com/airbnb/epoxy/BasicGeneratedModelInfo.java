package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.airbnb.epoxy.Utils.getElementByName;
import static com.airbnb.epoxy.Utils.getEpoxyObjectType;

 class BasicGeneratedModelInfo extends GeneratedModelInfo {

  private final Elements elementUtils;

  BasicGeneratedModelInfo(Types typeUtils, Elements elementUtils, TypeElement superClassElement,
      ErrorLogger errorLogger) {
    this.elementUtils = elementUtils;
    this.superClassName = ParameterizedTypeName.get(superClassElement.asType());
    this.superClassElement = superClassElement;
    generatedClassName = buildGeneratedModelName(superClassElement);

    for (TypeParameterElement typeParameterElement : superClassElement.getTypeParameters()) {
      typeVariableNames.add(TypeVariableName.get(typeParameterElement));
    }

    collectOriginalClassConstructors(superClassElement);
    collectMethodsReturningClassType(superClassElement, typeUtils);

    if (!typeVariableNames.isEmpty()) {
      TypeVariableName[] typeArguments =
          typeVariableNames.toArray(new TypeVariableName[typeVariableNames.size()]);
      this.parameterizedClassName = ParameterizedTypeName.get(generatedClassName, typeArguments);
    } else {
      this.parameterizedClassName = generatedClassName;
    }

    TypeMirror boundObjectTypeMirror = getEpoxyObjectType(superClassElement, typeUtils);
    if (boundObjectTypeMirror == null) {
      errorLogger
          .logError("Epoxy model type could not be found. (class: %s)",
              superClassElement.getSimpleName());
      // Return a basic view type so the code can be generated
      boundObjectTypeMirror =
          getElementByName(Utils.ANDROID_VIEW_TYPE, elementUtils, typeUtils).asType();
    }
    boundObjectTypeName = TypeName.get(boundObjectTypeMirror);

    boolean hasEpoxyClassAnnotation =
        superClassElement.getAnnotation(EpoxyModelClass.class) != null;
    boolean isAbstract = superClassElement.getModifiers().contains(Modifier.ABSTRACT);

    // By default we don't extend classes that are abstract; if they don't contain all required
    // methods then our generated class won't compile. If there is a EpoxyModelClass annotation
    // though we will always generate the subclass
    shouldGenerateModel = !isAbstract || hasEpoxyClassAnnotation;
  }

  protected ClassName buildGeneratedModelName(TypeElement classElement) {
    String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();

    int packageLen = packageName.length() + 1;
    String className =
        classElement.getQualifiedName().toString().substring(packageLen).replace('.', '$');

    return ClassName.get(packageName, className + GENERATED_CLASS_NAME_SUFFIX);
  }

  /**
   * Get information about constructors of the original class so we can duplicate them in the
   * generated class and call through to super with the proper parameters
   */
  private void collectOriginalClassConstructors(TypeElement originalClass) {
    for (Element subElement : originalClass.getEnclosedElements()) {
      if (subElement.getKind() == ElementKind.CONSTRUCTOR
          && !subElement.getModifiers().contains(Modifier.PRIVATE)) {
        ExecutableElement castedSubElement = ((ExecutableElement) subElement);
        List<? extends VariableElement> params = castedSubElement.getParameters();
        constructors
            .add(new ConstructorInfo(subElement.getModifiers(), buildParamSpecs(params),
                castedSubElement.isVarArgs()));
      }
    }
  }
}
