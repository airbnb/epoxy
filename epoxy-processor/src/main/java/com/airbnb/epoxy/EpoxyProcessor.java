package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;

import com.airbnb.epoxy.ClassToGenerateInfo.ConstructorInfo;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static com.squareup.javapoet.TypeName.BOOLEAN;
import static com.squareup.javapoet.TypeName.BYTE;
import static com.squareup.javapoet.TypeName.CHAR;
import static com.squareup.javapoet.TypeName.DOUBLE;
import static com.squareup.javapoet.TypeName.FLOAT;
import static com.squareup.javapoet.TypeName.INT;
import static com.squareup.javapoet.TypeName.LONG;
import static com.squareup.javapoet.TypeName.SHORT;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Looks for {@link EpoxyAttribute} annotations and generates a subclass for all classes that have
 * those attributes. The generated subclass includes setters, getters, equals, and hashcode for the
 * given field. Any constructors on the original class are duplicated. Abstract classes are ignored
 * since generated classes would have to be abstract in order to guarantee they compile, and that
 * reduces their usefulness and doesn't make as much sense to support.
 */
@AutoService(Processor.class)
public class EpoxyProcessor extends AbstractProcessor {
  private static final String GENERATED_CLASS_NAME_SUFFIX = "_";
  private static TypeMirror epoxyModelType;

  private Filer filer;
  private Messager messager;
  private Elements elementUtils;
  private Types typeUtils;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
    elementUtils = processingEnv.getElementUtils();
    epoxyModelType = elementUtils.getTypeElement(EpoxyModel.class.getCanonicalName()).asType();
    typeUtils = processingEnv.getTypeUtils();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Collections.singleton(EpoxyAttribute.class.getCanonicalName());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    LinkedHashMap<TypeElement, ClassToGenerateInfo> modelClassMap = new LinkedHashMap<>();

    for (Element attribute : roundEnv.getElementsAnnotatedWith(EpoxyAttribute.class)) {
      try {
        processAttribute(attribute, modelClassMap);
      } catch (EpoxyProcessorException e) {
        writeError(e);
      }
    }

    updateClassesForInheritance(modelClassMap);

    for (Entry<TypeElement, ClassToGenerateInfo> modelEntry : modelClassMap.entrySet()) {
      try {
        generateClassForModel(modelEntry.getValue());
      } catch (IOException e) {
        writeError(e);
      }
    }

