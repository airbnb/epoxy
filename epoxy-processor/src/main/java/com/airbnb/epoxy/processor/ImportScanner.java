package com.airbnb.epoxy.processor;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementScanner7;

public class ImportScanner extends ElementScanner7<Void, Void> {

  private Set<String> types = new HashSet<>();

  public Set<String> getImportedTypes() {
    return types;
  }

  @Override
  public Void visitType(TypeElement e, Void p) {
    for (TypeMirror interfaceType : e.getInterfaces()) {
      types.add(interfaceType.toString());
    }

    TypeMirror superclass = e.getSuperclass();
    SynchronizationKt.ensureLoaded(superclass);
    types.add(superclass.toString());
    return super.visitType(e, p);
  }

  @Override
  public Void visitExecutable(ExecutableElement e, Void p) {
    if (e.getReturnType().getKind() == TypeKind.DECLARED) {
      types.add(e.getReturnType().toString());
    }
    return super.visitExecutable(e, p);
  }

  @Override
  public Void visitTypeParameter(TypeParameterElement e, Void p) {
    if (e.asType().getKind() == TypeKind.DECLARED) {
      types.add(e.asType().toString());
    }
    return super.visitTypeParameter(e, p);
  }

  @Override
  public Void visitVariable(VariableElement e, Void p) {
    if (e.asType().getKind() == TypeKind.DECLARED) {
      types.add(e.asType().toString());
    }
    return super.visitVariable(e, p);
  }
}
