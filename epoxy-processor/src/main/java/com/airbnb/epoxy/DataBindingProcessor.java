package com.airbnb.epoxy;

import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

class DataBindingProcessor {
  private final Filer filer;
  private final Elements elementUtils;
  private final Types typeUtils;
  private final ErrorLogger errorLogger;
  private final ConfigManager configManager;
  private final LayoutResourceProcessor layoutResourceProcessor;
  private final DataBindingModuleLookup dataBindingModuleLookup;

  DataBindingProcessor(Filer filer, Elements elementUtils, Types typeUtils, ErrorLogger errorLogger,
      ConfigManager configManager, LayoutResourceProcessor layoutResourceProcessor,
      DataBindingModuleLookup dataBindingModuleLookup) {

    this.filer = filer;
    this.elementUtils = elementUtils;
    this.typeUtils = typeUtils;
    this.errorLogger = errorLogger;
    this.configManager = configManager;
    this.layoutResourceProcessor = layoutResourceProcessor;
    this.dataBindingModuleLookup = dataBindingModuleLookup;
  }

  void process(RoundEnvironment roundEnv) {
    Set<? extends Element> dataBindingLayoutPackageElements =
        roundEnv.getElementsAnnotatedWith(EpoxyDataBindingLayouts.class);

    for (Element packageElement : dataBindingLayoutPackageElements) {
      List<LayoutResource> layoutResources = layoutResourceProcessor
          .getLayoutsInAnnotation(packageElement, EpoxyDataBindingLayouts.class);

      // Get the module name after parsing resources so we can use the resource classes to figure
      // out the module name
      String moduleName = dataBindingModuleLookup.getModuleName(packageElement);

      for (LayoutResource layoutResource : layoutResources) {
        generateDataBindingModel(layoutResource, moduleName);
      }
    }
  }

  private void generateDataBindingModel(LayoutResource layoutResource, String moduleName) {
    String dataBindingClassName = getDataBindingClassNameForResource(layoutResource, moduleName);

    // This databinding class won't exist until the second round of annotation processing since
    // it is generated in the first round. If it doesn't exist we need to hold onto it until the
    // next round and then try again.
    Element dataBindingClass =
        ProcessorUtils.getElementByName(dataBindingClassName, elementUtils, typeUtils);

    // TODO: Inspect the methods enclosed in the binding class to find setters. Use those setters
    // to get the variable names and types. Then generate an EpoxyModel with those variables

  }

  private String getDataBindingClassNameForResource(LayoutResource layoutResource,
      String moduleName) {
    // TODO

    // From https://developer.android.com/topic/libraries/data-binding/index.html
    // By default, a Binding class will be generated based on the name of the layout file,
    // converting it to Pascal case and suffixing "Binding" to it. The above layout file was
    // main_activity.xml so the generate class was MainActivityBinding.

    // We don't support custom class names via <data class="com.example.CustomClassName">

    return "";
  }
}
