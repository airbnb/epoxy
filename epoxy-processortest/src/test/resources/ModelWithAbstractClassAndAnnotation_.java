package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;

import java.lang.CharSequence;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify!
 */
public class ModelWithAbstractClassAndAnnotation_ extends ModelWithAbstractClassAndAnnotation
    implements GeneratedModel<Object> {

  private OnModelBoundListener<ModelWithAbstractClassAndAnnotation_, Object>
      onModelBoundListener_epoxyGeneratedModel;
  private OnModelUnboundListener<ModelWithAbstractClassAndAnnotation_, Object>
      onModelUnboundListener_epoxyGeneratedModel;

  public ModelWithAbstractClassAndAnnotation_() {
    super();
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final Object object) {
  }

  @Override
  public void handlePostBind(final EpoxyViewHolder holder, final Object object) {
    if (onModelBoundListener_epoxyGeneratedModel != null) {
      onModelBoundListener_epoxyGeneratedModel.onModelBound(this, object);
    }
  }

  public ModelWithAbstractClassAndAnnotation_ onBind(
      OnModelBoundListener<ModelWithAbstractClassAndAnnotation_, Object> listener) {
    this.onModelBoundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public void unbind(Object object) {
    super.unbind(object);

    if (onModelUnboundListener_epoxyGeneratedModel != null) {
      onModelUnboundListener_epoxyGeneratedModel.onModelUnbound(this, object);
    }
  }

  public ModelWithAbstractClassAndAnnotation_ onUnbind(
      OnModelUnboundListener<ModelWithAbstractClassAndAnnotation_, Object> listener) {
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  @Override
  public ModelWithAbstractClassAndAnnotation_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithAbstractClassAndAnnotation_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithAbstractClassAndAnnotation_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithAbstractClassAndAnnotation_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelWithAbstractClassAndAnnotation_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithAbstractClassAndAnnotation_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithAbstractClassAndAnnotation_ hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelWithAbstractClassAndAnnotation_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;

    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithAbstractClassAndAnnotation_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithAbstractClassAndAnnotation_ that = (ModelWithAbstractClassAndAnnotation_) o;
    if ((onModelBoundListener_epoxyGeneratedModel == null) != (
        that.onModelBoundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if ((onModelUnboundListener_epoxyGeneratedModel == null) != (
        that.onModelUnboundListener_epoxyGeneratedModel == null)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);

    return result;
  }

  @Override
  public String toString() {
    return "ModelWithAbstractClassAndAnnotation_{" +
        "}" + super.toString();
  }
}