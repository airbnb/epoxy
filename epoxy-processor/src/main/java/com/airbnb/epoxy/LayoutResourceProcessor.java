package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Scans R files and and compiles layout resource values in those R classes. This allows us to look
 * up raw layout resource values (eg 23523452) and convert that to the resource name (eg
 * R.layout.my_view) so that we can properly reference that resource. This is important in library
 * projects where the R value at process time can be different from the final R value in the app.
 * <p>
 * This is adapted from Butterknife. https://github.com/JakeWharton/butterknife/pull/613
 */
class LayoutResourceProcessor {

  private final ErrorLogger errorLogger;
  private final Elements elementUtils;
  private final Types typeUtils;

  private Trees trees;
  private final Map<String, ClassName> rClassNameMap = new HashMap<>();
  private final AnnotationLayoutParamScanner scanner = new AnnotationLayoutParamScanner();

  LayoutResourceProcessor(ProcessingEnvironment processingEnv, ErrorLogger errorLogger,
      Elements elementUtils, Types typeUtils) {
    this.errorLogger = errorLogger;
    this.elementUtils = elementUtils;
    this.typeUtils = typeUtils;

    try {
      trees = Trees.instance(processingEnv);
    } catch (IllegalArgumentException ignored) {

    }
  }

  LayoutResource getLayoutInAnnotation(TypeElement element, Class annotationClass) {
    List<LayoutResource> layouts = getLayoutsInAnnotation(element, annotationClass);
    if (layouts.size() != 1) {
      errorLogger.logError(
          "Expected exactly 1 layout resource in the %s annotation but received %s. Annotated "
              + "element is %s",
          annotationClass.getSimpleName(), layouts.size(), element.getSimpleName());

      if (layouts.isEmpty()) {
        // Just pass back something so the code can compile before the error logger prints
        return new LayoutResource(0);
      }
    }

    return layouts.get(0);
  }

  /**
   * Get detailed information about the layout resources that are parameters to the given
   * annotation.
   */
  List<LayoutResource> getLayoutsInAnnotation(Element element, Class annotationClass) {
    List<Integer> layoutValues = getLayoutValues(element, annotationClass);
    List<LayoutResource> resources = new ArrayList<>(layoutValues.size());

    JCTree tree = (JCTree) trees.getTree(element, getAnnotationMirror(element, annotationClass));
    // tree can be null if the references are compiled types and not source
    if (tree != null) {
      // Collects details about the layout resource used for the annotation parameter
      scanner.clearResults();
      scanner.setCurrentAnnotationDetails(element, annotationClass);
      tree.accept(scanner);
      List<ScannerResult> scannerResults = scanner.getResults();

      for (ScannerResult scannerResult : scannerResults) {
        resources.add(new LayoutResource(
            getClassName(scannerResult.rClass),
            scannerResult.resourceName,
            scannerResult.resourceValue
        ));
      }
    }

    // Layout values may not have been picked up by the scanner if they are hardcoded.
    // In that case we just use the hardcoded value without an R class
    if (resources.size() != layoutValues.size()) {
      for (int layoutValue : layoutValues) {
        if (!isLayoutValueInResources(resources, layoutValue)) {
          resources.add(new LayoutResource(layoutValue));
        }
      }
    }

    return resources;
  }

  private boolean isLayoutValueInResources(List<LayoutResource> resources, int layoutValue) {
    for (LayoutResource resource : resources) {
      if (resource.value == layoutValue) {
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
   * Scans annotations that have layout resources as parameters. It supports both one layout
   * parameter, and parameters in an array. The R class, resource name, and value, is extract from
   * each layout to create a corresponding {@link ScannerResult} for each layout.
   */
  private class AnnotationLayoutParamScanner extends TreeScanner {
    private final List<ScannerResult> results = new ArrayList<>();
    private Element element;
    private Class annotationClass;

    void clearResults() {
      results.clear();
    }

    List<ScannerResult> getResults() {
      return new ArrayList<>(results);
    }

    @Override
    public void visitSelect(JCTree.JCFieldAccess jcFieldAccess) {
      // This "visit" method is called for each parameter in the annotation, but only if the
      // parameter is a field type (eg R.layout.resource_name is a field inside the R.layout
      // class). This means this method will not pick up things like booleans and strings.

      // This is the layout resource parameter inside the EpoxyModelClass annotation
      Symbol symbol = jcFieldAccess.sym;

      if (symbol instanceof VarSymbol
          && symbol.getEnclosingElement() != null // The R.layout class
          && symbol.getEnclosingElement().getEnclosingElement() != null // The R class
          && symbol.getEnclosingElement().getEnclosingElement().enclClass() != null) {

        ScannerResult result = parseResourceSymbol((VarSymbol) symbol);
        if (result != null) {
          results.add(result);
        }
      }
    }

    private ScannerResult parseResourceSymbol(VarSymbol symbol) {
      // eg com.airbnb.epoxy.R
      String rClass = symbol.getEnclosingElement().getEnclosingElement().enclClass().className();

      // eg com.airbnb.epoxy.R.layout
      String layoutClass = symbol.getEnclosingElement().getQualifiedName().toString();

      // Make sure this is a layout resource
      if (!(rClass + ".layout").equals(layoutClass)) {
        errorLogger
            .logError("%s annotation requires a layout resource but received %s. (Element: %s)",
                annotationClass.getSimpleName(), layoutClass, element.getSimpleName());
        return null;
      }

      // eg button_layout, as in R.layout.button_layout
      String layoutResourceName = symbol.getSimpleName().toString();

      Object layoutValue = symbol.getConstantValue();
      if (!(layoutValue instanceof Integer)) {
        errorLogger.logError("%s annotation requires an int value but received %s. (Element: %s)",
            annotationClass.getSimpleName(), symbol.getQualifiedName(), element.getSimpleName());
        return null;
      }

      return new ScannerResult(rClass, layoutResourceName, (int) layoutValue);
    }

    void setCurrentAnnotationDetails(Element element, Class annotationClass) {
      this.element = element;
      this.annotationClass = annotationClass;
    }
  }

  private static class ScannerResult {
    final String rClass;
    final String resourceName;
    final int resourceValue;

    private ScannerResult(String rClass, String resourceName, int resourceValue) {
      this.rClass = rClass;
      this.resourceName = resourceName;
      this.resourceValue = resourceValue;
    }
  }

  /**
   * Builds a JavaPoet ClassName from the string value of an R class. This is memoized since there
   * should be very few different R classes used.
   */
  private ClassName getClassName(String rClass) {
    ClassName className = rClassNameMap.get(rClass);

    if (className == null) {
      Element rClassElement = Utils.getElementByName(rClass, elementUtils, typeUtils);

      String rClassPackageName =
          elementUtils.getPackageOf(rClassElement).getQualifiedName().toString();
      className = ClassName.get(rClassPackageName, "R", "layout");

      rClassNameMap.put(rClass, className);
    }

    return className;
  }
}
