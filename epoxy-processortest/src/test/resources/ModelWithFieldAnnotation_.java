package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import java.lang.CharSequence;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify!
 */
public class ModelWithFieldAnnotation_ extends ModelWithFieldAnnotation
    implements GeneratedModel<Object> {

  private OnModelBoundListener<ModelWithFieldAnnotation_, Object>
      onModelBoundListener_epoxyGeneratedModel;
  private OnModelUnboundListener<ModelWithFieldAnnotation_, Object>
      onModelUnboundListener_epoxyGeneratedModel;

  public ModelWithFieldAnnotation_() {
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

  public ModelWithFieldAnnotation_ onBind(
      OnModelBoundListener<ModelWithFieldAnnotation_, Object> listener) {
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

  public ModelWithFieldAnnotation_ onUnbind(
      OnModelUnboundListener<ModelWithFieldAnnotation_, Object> listener) {
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  public ModelWithFieldAnnotation_ title(@Nullable String title) {
    this.title = title;
    return this;
  }

  @Nullable
  public String title() {
    return title;
  }

  @Override
  public ModelWithFieldAnnotation_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithFieldAnnotation_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithFieldAnnotation_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithFieldAnnotation_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelWithFieldAnnotation_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithFieldAnnotation_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithFieldAnnotation_ hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelWithFieldAnnotation_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;

    this.title = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithFieldAnnotation_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithFieldAnnotation_ that = (ModelWithFieldAnnotation_) o;
    if ((onModelBoundListener_epoxyGeneratedModel == null) != (
        that.onModelBoundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if ((onModelUnboundListener_epoxyGeneratedModel == null) != (
        that.onModelUnboundListener_epoxyGeneratedModel == null)) {
      return false;
    }

    if (title != null ? !title.equals(that.title) : that.title != null) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);

    result = 31 * result + (title != null ? title.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ModelWithFieldAnnotation_{" +
        "title=" + title +
        "}" + super.toString();
  }
}
