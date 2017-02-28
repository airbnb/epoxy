package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.view.View;
import java.lang.CharSequence;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.List;

/**
 * Generated file. Do not modify! */
public class ModelWithViewClickListener_ extends ModelWithViewClickListener implements ClickableModel {
  private EpoxyViewHolder boundEpoxyViewHolder;

  private Object epoxyModelBoundObject;

  public ModelWithViewClickListener_() {
    super();
  }

  @Override
  public void setViewHolder(EpoxyViewHolder holder) {
    this.boundEpoxyViewHolder = holder;
  }

  @Override
  public void bind(Object object) {
    super.bind(object);
    this.epoxyModelBoundObject = object;
  }

  @Override
  public void bind(Object object, List<Object> payloads) {
    super.bind(object, payloads);
    this.epoxyModelBoundObject = object;
  }

  @Override
  public void unbind(Object object) {
    super.unbind(object);
    this.epoxyModelBoundObject = null;
    this.boundEpoxyViewHolder = null;
  }

  public ModelWithViewClickListener_ clickListener(final OnModelClickListener<ModelWithViewClickListener_, Object> clickListener) {
    if (clickListener == null) {
      this.clickListener = null;
    } else {
      this.clickListener = new View.OnClickListener() {
        public void onClick(View v) {
          // protect from being called when unbound
          if (boundEpoxyViewHolder != null) {
            clickListener.onClick(ModelWithViewClickListener_.this, epoxyModelBoundObject,
                boundEpoxyViewHolder.getAdapterPosition());
          }
        }
        public int hashCode() {
          // Hash the original click listener to avoid changing model state
          return clickListener.hashCode();
        }
      };
    }
    return this;
  }

  public ModelWithViewClickListener_ clickListener(View.OnClickListener clickListener) {
    this.clickListener = clickListener;
    return this;
  }

  public View.OnClickListener clickListener() {
    return clickListener;
  }

  @Override
  public ModelWithViewClickListener_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithViewClickListener_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithViewClickListener_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithViewClickListener_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelWithViewClickListener_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithViewClickListener_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithViewClickListener_ hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelWithViewClickListener_ reset() {
    this.clickListener = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithViewClickListener_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithViewClickListener_ that = (ModelWithViewClickListener_) o;
    if (clickListener != null && that.clickListener == null || clickListener == null && that.clickListener != null) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (clickListener != null ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ModelWithViewClickListener_{" +
        "clickListener=" + clickListener +
        "}" + super.toString();
  }
}