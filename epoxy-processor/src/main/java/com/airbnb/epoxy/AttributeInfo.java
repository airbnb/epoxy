package com.airbnb.epoxy;

import com.airbnb.epoxy.EpoxyAttribute.Option;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Types;

import static com.airbnb.epoxy.ProcessorUtils.isViewClickListenerType;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;

class AttributeInfo {

  private final List<AnnotationSpec> setterAnnotations = new ArrayList<>();
  private final List<AnnotationSpec> getterAnnotations = new ArrayList<>();
  private final String name;
  private final TypeName type;
  private final boolean useInHash;
  private final boolean allowMissingHash;
  private final boolean generateSetter;
  private final boolean generateGetter;
  private final boolean hasFinalModifier;
  private final boolean packagePrivate;
  /**
   * Track whether there is a setter method for this attribute on a super class so that we can call
   * through to super.
   */
  private final boolean hasSuperSetter;
  private final Element attributeElement;
  private final Types typeUtils;

  private final TypeElement classElement;

  AttributeInfo(Element attribute, Types typeUtils, ErrorLogger errorLogger) {
    attributeElement = attribute;
    this.typeUtils = typeUtils;
    this.name = attribute.getSimpleName().toString();
    this.type = TypeName.get(attribute.asType());

    classElement = (TypeElement) attribute.getEnclosingElement();
    this.hasSuperSetter = hasSuperMethod(classElement, name);
    this.hasFinalModifier = attribute.getModifiers().contains(FINAL);
    this.packagePrivate = isFieldPackagePrivate(attribute);

    EpoxyAttribute annotation = attribute.getAnnotation(EpoxyAttribute.class);
    Set<Option> options = new HashSet<>(Arrays.asList(annotation.value()));
    validateAnnotationOptions(errorLogger, annotation, options);

    useInHash = annotation.hash() && !options.contains(Option.DoNotHash);
    allowMissingHash = options.contains(Option.AllowMissingHash);

    generateSetter = annotation.setter() && !options.contains(Option.NoSetter);
    generateGetter = !options.contains(Option.NoGetter);
    buildAnnotationLists(attribute.getAnnotationMirrors());
  }

  private void validateAnnotationOptions(ErrorLogger errorLogger, EpoxyAttribute annotation,
      Set<Option> options) {

    if (options.contains(Option.AllowMissingHash) && options.contains(Option.DoNotHash)) {
      errorLogger
          .logError("Illegal to use both %s and %s options in an %s annotation. (%s#%s)",
              Option.DoNotHash,
              Option.AllowMissingHash,
              EpoxyAttribute.class.getSimpleName(),
              classElement.getSimpleName(),
              name);
    }

    // Don't let legacy values be mixed with the new Options values
    if (!options.isEmpty()) {
      if (!annotation.hash()) {
        errorLogger
            .logError("Don't use hash=false in an %s if you are using options. Instead, use the"
                    + " %s option. (%s#%s)",
                EpoxyAttribute.class.getSimpleName(),
                Option.DoNotHash,
                classElement.getSimpleName(),
                name);
      }

      if (!annotation.setter()) {
        errorLogger
            .logError("Don't use setter=false in an %s if you are using options. Instead, use the"
                    + " %s option. (%s#%s)",
                EpoxyAttribute.class.getSimpleName(),
                Option.NoSetter,
                classElement.getSimpleName(),
                name);
      }
    }
  }

  /**
   * Check if the given class or any of its super classes have a super method with the given name.
   * Private methods are ignored since the generated subclass can't call super on those.
   */
  private boolean hasSuperMethod(TypeElement classElement, String methodName) {
    if (!ProcessorUtils.isEpoxyModel(classElement.asType())) {
      return false;
    }

    for (Element subElement : classElement.getEnclosedElements()) {
      if (subElement.getKind() == ElementKind.METHOD
          && !subElement.getModifiers().contains(Modifier.PRIVATE)
          && subElement.getSimpleName().toString().equals(methodName)) {
        return true;
      }
    }

    Element superClass = typeUtils.asElement(classElement.getSuperclass());
    return (superClass instanceof TypeElement)
        && hasSuperMethod((TypeElement) superClass, methodName);
  }

  /**
   * Checks if the given field has package-private visibility
   */
  private boolean isFieldPackagePrivate(Element attribute) {
    Set<Modifier> modifiers = attribute.getModifiers();
    return !modifiers.contains(PUBLIC)
        && !modifiers.contains(PROTECTED)
        && !modifiers.contains(PRIVATE);
  }

  /**
   * Keeps track of annotations on the attribute so that they can be used in the generated setter
   * and getter method. Setter and getter annotations are stored separately since the annotation may
   * not target both method and parameter types.
   */
  private void buildAnnotationLists(List<? extends AnnotationMirror> annotationMirrors) {
    for (AnnotationMirror annotationMirror : annotationMirrors) {
      if (!annotationMirror.getElementValues().isEmpty()) {
        // Not supporting annotations with values for now
        continue;
      }

      ClassName annotationClass =
          ClassName.bestGuess(annotationMirror.getAnnotationType().toString());
      if (annotationClass.equals(ClassName.get(EpoxyAttribute.class))) {
        // Don't include our own annotation
        continue;
      }

      DeclaredType annotationType = annotationMirror.getAnnotationType();
      // A target may exist on an annotation type to specify where the annotation can
      // be used, for example fields, methods, or parameters.
      Target targetAnnotation = annotationType.asElement().getAnnotation(Target.class);

      // Allow all target types if no target was specified on the annotation
      List<ElementType> elementTypes =
          Arrays.asList(targetAnnotation == null ? ElementType.values() : targetAnnotation.value());

      AnnotationSpec annotationSpec = AnnotationSpec.builder(annotationClass).build();
      if (elementTypes.contains(ElementType.PARAMETER)) {
        setterAnnotations.add(annotationSpec);
      }

      if (elementTypes.contains(ElementType.METHOD)) {
        getterAnnotations.add(annotationSpec);
      }
    }
  }

  String getName() {
    return name;
  }

  TypeName getType() {
    return type;
  }

  boolean useInHash() {
    return useInHash;
  }

  public boolean allowMissingHash() {
    return allowMissingHash;
  }

  boolean generateSetter() {
    return generateSetter;
  }

  List<AnnotationSpec> getSetterAnnotations() {
    return setterAnnotations;
  }

  public boolean generateGetter() {
    return generateGetter;
  }

  List<AnnotationSpec> getGetterAnnotations() {
    return getterAnnotations;
  }

  boolean hasSuperSetterMethod() {
    return hasSuperSetter;
  }

  boolean hasFinalModifier() {
    return hasFinalModifier;
  }

  boolean isPackagePrivate() {
    return packagePrivate;
  }

  @Override
  public String toString() {
    return "ModelAttributeData{"
        + "name='" + name + '\''
        + ", type=" + type
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AttributeInfo)) {
      return false;
    }

    AttributeInfo that = (AttributeInfo) o;

    if (!name.equals(that.name)) {
      return false;
    }
    return type.equals(that.type);
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + type.hashCode();
    return result;
  }

  Element getAttributeElement() {
    return attributeElement;
  }

  TypeElement getClassElement() {
    return classElement;
  }

  boolean isViewClickListener() {
    return isViewClickListenerType(attributeElement);
  }
}
