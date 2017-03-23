package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.view.View;
import java.lang.CharSequence;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;

/**
 * Generated file. Do not modify! */
public class ModelWithPrivateViewClickListener_ extends ModelWithPrivateViewClickListener implements GeneratedModel<Object> {
  private OnModelBoundListener<ModelWithPrivateViewClickListener_, Object> onModelBoundListener_epoxyGeneratedModel;

  private OnModelUnboundListener<ModelWithPrivateViewClickListener_, Object> onModelUnboundListener_epoxyGeneratedModel;

  private OnModelClickListener<ModelWithPrivateViewClickListener_, Object> clickListener_epoxyGeneratedModel;

  public ModelWithPrivateViewClickListener_() {
    super();
  }

  @Override
  public void addTo(EpoxyController controller) {
    super.addTo(controller);
    addWithDebugValidation(controller);
  }

  @Override
  public void handlePreBind(final EpoxyViewHolder holder, final Object object, int position) {
    validateStateHasNotChangedSinceAdded("The model was changed between being added to the controller and being bound.", position);
    if (clickListener_epoxyGeneratedModel != null) {
      super.setClickListener(new View.OnClickListener() {
              // Save the original click listener so if it gets changed on
              // the generated model this click listener won't be affected
              // if it is still bound to a view.
              private final OnModelClickListener<ModelWithPrivateViewClickListener_, Object> clickListener_epoxyGeneratedModel = ModelWithPrivateViewClickListener_.this.clickListener_epoxyGeneratedModel;
              public void onClick(View v) {
              clickListener_epoxyGeneratedModel.onClick(ModelWithPrivateViewClickListener_.this, object, v,
                  holder.getAdapterPosition());
              }
              public int hashCode() {
                 // Use the hash of the original click listener so we don't change the
                 // value by wrapping it with this anonymous click listener
                 return clickListener_epoxyGeneratedModel.hashCode();
              }
            });
    }
  }

  @Override
  public void handlePostBind(final Object object, int position) {
    if (onModelBoundListener_epoxyGeneratedModel != null) {
      onModelBoundListener_epoxyGeneratedModel.onModelBound(this, object, position);
    }
    validateStateHasNotChangedSinceAdded("The model was changed during the bind call.", position);
  }

  /**
   * Register a listener that will be called when this model is bound to a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public ModelWithPrivateViewClickListener_ onBind(OnModelBoundListener<ModelWithPrivateViewClickListener_, Object> listener) {
    validateMutability();
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

  /**
   * Register a listener that will be called when this model is unbound from a view.
   * <p>
   * The listener will contribute to this model's hashCode state per the {@link
   * com.airbnb.epoxy.EpoxyAttribute.Option#DoNotHash} rules.
   * <p>
   * You may clear the listener by setting a null value, or by calling {@link #reset()} */
  public ModelWithPrivateViewClickListener_ onUnbind(OnModelUnboundListener<ModelWithPrivateViewClickListener_, Object> listener) {
    validateMutability();
    this.onModelUnboundListener_epoxyGeneratedModel = listener;
    return this;
  }

  /**
   * Set a click listener that will provide the parent view, model, and adapter position of the clicked view. This will clear the normal View.OnClickListener if one has been set */
  public ModelWithPrivateViewClickListener_ clickListener(final OnModelClickListener<ModelWithPrivateViewClickListener_, Object> clickListener) {
    validateMutability();
    this.clickListener_epoxyGeneratedModel = clickListener;
    super.setClickListener(new View.OnClickListener() {
            // Save the original click listener so if it gets changed on
            // the generated model this click listener won't be affected
            // if it is still bound to a view.
            private final OnModelClickListener<ModelWithPrivateViewClickListener_, Object> clickListener_epoxyGeneratedModel = ModelWithPrivateViewClickListener_.this.clickListener_epoxyGeneratedModel;
            public void onClick(View v) { }
            
            public int hashCode() {
               // Use the hash of the original click listener so we don't change the
               // value by wrapping it with this anonymous click listener
               return clickListener_epoxyGeneratedModel.hashCode();
            }
          });
    return this;
  }

  public ModelWithPrivateViewClickListener_ clickListener(View.OnClickListener clickListener) {
    validateMutability();
    this.setClickListener(clickListener);
    this.clickListener_epoxyGeneratedModel = null;
    return this;
  }

  public View.OnClickListener clickListener() {
    return getClickListener();
  }

  @Override
  public ModelWithPrivateViewClickListener_ id(long id) {
    super.id(id);
    return this;
  }

  @Override
  public ModelWithPrivateViewClickListener_ id(CharSequence key) {
    super.id(key);
    return this;
  }

  @Override
  public ModelWithPrivateViewClickListener_ id(CharSequence key, long id) {
    super.id(key, id);
    return this;
  }

  @Override
  public ModelWithPrivateViewClickListener_ layout(@LayoutRes int arg0) {
    super.layout(arg0);
    return this;
  }

  @Override
  public ModelWithPrivateViewClickListener_ show() {
    super.show();
    return this;
  }

  @Override
  public ModelWithPrivateViewClickListener_ show(boolean show) {
    super.show(show);
    return this;
  }

  @Override
  public ModelWithPrivateViewClickListener_ hide() {
    super.hide();
    return this;
  }

  @Override
  public ModelWithPrivateViewClickListener_ reset() {
    onModelBoundListener_epoxyGeneratedModel = null;
    onModelUnboundListener_epoxyGeneratedModel = null;
    this.setClickListener(null);
    clickListener_epoxyGeneratedModel = null;
    super.reset();
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ModelWithPrivateViewClickListener_)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ModelWithPrivateViewClickListener_ that = (ModelWithPrivateViewClickListener_) o;
    if ((onModelBoundListener_epoxyGeneratedModel == null) != (that.onModelBoundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if ((onModelUnboundListener_epoxyGeneratedModel == null) != (that.onModelUnboundListener_epoxyGeneratedModel == null)) {
      return false;
    }
    if ((getClickListener() == null) != (that.getClickListener() == null)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (onModelBoundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (onModelUnboundListener_epoxyGeneratedModel != null ? 1 : 0);
    result = 31 * result + (getClickListener() != null ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ModelWithPrivateViewClickListener_{" +
        "clickListener=" + getClickListener() +
        "}" + super.toString();
  }
}