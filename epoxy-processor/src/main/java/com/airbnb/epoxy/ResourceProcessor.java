package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;
import com.sun.source.tree.ClassTree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Scans R files and and compiles resource values in those R classes. This allows us to look up raw
 * layout resource values (eg 23523452) and convert that to the resource name (eg R.layout.my_view)
 * so that we can properly reference that resource. This is important in library projects where the
 * R value at process time can be different from the final R value in the app.
 * <p>
 * This is taken from Butterknife. https://github.com/JakeWharton/butterknife/pull/613
 */
class ResourceProcessor {
  private static final List<String> SUPPORTED_TYPES = Collections.singletonList("layout");
  private final Elements elementUtils;
  private final Types typeUtils;

  private Trees trees;
  private final Map<Integer, AndroidResource> resources = new LinkedHashMap<>();

  ResourceProcessor(ProcessingEnvironment processingEnv, Elements elementUtils, Types typeUtils) {
    this.elementUtils = elementUtils;
    this.typeUtils = typeUtils;
    try {
      trees = Trees.instance(processingEnv);
    } catch (IllegalArgumentException ignored) {

    }
  }

  AndroidResource getResourceForValue(int value) {
    if (resources.get(value) == null) {
      resources.put(value, new AndroidResource(value));
    }
    return resources.get(value);
  }

  void processorResources(RoundEnvironment env) {
    resources.clear();

    if (trees == null) {
      return;
    }

    RClassScanner scanner = new RClassScanner();

    for (Class<? extends Annotation> annotation : EpoxyProcessor.getSupportedAnnotations()) {
      for (Element element : env.getElementsAnnotatedWith(annotation)) {
        JCTree tree = (JCTree) trees.getTree(element, getMirror(element, annotation));
        if (tree != null) { // tree can be null if the references are compiled types and not source
          tree.accept(scanner);
        }
      }
    }

    for (String rClass : scanner.getRClasses()) {
      parseRClass(rClass, resources);
    }
  }

  private static AnnotationMirror getMirror(Element element,
      Class<? extends Annotation> annotation) {
    for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
      if (annotationMirror.getAnnotationType().toString().equals(annotation.getCanonicalName())) {
        return annotationMirror;
      }
    }
    return null;
  }

  private void parseRClass(String rClass, Map<Integer, AndroidResource> symbols) {
    Element element;

    try {
      element = elementUtils.getTypeElement(rClass);
    } catch (MirroredTypeException mte) {
      element = typeUtils.asElement(mte.getTypeMirror());
    }

    JCTree tree = (JCTree) trees.getTree(element);
    if (tree != null) { // tree can be null if the references are compiled types and not source
      IdScanner idScanner =
          new IdScanner(symbols, elementUtils.getPackageOf(element).getQualifiedName().toString());
      tree.accept(idScanner);
    } else {
      parseCompiledR((TypeElement) element, symbols);
    }
  }

  private void parseCompiledR(TypeElement rClass, Map<Integer, AndroidResource> symbols) {
    for (Element element : rClass.getEnclosedElements()) {
      String innerClassName = element.getSimpleName().toString();
      if (SUPPORTED_TYPES.contains(innerClassName)) {
        for (Element enclosedElement : element.getEnclosedElements()) {
          if (enclosedElement instanceof VariableElement) {
            VariableElement variableElement = (VariableElement) enclosedElement;
            Object value = variableElement.getConstantValue();

            if (value instanceof Integer) {
              int id = (Integer) value;
              ClassName rClassName =
                  ClassName.get(elementUtils.getPackageOf(variableElement).toString(), "R",
                      innerClassName);
              String resourceName = variableElement.getSimpleName().toString();
              symbols.put(id, new AndroidResource(id, rClassName, resourceName));
            }
          }
        }
      }
    }
  }

  private static class RClassScanner extends TreeScanner {
    private final Set<String> rClasses = new LinkedHashSet<>();

    @Override
    public void visitSelect(JCTree.JCFieldAccess jcFieldAccess) {
      Symbol symbol = jcFieldAccess.sym;
      if (symbol != null
          && symbol.getEnclosingElement() != null
          && symbol.getEnclosingElement().getEnclosingElement() != null
          && symbol.getEnclosingElement().getEnclosingElement().enclClass() != null) {
        rClasses.add(symbol.getEnclosingElement().getEnclosingElement().enclClass().className());
      }
    }

    Set<String> getRClasses() {
      return rClasses;
    }
  }

  private static class IdScanner extends TreeScanner {
    private final Map<Integer, AndroidResource> resourceValues;
    private final String packageName;

    IdScanner(Map<Integer, AndroidResource> resourceValues, String packageName) {
      this.resourceValues = resourceValues;
      this.packageName = packageName;
    }

    @Override
    public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
      for (JCTree tree : jcClassDecl.defs) {
        if (tree instanceof ClassTree) {
          ClassTree classTree = (ClassTree) tree;
          String className = classTree.getSimpleName().toString();
          if (SUPPORTED_TYPES.contains(className)) {
            ClassName rClassName = ClassName.get(packageName, "R", className);
            VarScanner scanner = new VarScanner(resourceValues, rClassName);
            ((JCTree) classTree).accept(scanner);
          }
        }
      }
    }
  }

  private static class VarScanner extends TreeScanner {
    private final Map<Integer, AndroidResource> resourceValues;
    private final ClassName className;

    private VarScanner(Map<Integer, AndroidResource> resourceValues, ClassName className) {
      this.resourceValues = resourceValues;
      this.className = className;
    }

    @Override
    public void visitVarDef(JCTree.JCVariableDecl jcVariableDecl) {
      String type = jcVariableDecl.getType().toString();
      if ("int".equals(type)) {
        int resourceValue = Integer.valueOf(jcVariableDecl.getInitializer().toString());
        String resourceName = jcVariableDecl.getName().toString();
        resourceValues
            .put(resourceValue, new AndroidResource(resourceValue, className, resourceName));
      }
    }
  }
}
