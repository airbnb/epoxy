package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.airbnb.epoxy.Utils.getElementByName;

/**
 * Scans R files and and compiles resource values in those R classes. This allows us to look
 * up raw resource values (eg 23523452) and convert that to the resource name (eg
 * R.layout.my_view) so that we can properly reference that resource. This is important in library
 * projects where the R value at process time can be different from the final R value in the app.
 * <p>
 * This is adapted from Butterknife. https://github.com/JakeWharton/butterknife/pull/613
 */
class ResourceProcessor {

  private final ErrorLogger errorLogger;
  private final Elements elementUtils;
  private final Types typeUtils;

  private Trees trees;
  private final Map<String, ClassName> rClassNameMap = new HashMap<>();
  /** Maps the name of an R class to a list of all of the resources in that class. */
  private final Map<ClassName, List<ResourceValue>> rClassResources = new HashMap<>();
  private final AnnotationResourceParamScanner scanner = new AnnotationResourceParamScanner();

  ResourceProcessor(ProcessingEnvironment processingEnv, ErrorLogger errorLogger,
      Elements elementUtils, Types typeUtils) {
    this.errorLogger = errorLogger;
    this.elementUtils = elementUtils;
    this.typeUtils = typeUtils;

    try {
      trees = Trees.instance(processingEnv);
    } catch (IllegalArgumentException ignored) {

    }
  }

  ResourceValue getLayoutInAnnotation(Element element, Class annotationClass) {
    List<ResourceValue> layouts = getLayoutsInAnnotation(element, annotationClass);
    if (layouts.size() != 1) {
      errorLogger.logError(
          "Expected exactly 1 layout resource in the %s annotation but received %s. Annotated "
              + "element is %s",
          annotationClass.getSimpleName(), layouts.size(), element.getSimpleName());

      if (layouts.isEmpty()) {
        // Just pass back something so the code can compile before the error logger prints
        return new ResourceValue(0);
      }
    }

    return layouts.get(0);
  }

  /**
   * Get detailed information about the layout resources that are parameters to the given
   * annotation.
   */
  List<ResourceValue> getLayoutsInAnnotation(Element element, Class annotationClass) {
    List<Integer> layoutValues = getLayoutValues(element, annotationClass);
    return getResourcesInAnnotation(element, annotationClass, "layout", layoutValues);
  }

  ResourceValue getStringResourceInAnnotation(Element element, Class annotationClass,
      int resourceValue) {
    return getResourceInAnnotation(element, annotationClass, "string", resourceValue);
  }

  ResourceValue getResourceInAnnotation(Element element, Class annotationClass,
      String resourceType, int resourceValue) {
    List<ResourceValue> layouts = getResourcesInAnnotation(element, annotationClass, resourceType,
        Collections.singletonList(resourceValue));
    if (layouts.size() != 1) {
      errorLogger.logError(
          "Expected exactly 1 %s resource in the %s annotation but received %s. Annotated "
              + "element is %s",
          resourceType, annotationClass.getSimpleName(), layouts.size(), element.getSimpleName());

      if (layouts.isEmpty()) {
        // Just pass back something so the code can compile before the error logger prints
        return new ResourceValue(0);
      }
    }

    return layouts.get(0);
  }

  List<ResourceValue> getResourcesInAnnotation(Element element, Class annotationClass,
      String resourceType, List<Integer> resourceValues) {
    List<ResourceValue> resources = new ArrayList<>(resourceValues.size());

    JCTree tree = (JCTree) trees.getTree(element, getAnnotationMirror(element, annotationClass));
    // tree can be null if the references are compiled types and not source
    if (tree != null) {
      // Collects details about the layout resource used for the annotation parameter
      scanner.clearResults();
      scanner.setCurrentAnnotationDetails(element, annotationClass, resourceType);
      tree.accept(scanner);
      resources.addAll(scanner.getResults());
    }

    // Resource values may not have been picked up by the scanner if they are hardcoded.
    // In that case we just use the hardcoded value without an R class
    if (resources.size() != resourceValues.size()) {
      for (int layoutValue : resourceValues) {
        if (!isLayoutValueInResources(resources, layoutValue)) {
          resources.add(new ResourceValue(layoutValue));
        }
      }
    }

    return resources;
  }

  private boolean isLayoutValueInResources(List<ResourceValue> resources, int layoutValue) {
    for (ResourceValue resource : resources) {
      if (resource.getValue() == layoutValue) {
        return true;
      }
    }

    return false;
  }

  private static List<Integer> getLayoutValues(Element element, Class annotationClass) {
    Annotation annotation = element.getAnnotation(annotationClass);

    // We could do this in a more generic way if we ever need to support more annotation types
    List<Integer> layoutResources = new ArrayList<>();
    if (annotation instanceof EpoxyModelClass) {
      layoutResources.add(((EpoxyModelClass) annotation).layout());
    } else if (annotation instanceof EpoxyDataBindingLayouts) {
      for (int layoutRes : ((EpoxyDataBindingLayouts) annotation).value()) {
        layoutResources.add(layoutRes);
      }
    } else if (annotation instanceof ModelView) {
      layoutResources.add(((ModelView) annotation).defaultLayout());
    }

    return layoutResources;
  }

  private AnnotationMirror getAnnotationMirror(Element element, Class annotationClass) {
    for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
      if (annotationMirror.getAnnotationType().toString()
          .equals(annotationClass.getCanonicalName())) {
        return annotationMirror;
      }
    }

    errorLogger.logError("Unable to get %s annotation on model %",
        annotationClass.getSimpleName(), element.getSimpleName());

