package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.airbnb.epoxy.ClassNames.EPOXY_DATA_BINDING_HOLDER;
import static com.airbnb.epoxy.ClassNames.EPOXY_DATA_BINDING_MODEL;
import static com.airbnb.epoxy.ProcessorUtils.capitalizeFirstLetter;
import static com.airbnb.epoxy.ProcessorUtils.getElementByName;

class DataBindingModelInfo extends GeneratedModelInfo {

  private static final String BINDING_SUFFIX = "Binding";
  private static final String SET_PREFIX = "set";

  private final Types typeUtils;
  private final Elements elementUtils;
  private final LayoutResource layoutResource;
  private final String moduleName;
  private final ClassName dataBindingClassName;

  DataBindingModelInfo(Types typeUtils, Elements elementUtils, LayoutResource layoutResource,
      String moduleName) {
    this.layoutResource = layoutResource;
    this.moduleName = moduleName;

    dataBindingClassName = getDataBindingClassNameForResource(layoutResource, moduleName);

    this.typeUtils = typeUtils;
    this.elementUtils = elementUtils;

    superClassElement = (TypeElement) ProcessorUtils.getElementByName(EPOXY_DATA_BINDING_MODEL,
        elementUtils, typeUtils);
    superClassName = EPOXY_DATA_BINDING_MODEL;
    generatedClassName = buildGeneratedModelName();
    parameterizedClassName = generatedClassName;
    boundObjectTypeName = EPOXY_DATA_BINDING_HOLDER;
    shouldGenerateModel = true;
    generateFieldsForAttributes = true;
  }

  void parseDataBindingClass() {
    // This databinding class won't exist until the second round of annotation processing since
    // it is generated in the first round.
    Element dataBindingClass = getElementByName(dataBindingClassName, elementUtils, typeUtils);

    for (Element element : dataBindingClass.getEnclosedElements()) {
      if (element.getKind() == ElementKind.METHOD) {
        ExecutableElement method = (ExecutableElement) element;
        if (method.getSimpleName().toString().startsWith(SET_PREFIX)
            && method.getParameters().size() == 1) {
          Element dataBindingElement = method.getParameters().get(0);
          addAttribute(new DataBindingAttributeInfo(this, dataBindingElement));
        }
      }
    }
  }

  private ClassName getDataBindingClassNameForResource(LayoutResource layoutResource,
      String moduleName) {
    StringBuilder builder = new StringBuilder();
    String[] parts = layoutResource.resourceName.split("_");
    for (String part : parts) {
      builder.append(capitalizeFirstLetter(part));
    }
    builder.append(BINDING_SUFFIX);

    return ClassName.get(moduleName + ".databinding", builder.toString());
  }

  private ClassName buildGeneratedModelName() {
    String simpleName = dataBindingClassName.simpleName() + "Model" + GENERATED_CLASS_NAME_SUFFIX;
    return ClassName.get(moduleName, simpleName);
  }

  LayoutResource getLayoutResource() {
    return layoutResource;
  }
}
