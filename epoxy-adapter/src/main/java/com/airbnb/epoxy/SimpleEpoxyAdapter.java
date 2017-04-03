package com.airbnb.epoxy;

import java.util.Collection;
import java.util.List;

/**
 * A non-abstract version of {@link com.airbnb.epoxy.EpoxyAdapter} that exposes all methods and
 * models as public. Use this if you don't want to create your own adapter subclass and instead want
 * to modify the adapter from elsewhere, such as from an activity.
 */
public class SimpleEpoxyAdapter extends EpoxyAdapter {

  public List<EpoxyModel<?>> getModels() {
    return models;
  }

  @Override
  public void enableDiffing() {
    super.enableDiffing();
  }

  @Override
  public void notifyModelsChanged() {
    super.notifyModelsChanged();
  }

  @Override
  public BoundViewHolders getBoundViewHolders() {
    return super.getBoundViewHolders();
  }

  @Override
  public void notifyModelChanged(EpoxyModel<?> model) {
    super.notifyModelChanged(model);
  }

  @Override
  public void addModels(EpoxyModel<?>... modelsToAdd) {
    super.addModels(modelsToAdd);
  }

  @Override
  public void addModels(Collection<? extends EpoxyModel<?>> modelsToAdd) {
    super.addModels(modelsToAdd);
  }

  @Override
  public void insertModelBefore(EpoxyModel<?> modelToInsert, EpoxyModel<?> modelToInsertBefore) {
    super.insertModelBefore(modelToInsert, modelToInsertBefore);
  }

  @Override
  public void insertModelAfter(EpoxyModel<?> modelToInsert, EpoxyModel<?> modelToInsertAfter) {
    super.insertModelAfter(modelToInsert, modelToInsertAfter);
  }

  @Override
  public void removeModel(EpoxyModel<?> model) {
    super.removeModel(model);
  }

  @Override
  public void removeAllModels() {
    super.removeAllModels();
  }

  @Override
  public void removeAllAfterModel(EpoxyModel<?> model) {
    super.removeAllAfterModel(model);
  }

  @Override
  public void showModel(EpoxyModel<?> model, boolean show) {
    super.showModel(model, show);
  }

  @Override
  public void showModel(EpoxyModel<?> model) {
    super.showModel(model);
  }

  @Override
  public void showModels(EpoxyModel<?>... models) {
    super.showModels(models);
  }

  @Override
  public void showModels(boolean show, EpoxyModel<?>... models) {
    super.showModels(show, models);
  }

  @Override
  public void showModels(Iterable<EpoxyModel<?>> epoxyModels) {
    super.showModels(epoxyModels);
  }

  @Override
  public void showModels(Iterable<EpoxyModel<?>> epoxyModels, boolean show) {
    super.showModels(epoxyModels, show);
  }

  @Override
  public void hideModel(EpoxyModel<?> model) {
    super.hideModel(model);
  }

  @Override
  public void hideModels(Iterable<EpoxyModel<?>> epoxyModels) {
    super.hideModels(epoxyModels);
  }

  @Override
  public void hideModels(EpoxyModel<?>... models) {
    super.hideModels(models);
  }

  @Override
  public void hideAllAfterModel(EpoxyModel<?> model) {
    super.hideAllAfterModel(model);
  }

  @Override
  public List<EpoxyModel<?>> getAllModelsAfter(EpoxyModel<?> model) {
    return super.getAllModelsAfter(model);
  }

  @Override
  public int getModelPosition(EpoxyModel<?> model) {
    return super.getModelPosition(model);
  }
}
