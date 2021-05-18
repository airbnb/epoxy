package com.airbnb.epoxy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.airbnb.epoxy.DataBindingEpoxyModel.DataBindingHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * A version of {@link com.airbnb.epoxy.EpoxyModel} that can be used with databinding. The layout
 * resource used with this model must be a databinding layout. This class will create the
 * databinding object from the layout and call the {@link #setDataBindingVariables} methods when the
 * view needs binding to the model.
 * <p>
 * The easiest way to use this model is to have Epoxy generate a model to do all the binding work
 * for you. To do this, create an abstract subclass of this model, annotate it with {@link
 * EpoxyModelClass}, and pass your layout resource as the layout param. (You must pass the layout
 * this way instead of implementing {@link #getDefaultLayout()}).
 * <p>
 * Then, make a field to represent each of the data variables in your layout and annotate each one
 * with {@link EpoxyAttribute}. The name of each field must match the name of the variable in the
 * layout exactly.
 * <p>
 * Epoxy will generate a subclass of your model at compile time that implements {@link
 * #setDataBindingVariables(ViewDataBinding)} and {@link #setDataBindingVariables(ViewDataBinding,
 * EpoxyModel)} for you. This will do all binding for you, and also only bind variables that change
 * if you update your model (Note: The change optimization only works when used with {@link
 * EpoxyController}).
 */
public abstract class DataBindingEpoxyModel extends EpoxyModelWithHolder<DataBindingHolder> {

  @Override
  public View buildView(@NonNull ViewGroup parent) {
    LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
    ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, getViewType(), parent, false);
    View view = binding.getRoot();
    view.setTag(binding);
    return view;
  }

  @Override
  public void bind(@NonNull DataBindingHolder holder) {
    setDataBindingVariables(holder.dataBinding);
    holder.dataBinding.executePendingBindings();
  }

  @Override
  public void bind(@NonNull DataBindingHolder holder, @NonNull EpoxyModel<?> previouslyBoundModel) {
    setDataBindingVariables(holder.dataBinding, previouslyBoundModel);
    holder.dataBinding.executePendingBindings();
  }

  @Override
  public void bind(@NonNull DataBindingHolder holder, @NonNull List<Object> payloads) {
    setDataBindingVariables(holder.dataBinding, payloads);
    holder.dataBinding.executePendingBindings();
  }

  /**
   * This is called when the model is bound to a view, and the view's variables should be updated
   * with the model's data. {@link ViewDataBinding#executePendingBindings()} is called for you after
   * this method is run.
   * <p>
   * If you leave your class abstract and have a model generated for you via annotations this will
   * be implemented for you. However, you may choose to implement this manually if you like.
   */
  protected abstract void setDataBindingVariables(ViewDataBinding binding);

  /**
   * Similar to {@link #setDataBindingVariables(ViewDataBinding)}, but this method only binds
   * variables that have changed. The changed model comes from {@link #bind(DataBindingHolder,
   * EpoxyModel)}. This will only be called if the model is used in an {@link EpoxyController}
   * <p>
   * If you leave your class abstract and have a model generated for you via annotations this will
   * be implemented for you. However, you may choose to implement this manually if you like.
   */
  protected void setDataBindingVariables(ViewDataBinding dataBinding,
      EpoxyModel<?> previouslyBoundModel) {
    setDataBindingVariables(dataBinding);
  }

  protected void setDataBindingVariables(ViewDataBinding dataBinding, List<Object> payloads) {
    setDataBindingVariables(dataBinding);
  }

  @Override
  public void unbind(@NonNull DataBindingHolder holder) {
    holder.dataBinding.unbind();
  }

  @Override
  protected final DataBindingHolder createNewHolder(@NonNull ViewParent parent) {
    return new DataBindingHolder();
  }

  public static class DataBindingHolder extends EpoxyHolder {
    private ViewDataBinding dataBinding;

    public ViewDataBinding getDataBinding() {
      return dataBinding;
    }

    @Override
    protected void bindView(@NonNull View itemView) {
      dataBinding = (ViewDataBinding) itemView.getTag();
    }
  }
}
