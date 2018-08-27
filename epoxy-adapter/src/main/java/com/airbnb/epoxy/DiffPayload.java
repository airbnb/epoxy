package com.airbnb.epoxy;

import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.collection.LongSparseArray;

/**
 * A helper class for tracking changed models found by the {@link com.airbnb.epoxy.DiffHelper} to
 * be included as a payload in the
 * {@link androidx.recyclerview.widget.RecyclerView.Adapter#notifyItemChanged(int, Object)}
 * call.
 */
public class DiffPayload {
  private final EpoxyModel<?> singleModel;
  private final LongSparseArray<EpoxyModel<?>> modelsById;

  DiffPayload(List<? extends EpoxyModel<?>> models) {
    if (models.isEmpty()) {
      throw new IllegalStateException("Models must not be empty");
    }

    int modelCount = models.size();

    if (modelCount == 1) {
      // Optimize for the common case of only one model changed.
      singleModel = models.get(0);
      modelsById = null;
    } else {
      singleModel = null;
      modelsById = new LongSparseArray<>(modelCount);
      for (EpoxyModel<?> model : models) {
        modelsById.put(model.id(), model);
      }
    }
  }

  public DiffPayload(EpoxyModel<?> changedItem) {
    this(Collections.singletonList(changedItem));
  }

  /**
   * Looks through the payloads list and returns the first model found with the given model id. This
   * assumes that the payloads list will only contain objects of type {@link DiffPayload}, and will
   * throw if an unexpected type is found.
   */
  @Nullable
  public static EpoxyModel<?> getModelFromPayload(List<Object> payloads, long modelId) {
    if (payloads.isEmpty()) {
      return null;
    }

    for (Object payload : payloads) {
      DiffPayload diffPayload = (DiffPayload) payload;

      if (diffPayload.singleModel != null) {
        if (diffPayload.singleModel.id() == modelId) {
          return diffPayload.singleModel;
        }
      } else {
        EpoxyModel<?> modelForId = diffPayload.modelsById.get(modelId);
        if (modelForId != null) {
          return modelForId;
        }
      }
    }

    return null;
  }

  @VisibleForTesting
  boolean equalsForTesting(DiffPayload that) {
    if (singleModel != null) {
      return that.singleModel == singleModel;
    }

    int thisSize = modelsById.size();
    int thatSize = that.modelsById.size();

    if (thisSize != thatSize) {
      return false;
    }

    for (int i = 0; i < thisSize; i++) {
      long thisKey = modelsById.keyAt(i);
      long thatKey = that.modelsById.keyAt(i);

      if (thisKey != thatKey) {
        return false;
      }

      EpoxyModel<?> thisModel = modelsById.valueAt(i);
      EpoxyModel<?> thatModel = that.modelsById.valueAt(i);
      if (thisModel != thatModel) {
        return false;
      }
    }

    return true;
  }
}