    return null;
  }

  List<ClassName> getRClassNames() {
    return new ArrayList<>(rClassNameMap.values());
  }

  /**
   * Returns a list of layout resources whose name contains the given layout as a prefix.
   */
  List<ResourceValue> getAlternateLayouts(ResourceValue layout) {
    if (rClassResources.isEmpty()) {
      // This will only have been filled if at least one view has a layout in it's annotation.
      // If all view's use their default layout then resources haven't been parsed yet and we can
      // do it now
      Element rLayoutClassElement = getElementByName(layout.getClassName(), elementUtils,
          typeUtils);
      saveResourceValuesForRClass(layout.getClassName(), rLayoutClassElement);
    }

    List<ResourceValue> layouts = rClassResources.get(layout.getClassName());
    if (layouts == null) {
      errorLogger.logError("No layout files found for R class: %s", layout.getClassName());
      return Collections.emptyList();
    }

    List<ResourceValue> result = new ArrayList<>();
    String target = layout.getResourceName() + "_";
    for (ResourceValue otherLayout : layouts) {
      if (otherLayout.getResourceName().startsWith(target)) {
        result.add(otherLayout);
      }
    }

    return result;
  }

  /**
   * @param rClass        Class name for a resource, like R.layout
   * @param resourceClass The class element representing that resource eg the R.layout class
   */
  private void saveResourceValuesForRClass(ClassName rClass, Element resourceClass) {
    if (rClassResources.containsKey(rClass)) {
      return;
    }

    List<? extends Element> resourceElements = resourceClass.getEnclosedElements();
    List<ResourceValue> resourceNames = new ArrayList<>(resourceElements.size());
    for (Element resource : resourceElements) {
      if (!(resource instanceof VariableElement)) {
        continue;
      }

      String resourceName = resource.getSimpleName().toString();
      resourceNames.add(new ResourceValue(
          rClass,
          resourceName,
          0 // Don't care about this for our use case
      ));
    }

    rClassResources.put(rClass, resourceNames);
  }

  /**
   * Scans annotations that have resources as parameters. It supports both one resource parameter,
   * and parameters in an array. The R class, resource name, and value, is extracted to create a
   * corresponding {@link ResourceValue} for each resource.
   */
  private class AnnotationResourceParamScanner extends TreeScanner {
    private final List<ResourceValue> results = new ArrayList<>();
    private Element element;
    private Class annotationClass;
    /** Eg "string", "layout", etc */
    private String resourceType;

    void clearResults() {
      results.clear();
    }

    List<ResourceValue> getResults() {
      return results;
    }

    @Override
    public void visitSelect(JCTree.JCFieldAccess jcFieldAccess) {
      // This "visit" method is called for each parameter in the annotation, but only if the
      // parameter is a field type (eg R.layout.resource_name is a field inside the R.layout
      // class). This means this method will not pick up things like booleans and strings.

      // This is the resource parameter inside the annotation
      Symbol symbol = jcFieldAccess.sym;

      if (symbol instanceof VarSymbol
          && symbol.getEnclosingElement() != null // The R.resourceType class
          && symbol.getEnclosingElement().getEnclosingElement() != null // The R class
          && symbol.getEnclosingElement().getEnclosingElement().enclClass() != null) {

        ResourceValue result = parseResourceSymbol((VariableElement) symbol);
        if (result != null) {
          results.add(result);
        }
      }
    }

    private ResourceValue parseResourceSymbol(VariableElement symbol) {
      TypeElement resourceClass = (TypeElement) symbol.getEnclosingElement();

      // eg com.airbnb.epoxy.R
      String rClass =
          ((TypeElement) resourceClass.getEnclosingElement()).getQualifiedName().toString();

      // eg com.airbnb.epoxy.R.layout
      String resourceClassName = resourceClass.getQualifiedName().toString();

      // Make sure this is the right resource type
      if (!(rClass + "." + resourceType).equals(resourceClassName)) {
        errorLogger
            .logError("%s annotation requires a %s resource but received %s. (Element: %s)",
                annotationClass.getSimpleName(), resourceType, resourceClass,
                element.getSimpleName());
        return null;
      }

      // eg button_layout, as in R.layout.button_layout
      String resourceName = symbol.getSimpleName().toString();

      Object resourceValue = symbol.getConstantValue();
      if (!(resourceValue instanceof Integer)) {
        errorLogger.logError("%s annotation requires an int value but received %s. (Element: %s)",
            annotationClass.getSimpleName(), symbol.getSimpleName(), element.getSimpleName());
        return null;
      }

      ClassName rClassName = getClassName(resourceClassName, resourceType);
      saveResourceValuesForRClass(rClassName, resourceClass);

      return new ResourceValue(rClassName, resourceName, (int) resourceValue);
    }

    void setCurrentAnnotationDetails(Element element, Class annotationClass, String resourceType) {
      this.element = element;
      this.annotationClass = annotationClass;
      this.resourceType = resourceType;
    }
  }

  /**
   * Builds a JavaPoet ClassName from the string value of an R class. This is memoized since there
   * should be very few different R classes used.
   */
  private ClassName getClassName(String rClass, String resourceType) {
    ClassName className = rClassNameMap.get(rClass);

    if (className == null) {
      Element rClassElement = getElementByName(rClass, elementUtils, typeUtils);

      String rClassPackageName =
          elementUtils.getPackageOf(rClassElement).getQualifiedName().toString();
      className = ClassName.get(rClassPackageName, "R", resourceType);

      rClassNameMap.put(rClass, className);
    }

    return className;
  }
}
