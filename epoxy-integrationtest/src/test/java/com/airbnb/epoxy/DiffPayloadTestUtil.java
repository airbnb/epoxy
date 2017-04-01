package com.airbnb.epoxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class DiffPayloadTestUtil {

  static DiffPayload diffPayloadWithModels(EpoxyModel<?>... models) {
    List<EpoxyModel<?>> epoxyModels = Arrays.asList(models);
    return new DiffPayload(epoxyModels);
  }

  static List<Object> payloadsWithDiffPayloads(DiffPayload... diffPayloads) {
    List<DiffPayload> payloads = Arrays.asList(diffPayloads);
    return new ArrayList<Object>(payloads);
  }

  static List<Object> payloadsWithChangedModels(EpoxyModel<?>... models) {
    return payloadsWithDiffPayloads(diffPayloadWithModels(models));
  }
}