    return true;
  }

  private void processAttribute(Element attribute,
      Map<TypeElement, ClassToGenerateInfo> modelClassMap)
      throws EpoxyProcessorException {

    validateAccessibleViaGeneratedCode(attribute);
    TypeElement classElement = (TypeElement) attribute.getEnclosingElement();

    ClassToGenerateInfo helperClass = getOrCreateTargetClass(modelClassMap, classElement);

    String name = attribute.getSimpleName().toString();
    TypeName type = TypeName.get(attribute.asType());
    boolean hasSuper = hasSuperMethod(classElement, name);
    boolean hasFinalModifier = attribute.getModifiers().contains(FINAL);
    boolean packagePrivate = isFieldPackagePrivate(attribute);
    helperClass.addAttribute(
        new AttributeInfo(name, type, attribute.getAnnotationMirrors(),
            attribute.getAnnotation(EpoxyAttribute.class), hasSuper, hasFinalModifier,
            packagePrivate));
  }

  /**
   * Checks if the given field has package-private visibility
   */
  private boolean isFieldPackagePrivate(Element attribute) {
    Set<Modifier> modifiers = attribute.getModifiers();
    return !modifiers.contains(PUBLIC) && !modifiers.contains(PROTECTED);
  }

  /**
   * Check if the given class or any of its super classes have a super method with the given name.
   * Private methods are ignored since the generated subclass can't call super on those.
   */
  private boolean hasSuperMethod(TypeElement classElement, String methodName) {
    if (!isSubtype(classElement.asType(), epoxyModelType)) {
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

  private void validateAccessibleViaGeneratedCode(Element attribute) throws
      EpoxyProcessorException {

    TypeElement enclosingElement = (TypeElement) attribute.getEnclosingElement();

    // Verify method modifiers.
    Set<Modifier> modifiers = attribute.getModifiers();
    if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
      throwError(
          "%s annotations must not be on private or static fields. (class: %s, field: %s)",
          EpoxyAttribute.class.getSimpleName(),
          enclosingElement.getSimpleName(), attribute.getSimpleName());
    }

    // Nested classes must be static
    if (enclosingElement.getNestingKind().isNested()) {
      if (!enclosingElement.getModifiers().contains(STATIC)) {
        throwError(
            "Nested classes with %s annotations must be static. (class: %s, field: %s)",
            EpoxyAttribute.class.getSimpleName(),
            enclosingElement.getSimpleName(), attribute.getSimpleName());
      }
    }

    // Verify containing type.
    if (enclosingElement.getKind() != CLASS) {
      throwError("%s annotations may only be contained in classes. (class: %s, field: %s)",
          EpoxyAttribute.class.getSimpleName(),
          enclosingElement.getSimpleName(), attribute.getSimpleName());
    }

    // Verify containing class visibility is not private.
    if (enclosingElement.getModifiers().contains(PRIVATE)) {
      throwError("%s annotations may not be contained in private classes. (class: %s, field: %s)",
          EpoxyAttribute.class.getSimpleName(),
          enclosingElement.getSimpleName(), attribute.getSimpleName());
    }
  }

  private ClassToGenerateInfo getOrCreateTargetClass(
      Map<TypeElement, ClassToGenerateInfo> modelClassMap, TypeElement classElement)
      throws EpoxyProcessorException {

    ClassToGenerateInfo classToGenerateInfo = modelClassMap.get(classElement);

    boolean isFinal = classElement.getModifiers().contains(Modifier.FINAL);
    if (isFinal) {
      throwError("Class with %s annotations cannot be final: %s",
          EpoxyAttribute.class.getSimpleName(), classElement.getSimpleName());
    }

    if (!isSubtype(classElement.asType(), epoxyModelType)) {
      throwError("Class with %s annotations must extend %s (%s)",
          EpoxyAttribute.class.getSimpleName(), epoxyModelType,
          classElement.getSimpleName());
    }

    if (classToGenerateInfo == null) {
      ClassName generatedClassName = getGeneratedClassName(classElement);
      boolean isAbstract = classElement.getModifiers().contains(Modifier.ABSTRACT);
      classToGenerateInfo = new ClassToGenerateInfo(classElement, generatedClassName, isAbstract);
      modelClassMap.put(classElement, classToGenerateInfo);
    }

    return classToGenerateInfo;
  }

  private ClassName getGeneratedClassName(TypeElement classElement) {
    String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();

    int packageLen = packageName.length() + 1;
    String className =
        classElement.getQualifiedName().toString().substring(packageLen).replace('.', '$');

    return ClassName.get(packageName, className + GENERATED_CLASS_NAME_SUFFIX);
  }

  /**
   * Check each model for super classes that also have attributes. For each super class with
   * attributes we add those attributes to the attributes of the generated class, so that a
   * generated class contains all the attributes of its super classes combined.
   */
  private void updateClassesForInheritance(
      LinkedHashMap<TypeElement, ClassToGenerateInfo> helperClassMap) {
    for (Entry<TypeElement, ClassToGenerateInfo> entry : helperClassMap.entrySet()) {
      LinkedHashMap<TypeElement, ClassToGenerateInfo> otherClasses =
          new LinkedHashMap<>(helperClassMap);
      otherClasses.remove(entry.getKey());

      for (Entry<TypeElement, ClassToGenerateInfo> otherEntry : otherClasses.entrySet()) {
        if (isSubtype(entry.getKey().asType(), otherEntry.getKey().asType())) {
          if (belongToTheSamePackage(entry.getKey(), otherEntry.getKey())) {
            entry.getValue().addAttributes(otherEntry.getValue().getAttributeInfo());
          } else {
            for (AttributeInfo attribute : otherEntry.getValue().getAttributeInfo()) {
              if (!attribute.isPackagePrivate()) {
                entry.getValue().addAttribute(attribute);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Checks if two classes belong to the same package
   */
  private boolean belongToTheSamePackage(TypeElement class1, TypeElement class2) {
    Name package1 = elementUtils.getPackageOf(class1).getQualifiedName();
    Name package2 = elementUtils.getPackageOf(class2).getQualifiedName();
    return package1.equals(package2);
  }

  private boolean isSubtype(TypeMirror e1, TypeMirror e2) {
    // We use erasure so that EpoxyModelA is considered a subtype of EpoxyModel<T extends View>
    return typeUtils.isSubtype(e1, typeUtils.erasure(e2));
  }

  private void generateClassForModel(ClassToGenerateInfo info) throws IOException {
    if (info.isOriginalClassAbstract()) {
      // Don't extend classes that are abstract. If they don't contain all required
      // methods then our generated class won't compile
      return;
    }

    TypeSpec generatedClass = TypeSpec.classBuilder(info.getGeneratedName())
        .addJavadoc("Generated file. Do not modify!")
        .addModifiers(Modifier.PUBLIC)
        .superclass(info.getOriginalClassName())
        .addTypeVariables(info.getTypeVariables())
        .addMethods(generateConstructors(info))
        .addMethods(generateSettersAndGetters(info))
        .build();

    JavaFile.builder(info.getGeneratedName().packageName(), generatedClass)
        .build()
        .writeTo(filer);
  }

  /** Include any constructors that are in the super class. */
  private Iterable<MethodSpec> generateConstructors(ClassToGenerateInfo info) {
    List<MethodSpec> constructors = new ArrayList<>(info.getConstructors().size());

    for (ConstructorInfo constructorInfo : info.getConstructors()) {
      Builder builder = MethodSpec.constructorBuilder()
          .addModifiers(constructorInfo.modifiers)
          .addParameters(constructorInfo.params);

      StringBuilder statementBuilder = new StringBuilder("super(");
      boolean first = true;
      for (ParameterSpec param : constructorInfo.params) {
        if (!first) {
          statementBuilder.append(", ");
        }
        first = false;
        statementBuilder.append(param.name);
      }
      statementBuilder.append(")");

      constructors.add(builder
          .addStatement(statementBuilder.toString())
          .build());
    }

    return constructors;
  }

  private List<MethodSpec> generateSettersAndGetters(ClassToGenerateInfo helperClass) {
    List<MethodSpec> methods = new ArrayList<>();

    for (AttributeInfo data : helperClass.getAttributeInfo()) {
      if (!data.hasFinalModifier()) {
        methods.add(generateSetter(helperClass, data));
      }
      methods.add(generateGetter(data));
    }

    methods.addAll(buildDefaultSetters(helperClass));
    methods.add(buildEquals(helperClass));
    methods.add(buildHashCode(helperClass));

    return methods;
  }

  private MethodSpec buildEquals(ClassToGenerateInfo helperClass) {
    Builder builder = MethodSpec.methodBuilder("equals")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(boolean.class)
        .addParameter(Object.class, "o")
        .beginControlFlow("if (o == this)")
        .addStatement("return true")
        .endControlFlow()
        .beginControlFlow("if (!(o instanceof $T))", helperClass.getOriginalClassNameWithoutType())
        .addStatement("return false")
        .endControlFlow()
        .beginControlFlow("if (!super.equals(o))")
        .addStatement("return false")
        .endControlFlow()
        .addStatement("$T that = ($T) o", helperClass.getGeneratedName(),
            helperClass.getGeneratedName());

    for (AttributeInfo attributeInfo : helperClass.getAttributeInfo()) {
      if (!attributeInfo.useInHash()) {
        continue;
      }

      TypeName type = attributeInfo.getType();
      String name = attributeInfo.getName();
      if (type == FLOAT) {
        builder.beginControlFlow("if (Float.compare(that.$L, $L) != 0)", name, name)
            .addStatement("return false")
            .endControlFlow();
      } else if (type == DOUBLE) {
        builder.beginControlFlow("if (Double.compare(that.$L, $L) != 0)", name, name)
            .addStatement("return false")
            .endControlFlow();
      } else if (type.isPrimitive()) {
        builder.beginControlFlow("if ($L != that.$L)", name, name)
            .addStatement("return false")
            .endControlFlow();
      } else if (type instanceof ArrayTypeName) {
        builder.beginControlFlow("if (!$T.equals($L, that.$L))", TypeName.get(Arrays.class), name,
            name)
            .addStatement("return false")
            .endControlFlow();
      } else {
        builder
            .beginControlFlow("if ($L != null ? !$L.equals(that.$L) : that.$L != null)", name, name,
                name, name)
            .addStatement("return false")
            .endControlFlow();
      }
    }

    return builder
        .addStatement("return true")
        .build();
  }

  private MethodSpec buildHashCode(ClassToGenerateInfo helperClass) {
    Builder builder = MethodSpec.methodBuilder("hashCode")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(int.class)
        .addStatement("int result = super.hashCode()");

    for (AttributeInfo attributeInfo : helperClass.getAttributeInfo()) {
      if (!attributeInfo.useInHash()) {
        continue;
      }
      if (attributeInfo.getType() == DOUBLE) {
        builder.addStatement("long temp");
        break;
      }
    }

    for (AttributeInfo attributeInfo : helperClass.getAttributeInfo()) {
      if (!attributeInfo.useInHash()) {
        continue;
      }

      TypeName type = attributeInfo.getType();
      String name = attributeInfo.getName();

      if ((type == BYTE) || (type == CHAR) || (type == SHORT) || (type == INT)) {
        builder.addStatement("result = 31 * result + $L", name);
      } else if (type == LONG) {
        builder.addStatement("result = 31 * result + (int) ($L ^ ($L >>> 32))", name, name);
      } else if (type == FLOAT) {
        builder.addStatement("result = 31 * result + ($L != +0.0f ? Float.floatToIntBits($L) : 0)",
            name, name);
      } else if (type == DOUBLE) {
        builder.addStatement("temp = Double.doubleToLongBits($L)", name)
            .addStatement("result = 31 * result + (int) (temp ^ (temp >>> 32))");
      } else if (type == BOOLEAN) {
        builder.addStatement("result = 31 * result + ($L ? 1 : 0)", name);
      } else if (type instanceof ArrayTypeName) {
        builder.addStatement("result = 31 * result + Arrays.hashCode($L)", name);
      } else {
        builder.addStatement("result = 31 * result + ($L != null ? $L.hashCode() : 0)", name, name);
      }
    }

    return builder
        .addStatement("return result")
        .build();
  }

  private MethodSpec generateGetter(AttributeInfo data) {
    return MethodSpec.methodBuilder(data.getName())
        .addModifiers(Modifier.PUBLIC)
        .returns(data.getType())
        .addAnnotations(data.getGetterAnnotations())
        .addStatement("return $L", data.getName())
        .build();
  }

  private MethodSpec generateSetter(ClassToGenerateInfo helperClass, AttributeInfo data) {
    String attributeName = data.getName();
    Builder builder = MethodSpec.methodBuilder(attributeName)
        .addModifiers(Modifier.PUBLIC)
        .returns(helperClass.getParameterizedGeneratedName())
        .addParameter(ParameterSpec.builder(data.getType(), attributeName)
            .addAnnotations(data.getSetterAnnotations()).build())
        .addStatement("this.$L = $L", attributeName, attributeName);

    if (data.hasSuperSetterMethod()) {
      builder.addStatement("super.$L($L)", attributeName, attributeName);
    }

    return builder
        .addStatement("return this")
        .build();
  }

  /**
   * Include overrides of the setters on the base EpoxyModel class so that calling them returns the
   * generated class type for use in chaining.
   */
  private List<MethodSpec> buildDefaultSetters(ClassToGenerateInfo helperClass) {
    List<MethodSpec> result = new ArrayList<>();

    result.add(MethodSpec.methodBuilder("id")
        .addModifiers(Modifier.PUBLIC)
        .returns(helperClass.getParameterizedGeneratedName())
        .addAnnotation(Override.class)
        .addParameter(long.class, "id")
        .addStatement("super.id(id)")
        .addStatement("return this")
        .build());

    result.add(MethodSpec.methodBuilder("layout")
        .addModifiers(Modifier.PUBLIC)
        .returns(helperClass.getParameterizedGeneratedName())
        .addAnnotation(Override.class)
        .addParameter(
            ParameterSpec.builder(int.class, "layoutRes").addAnnotation(LayoutRes.class).build())
        .addStatement("super.layout(layoutRes)")
        .addStatement("return this")
        .build());

    result.add(MethodSpec.methodBuilder("show")
        .addModifiers(Modifier.PUBLIC)
        .returns(helperClass.getParameterizedGeneratedName())
        .addAnnotation(Override.class)
        .addStatement("super.show()")
        .addStatement("return this")
        .build());

    result.add(MethodSpec.methodBuilder("show")
        .addModifiers(Modifier.PUBLIC)
        .returns(helperClass.getParameterizedGeneratedName())
        .addParameter(boolean.class, "show")
        .addAnnotation(Override.class)
        .addStatement("super.show(show)")
        .addStatement("return this")
        .build());

    result.add(MethodSpec.methodBuilder("hide")
        .addModifiers(Modifier.PUBLIC)
        .returns(helperClass.getParameterizedGeneratedName())
        .addAnnotation(Override.class)
        .addStatement("super.hide()")
        .addStatement("return this")
        .build());

    return result;
  }

  private void writeError(Exception e) {
    messager.printMessage(Diagnostic.Kind.ERROR, e.toString());
  }

  private void throwError(String msg, Object... args)
      throws EpoxyProcessorException {
    throw new EpoxyProcessorException(String.format(msg, args));
  }
}
