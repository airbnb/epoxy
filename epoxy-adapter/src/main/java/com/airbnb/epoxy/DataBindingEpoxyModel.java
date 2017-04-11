package com.airbnb.epoxy;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.epoxy.DataBindingEpoxyModel.DataBindingHolder;

import java.util.List;

public abstract class DataBindingEpoxyModel extends EpoxyModelWithHolder<DataBindingHolder> {

  @Override
  View buildView(ViewGroup parent) {
    LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
    ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, getViewType(), parent, false);
    View view = binding.getRoot();
    view.setTag(binding);
    return view;
  }

  @Override
  public void bind(DataBindingHolder holder) {
    setDataBindingVariables(holder.dataBinding);
    holder.dataBinding.executePendingBindings();
  }

  @Override
  public void bind(DataBindingHolder holder, EpoxyModel<?> previouslyBoundModel) {
    setDataBindingVariables(holder.dataBinding, previouslyBoundModel);
    holder.dataBinding.executePendingBindings();
  }

  @Override
  public void bind(DataBindingHolder holder, List<Object> payloads) {
    setDataBindingVariables(holder.dataBinding, payloads);
    holder.dataBinding.executePendingBindings();
  }

  protected abstract void setDataBindingVariables(ViewDataBinding binding);

  protected void setDataBindingVariables(ViewDataBinding dataBinding,
      EpoxyModel<?> previouslyBoundModel) {
    setDataBindingVariables(dataBinding);
  }

  protected void setDataBindingVariables(ViewDataBinding dataBinding, List<Object> payloads) {
    setDataBindingVariables(dataBinding);
  }

  @Override
  public void unbind(DataBindingHolder holder) {
    holder.dataBinding.unbind();
  }

  @Override
  protected final DataBindingHolder createNewHolder() {
    return new DataBindingHolder();
  }

  public static class DataBindingHolder extends EpoxyHolder {
    private ViewDataBinding dataBinding;

    @Override
    protected void bindView(View itemView) {
      dataBinding = (ViewDataBinding) itemView.getTag();
    }
  }
}
