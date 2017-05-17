package com.airbnb.epoxy;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;

import static com.airbnb.epoxy.Utils.isFieldPackagePrivate;

class ControllerModelField {

  String fieldName;
  TypeName typeName;
  boolean packagePrivate;

  ControllerModelField(Element element) {
    fieldName = element.getSimpleName().toString();
    typeName = TypeName.get(element.asType());
    this.packagePrivate = isFieldPackagePrivate(element);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ControllerModelField that = (ControllerModelField) o;

    if (packagePrivate != that.packagePrivate) {
      return false;
    }
    if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null) {
      return false;
    }
    return typeName != null ? typeName.equals(that.typeName) : that.typeName == null;
  }

  @Override
  public int hashCode() {
    int result = fieldName != null ? fieldName.hashCode() : 0;
    result = 31 * result + (typeName != null ? typeName.hashCode() : 0);
    result = 31 * result + (packagePrivate ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ControllerModelField{"
        + "fieldName='" + fieldName + '\''
        + ", typeName=" + typeName
        + ", packagePrivate=" + packagePrivate
        + '}';
  }
}
