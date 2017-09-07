package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

class DataBindingModuleLookup {

  private final Elements elements;
  private final Types types;
  private final ErrorLogger errorLogger;
  private final ResourceProcessor resourceProcessor;

  DataBindingModuleLookup(Elements elements, Types types, ErrorLogger errorLogger,
      ResourceProcessor resourceProcessor) {

    this.elements = elements;
    this.types = types;
    this.errorLogger = errorLogger;
    this.resourceProcessor = resourceProcessor;
  }

  String getModuleName(Element element) {
    PackageElement packageOf = elements.getPackageOf(element);
    String packageName = packageOf.getQualifiedName().toString();

    // First we try to get the module name by looking at what R classes were found when processing
    // layout annotations. This may find nothing if no layouts were given as annotation params
    String moduleName = getModuleNameViaResources(packageName);

    if (moduleName == null) {
      // If the first approach fails, we try to guess at the R class for the module and look up
      // the class to see if it exists. This can fail if this model's package name does not
      // include the module name as a prefix (convention makes this unlikely.)
      moduleName = getModuleNameViaGuessing(packageName);
    }

    if (moduleName == null) {
      errorLogger.logError("Could not find module name for DataBinding BR class.");
      // Fallback to using the package name so we can at least try to generate and compile something
      moduleName = packageName;
    }

    return moduleName;
  }

  /**
   * Attempts to get the module name of the given package. We can do this because the package name
   * of an R class is the module. Generally only one R class is used and we can just use that module
   * name, but it is possible to have multiple R classes. In that case we compare the package names
   * to find what is the most similar.
   * <p>
   * We need to get the module name to know the path of the BR class for data binding.
   */
  private String getModuleNameViaResources(String packageName) {
    List<ClassName> rClasses = resourceProcessor.getRClassNames();
    if (rClasses.isEmpty()) {
      return packageName;
    }

    if (rClasses.size() == 1) {
      // Common case
      return rClasses.get(0).packageName();
    }

    // Generally the only R class used should be the app's. It is possible to use other R classes
    // though, like Android's. In that case we figure out the most likely match by comparing the
    // package name.
    //  For example we might have "com.airbnb.epoxy.R" and "android.R"
    String[] packageNames = packageName.split("\\.");

    ClassName bestMatch = null;
    int bestNumMatches = -1;

    for (ClassName rClass : rClasses) {
      String[] rModuleNames = rClass.packageName().split("\\.");
      int numNameMatches = 0;
      for (int i = 0; i < Math.min(packageNames.length, rModuleNames.length); i++) {
        if (packageNames[i].equals(rModuleNames[i])) {
          numNameMatches++;
        } else {
          break;
        }
      }

      if (numNameMatches > bestNumMatches) {
        bestMatch = rClass;
      }
    }

    return bestMatch.packageName();
  }

  /**
   * Attempts to get the android module that is currently being processed.. We can do this because
   * the package name of an R class is the module name. So, we take any element in the module,
   * <p>
   * We need to get the module name to know the path of the BR class for data binding.
   */
  private String getModuleNameViaGuessing(String packageName) {
    String[] packageNameParts = packageName.split("\\.");

    String moduleName = "";
    for (int i = 0; i < packageNameParts.length; i++) {
      moduleName += packageNameParts[i];

      Element rClass = Utils.getElementByName(moduleName + ".R", elements, types);
      if (rClass != null) {
        return moduleName;
      } else {
        moduleName += ".";
      }
    }

    return null;
  }
}
