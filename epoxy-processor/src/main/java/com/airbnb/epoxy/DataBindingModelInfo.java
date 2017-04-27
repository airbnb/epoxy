package com.airbnb.epoxy;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.airbnb.epoxy.ProcessorUtils.getElementByName;

class DataBindingModelInfo extends GeneratedModelInfo {
  private final LayoutResource layoutResource;
  private final String moduleName;
  private final String dataBindingClassName;

  DataBindingModelInfo(LayoutResource layoutResource, String moduleName) {
    this.layoutResource = layoutResource;
    this.moduleName = moduleName;

    dataBindingClassName = getDataBindingClassNameForResource(layoutResource, moduleName);
  }

  void parseDataBindingClass(Types typeUtils, Elements elementUtils) {
    // This databinding class won't exist until the second round of annotation processing since
    // it is generated in the first round.
    Element dataBindingClass = getElementByName(dataBindingClassName, elementUtils, typeUtils);

    // TODO: Inspect the methods enclosed in the binding class to find setters. Use those setters
    // to get the variable names and types.

    //addAttribute(new DataBindingAttributeInfo());
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

  LayoutResource getLayoutResource() {
    return layoutResource;
  }
}
