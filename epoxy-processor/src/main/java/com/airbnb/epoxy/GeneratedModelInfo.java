package com.airbnb.epoxy;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterSpec.Builder;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

abstract class GeneratedModelInfo {
  private static final String RESET_METHOD = "reset";
  protected static final String GENERATED_CLASS_NAME_SUFFIX = "_";

  protected TypeName superClassName;
  protected TypeElement superClassElement;
  protected TypeName parameterizedClassName;
  protected ClassName generatedClassName;
  protected TypeName boundObjectTypeName;
  protected boolean shouldGenerateModel;
  protected final Set<AttributeInfo> attributeInfo = new HashSet<>();
  protected final List<TypeVariableName> typeVariableNames = new ArrayList<>();
  protected final List<ConstructorInfo> constructors = new ArrayList<>();
  protected final Set<MethodInfo> methodsReturningClassType = new LinkedHashSet<>();

  /**
   * Get information about methods returning class type of the original class so we can duplicate
   * them in the generated class for chaining purposes
   */
  protected void collectMethodsReturningClassType(TypeElement modelClass, Types typeUtils) {
    TypeElement clazz = modelClass;
    while (clazz.getSuperclass().getKind() != TypeKind.NONE) {
      for (Element subElement : clazz.getEnclosedElements()) {
        Set<Modifier> modifiers = subElement.getModifiers();
        if (subElement.getKind() == ElementKind.METHOD
            && !modifiers.contains(Modifier.PRIVATE)
            && !modifiers.contains(Modifier.FINAL)
            && !modifiers.contains(Modifier.STATIC)) {
          TypeMirror methodReturnType = ((ExecutableType) subElement.asType()).getReturnType();
          if (methodReturnType.equals(clazz.asType())
              || typeUtils.isSubtype(clazz.asType(), methodReturnType)) {
            ExecutableElement castedSubElement = ((ExecutableElement) subElement);
            List<? extends VariableElement> params = castedSubElement.getParameters();
            String methodName = subElement.getSimpleName().toString();
            if (methodName.equals(RESET_METHOD) && params.isEmpty()) {
              continue;
            }
            methodsReturningClassType.add(new MethodInfo(methodName, modifiers,
                buildParamSpecs(params), castedSubElement.isVarArgs()));
          }
        }
      }
      clazz = (TypeElement) typeUtils.asElement(clazz.getSuperclass());
    }
  }

  protected List<ParameterSpec> buildParamSpecs(List<? extends VariableElement> params) {
    List<ParameterSpec> result = new ArrayList<>();

    for (VariableElement param : params) {
      Builder builder = ParameterSpec.builder(TypeName.get(param.asType()),
          param.getSimpleName().toString());
      for (AnnotationMirror annotation : param.getAnnotationMirrors()) {
        builder.addAnnotation(AnnotationSpec.get(annotation));
      }
      result.add(builder.build());
    }

    return result;
  }

  void addAttribute(AttributeInfo attributeInfo) {
    addAttributes(Collections.singletonList(attributeInfo));
  }

  void addAttributes(Collection<AttributeInfo> attributeInfo) {
    removeMethodIfDuplicatedBySetter(attributeInfo);
    this.attributeInfo.addAll(attributeInfo);
  }

  private void removeMethodIfDuplicatedBySetter(Collection<AttributeInfo> attributeInfos) {
    for (AttributeInfo attributeInfo : attributeInfos) {
      Iterator<MethodInfo> iterator = methodsReturningClassType.iterator();
      while (iterator.hasNext()) {
        MethodInfo methodInfo = iterator.next();
        if (methodInfo.name.equals(attributeInfo.getName())
            // checking for overloads
            && methodInfo.params.size() == 1
            && methodInfo.params.get(0).type.equals(attributeInfo.getTypeName())) {
          iterator.remove();
        }
      }
    }
  }

  TypeElement getSuperClassElement() {
    return superClassElement;
  }

  TypeName getSuperClassName() {
    return superClassName;
  }

  List<ConstructorInfo> getConstructors() {
    return constructors;
  }

  Set<MethodInfo> getMethodsReturningClassType() {
    return methodsReturningClassType;
  }

  ClassName getGeneratedName() {
    return generatedClassName;
  }

  Set<AttributeInfo> getAttributeInfo() {
    return attributeInfo;
  }

  boolean shouldGenerateModel() {
    return shouldGenerateModel;
  }

  Iterable<TypeVariableName> getTypeVariables() {
    return typeVariableNames;
  }

  TypeName getParameterizedGeneratedName() {
    return parameterizedClassName;
  }

  /**
   * Get the object type this model is typed with.
   */
  TypeName getModelType() {
    return boundObjectTypeName;
  }

  static class ConstructorInfo {
    final Set<Modifier> modifiers;
    final List<ParameterSpec> params;
    final boolean varargs;

    ConstructorInfo(Set<Modifier> modifiers, List<ParameterSpec> params, boolean varargs) {
      this.modifiers = modifiers;
      this.params = params;
      this.varargs = varargs;
    }
  }

  static class MethodInfo {
    final String name;
    final Set<Modifier> modifiers;
    final List<ParameterSpec> params;
    final boolean varargs;

    MethodInfo(String name, Set<Modifier> modifiers, List<ParameterSpec> params,
        boolean varargs) {
      this.name = name;
      this.modifiers = modifiers;
      this.params = params;
      this.varargs = varargs;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      MethodInfo that = (MethodInfo) o;

      if (varargs != that.varargs) {
        return false;
      }
      if (name != null ? !name.equals(that.name) : that.name != null) {
        return false;
      }
      if (modifiers != null ? !modifiers.equals(that.modifiers) : that.modifiers != null) {
        return false;
      }
      return params != null ? params.equals(that.params) : that.params == null;
    }

    @Override
    public int hashCode() {
      int result = name != null ? name.hashCode() : 0;
      result = 31 * result + (modifiers != null ? modifiers.hashCode() : 0);
      result = 31 * result + (params != null ? params.hashCode() : 0);
      result = 31 * result + (varargs ? 1 : 0);
      return result;
    }
  }

  @Override
  public String toString() {
    return "GeneratedModelInfo{"
        + "attributeInfo=" + attributeInfo
        + ", superClassName=" + superClassName
        + '}';
  }
}
