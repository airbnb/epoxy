
package com.airbnb.epoxy;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterSpec.Builder;
import com.squareup.javapoet.ParameterizedTypeName;
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
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public class ClassToGenerateInfo {

  private static final String RESET_METHOD = "reset";

  private final TypeName originalClassName;
  private final TypeName originalClassNameWithoutType;
  private final TypeName parameterizedClassName;
  private final ClassName generatedClassName;
  private final boolean isOriginalClassAbstract;
  private final Set<AttributeInfo> attributeInfo = new HashSet<>();
  private final List<TypeVariableName> typeVariableNames = new ArrayList<>();
  private final List<ConstructorInfo> constructors = new ArrayList<>();
  private final Set<MethodInfo> methodsReturningClassType = new LinkedHashSet<>();
  private final Types typeUtils;

  public ClassToGenerateInfo(Types typeUtils, TypeElement originalClassName,
      ClassName generatedClassName, boolean isOriginalClassAbstract) {
    this.typeUtils = typeUtils;
    this.originalClassName = ParameterizedTypeName.get(originalClassName.asType());
    this.originalClassNameWithoutType = ClassName.get(originalClassName);

    for (TypeParameterElement typeParameterElement : originalClassName.getTypeParameters()) {
      typeVariableNames.add(TypeVariableName.get(typeParameterElement));
    }

    collectOriginalClassConstructors(originalClassName);
    collectMethodsReturningClassType(originalClassName);

    if (!typeVariableNames.isEmpty()) {
      TypeVariableName[] typeArguments =
          typeVariableNames.toArray(new TypeVariableName[typeVariableNames.size()]);
      this.parameterizedClassName = ParameterizedTypeName.get(generatedClassName, typeArguments);
    } else {
      this.parameterizedClassName = generatedClassName;
    }

    this.generatedClassName = generatedClassName;
    this.isOriginalClassAbstract = isOriginalClassAbstract;
  }

  /**
   * Get information about constructors of the original class so we can duplicate
     them in the generated class and call through to super with the proper parameters
   */
  private void collectOriginalClassConstructors(TypeElement originalClass) {
    for (Element subElement : originalClass.getEnclosedElements()) {
      if (subElement.getKind() == ElementKind.CONSTRUCTOR
          && !subElement.getModifiers().contains(Modifier.PRIVATE)) {
        ExecutableElement castedSubElement = ((ExecutableElement) subElement);
        List<? extends VariableElement> params = castedSubElement.getParameters();
        constructors
            .add(new ConstructorInfo(subElement.getModifiers(), buildParamList(params),
                castedSubElement.isVarArgs()));
      }
    }
  }

  /**
   * Get information about methods returning class type of the original class so we can
   * duplicate them in the generated class for chaining purposes
   */
  private void collectMethodsReturningClassType(TypeElement originalClass) {
    TypeElement clazz = originalClass;
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
                buildParamList(params), castedSubElement.isVarArgs()));
          }
        }
      }
      clazz = (TypeElement) typeUtils.asElement(clazz.getSuperclass());
    }
  }

  private List<ParameterSpec> buildParamList(List<? extends VariableElement> params) {
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

  public void addAttribute(AttributeInfo attributeInfo) {
    addAttributes(Collections.singletonList(attributeInfo));
  }

  public void addAttributes(Collection<AttributeInfo> attributeInfo) {
    removeMethodIfDuplicatedBySetter(attributeInfo);
    this.attributeInfo.addAll(attributeInfo);
  }

  public TypeName getOriginalClassName() {
    return originalClassName;
  }

  public TypeName getOriginalClassNameWithoutType() {
    return originalClassNameWithoutType;
  }

  public List<ConstructorInfo> getConstructors() {
    return constructors;
  }

  public Set<MethodInfo> getMethodsReturningClassType() {
    return methodsReturningClassType;
  }

  public ClassName getGeneratedName() {
    return generatedClassName;
  }

  public Set<AttributeInfo> getAttributeInfo() {
    return attributeInfo;
  }

  public boolean isOriginalClassAbstract() {
    return isOriginalClassAbstract;
  }

  public Iterable<TypeVariableName> getTypeVariables() {
    return typeVariableNames;
  }

  public TypeName getParameterizedGeneratedName() {
    return parameterizedClassName;
  }

  private void removeMethodIfDuplicatedBySetter(Collection<AttributeInfo> attributeInfos) {
    for (AttributeInfo attributeInfo : attributeInfos) {
      Iterator<MethodInfo> iterator = methodsReturningClassType.iterator();
      while (iterator.hasNext()) {
        MethodInfo methodInfo = iterator.next();
        if (methodInfo.name.equals(attributeInfo.getName())
            // checking for overloads
            && methodInfo.params.size() == 1
            && methodInfo.params.get(0).type.equals(attributeInfo.getType())) {
          iterator.remove();
        }
      }
    }
  }

  public static class ConstructorInfo {
    final Set<Modifier> modifiers;
    final List<ParameterSpec> params;
    final boolean varargs;

    public ConstructorInfo(Set<Modifier> modifiers, List<ParameterSpec> params, boolean varargs) {
      this.modifiers = modifiers;
      this.params = params;
      this.varargs = varargs;
    }
  }

  public static class MethodInfo {
    final String name;
    final Set<Modifier> modifiers;
    final List<ParameterSpec> params;
    final boolean varargs;

    public MethodInfo(String name, Set<Modifier> modifiers, List<ParameterSpec> params,
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
}
