package com.airbnb.epoxy;

import com.airbnb.epoxy.GeneratedModelInfo.AttributeGroup;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.type.TypeMirror;

import static com.airbnb.epoxy.Utils.isViewClickListenerType;
import static com.airbnb.epoxy.Utils.isViewLongClickListenerType;

abstract class AttributeInfo {

  protected String fieldName;
  protected TypeMirror typeMirror;
  protected String modelName;
  protected String modelPackageName;
  protected boolean useInHash;
  protected boolean ignoreRequireHashCode;
  protected boolean doNotUseInToString;
  protected boolean generateSetter;
  protected List<AnnotationSpec> setterAnnotations = new ArrayList<>();
  protected boolean generateGetter;
  protected List<AnnotationSpec> getterAnnotations = new ArrayList<>();
  protected boolean hasFinalModifier;
  protected boolean packagePrivate;
  protected CodeBlock javaDoc;

  /** If this attribute is in an attribute group this is the name of the group. */
  String groupKey;
  private AttributeGroup attributeGroup;

  /**
   * Track whether there is a setter method for this attribute on a super class so that we can call
   * through to super.
   */
  protected boolean hasSuperSetter;
  // for private fields (Kotlin case)
  protected boolean isPrivate;
  protected String getterMethodName;

  protected String setterMethodName;

  /**
   * True if this attribute is completely generated as a field on the generated model. False if it
   * exists as a user defined attribute in a model super class.
   */
  protected boolean isGenerated;
  /** If {@link #isGenerated} is true, a default value for the field can be set here. */
  final DefaultValue codeToSetDefault = new DefaultValue();

  static class DefaultValue {
    /** An explicitly defined default via the default param in the prop annotation. */
    CodeBlock explicit;
    /**
     * An implicitly assumed default, either via an @Nullable annotation or a primitive's default
     * value. This is overridden if an explicit value is set.
     */
    CodeBlock implicit;

    boolean isPresent() {
      return explicit != null || implicit != null;
    }

    boolean isEmpty() {
      return !isPresent();
    }

    public CodeBlock value() {
      return explicit != null ? explicit : implicit;
    }
  }

  /**
   * If {@link #isGenerated} is true, this represents whether null is a valid value to set on the
   * attribute. If this is true, then the {@link #codeToSetDefault} should be null unless a
   * different default value is explicitly set.
   * <p>
   * This is Boolean to have null represent that nullability was not explicitly set, eg for
   * primitives or legacy attributes that weren't made with nullability support in mind.
   */
  private Boolean isNullable;

  protected void setJavaDocString(String docComment) {
    if (docComment != null && !docComment.trim().isEmpty()) {
      javaDoc = CodeBlock.of(docComment);
    } else {
      javaDoc = null;
    }
  }

  public boolean isNullable() {
    if (!hasSetNullability()) {
      throw new IllegalStateException("Nullability has not been set");
    }

    return isNullable;
  }

  public boolean hasSetNullability() {
    return isNullable != null;
  }

  public void setNullable(boolean nullable) {
    if (isPrimitive()) {
      throw new IllegalStateException("Primitives cannot be nullable");
    }

    isNullable = nullable;
  }

  public boolean isPrimitive() {
    return getTypeName().isPrimitive();
  }

  boolean isRequired() {
    return isGenerated && codeToSetDefault.isEmpty();
  }

  String getFieldName() {
    return fieldName;
  }

  TypeName getTypeName() {
    return TypeName.get(typeMirror);
  }

  public TypeMirror getTypeMirror() {
    return typeMirror;
  }

  boolean useInHash() {
    return useInHash;
  }

  boolean ignoreRequireHashCode() {
    return ignoreRequireHashCode;
  }

  public boolean doNotUseInToString() {
    return doNotUseInToString;
  }

  boolean generateSetter() {
    return generateSetter;
  }

  List<AnnotationSpec> getSetterAnnotations() {
    return setterAnnotations;
  }

  boolean generateGetter() {
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

  String getterCode() {
    return isPrivate ? getterMethodName + "()" : fieldName;
  }

  // Special case to avoid generating recursive getter if field and its getter names are the same
  String superGetterCode() {
    return isPrivate ? String.format("super.%s()", getterMethodName) : fieldName;
  }

  String setterCode() {
    return (isGenerated ? "this." : "super.")
        + (isPrivate ? setterMethodName + "($L)" : fieldName + " = $L");
  }

  String generatedSetterName() {
    return fieldName;
  }

  String generatedGetterName() {
    return fieldName;
  }

  @Override
  public String toString() {
    return "Attribute {"
        + "model='" + modelName + '\''
        + ", name='" + fieldName + '\''
        + ", type=" + getTypeName()
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

    if (!fieldName.equals(that.fieldName)) {
      return false;
    }
    return getTypeName().equals(that.getTypeName());
  }

  @Override
  public int hashCode() {
    int result = fieldName.hashCode();
    result = 31 * result + getTypeName().hashCode();
    return result;
  }

  boolean isViewClickListener() {
    return isViewClickListenerType(getTypeMirror()) || isViewLongClickListenerType(getTypeMirror());
  }

  String getPackageName() {
    return modelPackageName;
  }

  void setAttributeGroup(AttributeGroup group) {
    attributeGroup = group;
  }

  public AttributeGroup getAttributeGroup() {
    return attributeGroup;
  }

  boolean isOverload() {
    return attributeGroup != null && attributeGroup.attributes.size() > 1;
  }
}
