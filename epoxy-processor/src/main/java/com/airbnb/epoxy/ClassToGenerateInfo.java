
package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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

  public ClassToGenerateInfo(TypeElement originalClassName, ClassName generatedClassName, boolean isOriginalClassAbstract) {
    this.originalClassName = ParameterizedTypeName.get(originalClassName.asType());
    this.originalClassNameWithoutType = ClassName.get(originalClassName);

    for (TypeParameterElement typeParameterElement : originalClassName.getTypeParameters()) {
      typeVariableNames.add(TypeVariableName.get(typeParameterElement));
    }

    // Get information about constructors on the original class so we can duplicate
    // them in the generated class and call through to super with the proper parameters
    for (Element subElement : originalClassName.getEnclosedElements()) {
      if (subElement.getKind() == ElementKind.CONSTRUCTOR && !subElement.getModifiers().contains(Modifier.PRIVATE)) {
        List<? extends TypeMirror> params = subElement.asType().accept(constructorVisitor, null);
        constructors.add(new ConstructorInfo(subElement.getModifiers(), buildConstructorParamList(params)));
      }
    }

    if (!typeVariableNames.isEmpty()) {
      TypeVariableName[] typeArguments = typeVariableNames.toArray(new TypeVariableName[typeVariableNames.size()]);
      this.parameterizedClassName = ParameterizedTypeName.get(generatedClassName, typeArguments);
    } else {
      this.parameterizedClassName = generatedClassName;
    }

    this.generatedClassName = generatedClassName;
    this.isOriginalClassAbstract = isOriginalClassAbstract;
  }

  private List<ParameterSpec> buildConstructorParamList(List<? extends TypeMirror> params) {
    List<ParameterSpec> result = new ArrayList<>();

    // We don't know the name of the variable, just the type. So just use generic parameter names
    int paramCount = 1;
    for (TypeMirror param : params) {
      result.add(ParameterSpec.builder(TypeName.get(param), "param" + paramCount).build());
      paramCount++;
    }
    return result;
  }

  private static final TypeVisitor<List<? extends TypeMirror>, Void> constructorVisitor = new SimpleTypeVisitor6<List<? extends TypeMirror>, Void>() {
    public List<? extends TypeMirror> visitExecutable(ExecutableType t, Void v) {
      return t.getParameterTypes();
    }
  };

  public void addAttribute(AttributeInfo attributeInfo) {
    this.attributeInfo.add(attributeInfo);
  }

  public void addAttributes(Collection<AttributeInfo> attributeInfo) {
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

  public static class ConstructorInfo {
    final Set<Modifier> modifiers;
    final List<ParameterSpec> params;

    public ConstructorInfo(Set<Modifier> modifiers, List<ParameterSpec> params) {
      this.modifiers = modifiers;
      this.params = params;
    }
  }
}
