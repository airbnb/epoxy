
package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.SimpleTypeVisitor6;

public class ClassToGenerateInfo {

  private final TypeName originalClassName;
  private final TypeName originalClassNameWithoutType;
  private final TypeName parameterizedClassName;
  private final ClassName generatedClassName;
  private final boolean isOriginalClassAbstract;
  private final Set<AttributeInfo> attributeInfo = new HashSet<>();
  private final List<TypeVariableName> typeVariableNames = new ArrayList<>();
  private final List<ConstructorInfo> constructors = new ArrayList<>();
  private final List<MethodInfo> methodsReturningClassType = new ArrayList<>();

  public ClassToGenerateInfo(TypeElement originalClassName, ClassName generatedClassName,
      boolean isOriginalClassAbstract) {
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
        List<? extends TypeMirror> params = subElement.asType().accept(CONSTRUCTOR_VISITOR, null);
        constructors
            .add(new ConstructorInfo(subElement.getModifiers(), buildParamList(params)));
      }
    }
  }

  /**
   * Get information about methods returning class type of the original class so we can
   * duplicate them in the generated class for chaining purposes
   */
  private void collectMethodsReturningClassType(TypeElement originalClass) {
    for (Element subElement : originalClass.getEnclosedElements()) {
      Set<Modifier> modifiers = subElement.getModifiers();
      if (subElement.getKind() == ElementKind.METHOD
          && ((ExecutableType) subElement.asType()).getReturnType().equals(originalClass.asType())
          && !modifiers.contains(Modifier.PRIVATE)
          && !modifiers.contains(Modifier.STATIC)) {
        List<? extends TypeMirror> params = ((ExecutableType) subElement.asType())
            .getParameterTypes();
        String methodName = subElement.getSimpleName().toString();
        if (params.size() == 1) {
          TypeMirror param = params.get(0);
          ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.get(param),
              methodName).build();
          methodsReturningClassType.add(new MethodInfo(methodName, modifiers,
              Collections.singletonList(parameterSpec)));
        } else {
          methodsReturningClassType.add(new MethodInfo(methodName, modifiers,
              buildParamList(params)));
        }
      }
    }
  }

  private List<ParameterSpec> buildParamList(List<? extends TypeMirror> params) {
    List<ParameterSpec> result = new ArrayList<>();

    // We don't know the name of the variable, just the type. So just use generic parameter names
    int paramCount = 1;
    for (TypeMirror param : params) {
      result.add(ParameterSpec.builder(TypeName.get(param), "param" + paramCount).build());
      paramCount++;
    }
    return result;
  }

  private static final TypeVisitor<List<? extends TypeMirror>, Void> CONSTRUCTOR_VISITOR =
      new SimpleTypeVisitor6<List<? extends TypeMirror>, Void>() {
        public List<? extends TypeMirror> visitExecutable(ExecutableType t, Void v) {
          return t.getParameterTypes();
        }
      };

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

  public List<MethodInfo> getMethodsReturningClassType() {
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

    public ConstructorInfo(Set<Modifier> modifiers, List<ParameterSpec> params) {
      this.modifiers = modifiers;
      this.params = params;
    }
  }

  public static class MethodInfo {
    final String name;
    final Set<Modifier> modifiers;
    final List<ParameterSpec> params;

    public MethodInfo(String name, Set<Modifier> modifiers, List<ParameterSpec> params) {
      this.name = name;
      this.modifiers = modifiers;
      this.params = params;
    }
  }
}
