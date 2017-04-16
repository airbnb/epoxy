package com.airbnb.epoxy;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

/**
 * Represents a layout resource used in an Epoxy model via the {@link EpoxyModelClass} annotation
 * <p>
 * Inpspired by Butterknife. https://github.com/JakeWharton/butterknife/pull/613
 */
final class LayoutResource {
  private static final ClassName ANDROID_R = ClassName.get("android", "R");

  final ClassName className;
  final String resourceName;
  final int value;
  final CodeBlock code;
  final boolean qualifed;

  LayoutResource(int value) {
    this.value = value;
    this.code = CodeBlock.of("$L", value);
    this.qualifed = false;
    resourceName = null;
    className = null;
  }

  LayoutResource(ClassName className, String resourceName, int value) {
    this.className = className;
    this.resourceName = resourceName;
    this.value = value;
    this.code = className.topLevelClassName().equals(ANDROID_R)
        ? CodeBlock.of("$L.$N", className, resourceName)
        : CodeBlock.of("$T.$N", className, resourceName);
    this.qualifed = true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof LayoutResource)) {
      return false;
    }

    LayoutResource that = (LayoutResource) o;

    if (value != that.value) {
      return false;
    }
    return code.equals(that.code);
  }

  @Override
  public int hashCode() {
    int result = value;
    result = 31 * result + code.hashCode();
    return result;
  }

  @Override
  public String toString() {
    throw new UnsupportedOperationException("Please use value or code explicitly");
  }
}
