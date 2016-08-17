/*
 * Copyright (C) 2016 Airbnb, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.airbnb.epoxy;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class EpoxyViewHolder extends RecyclerView.ViewHolder {
  @SuppressWarnings("rawtypes") private EpoxyModel epoxyModel;
  private List<Object> payloads;

  public EpoxyViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
    super(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
  }

  public void bind(@SuppressWarnings("rawtypes") EpoxyModel model, List<Object> payloads) {
    this.payloads = payloads;

    if (payloads.isEmpty()) {
      // noinspection unchecked
      model.bind(itemView);
    } else {
      // noinspection unchecked
      model.bind(itemView, payloads);
    }

    epoxyModel = model;
  }

  public void unbind() {
    assertBound();
    // noinspection unchecked
    epoxyModel.unbind(itemView);
    epoxyModel = null;
    payloads = null;
  }

  public List<Object> getPayloads() {
    assertBound();
    return payloads;
  }

  public EpoxyModel<?> getModel() {
    assertBound();
    return epoxyModel;
  }

  private void assertBound() {
    if (epoxyModel == null) {
      throw new IllegalStateException("This holder is not currently bound.");
    }
  }

  @Override
  public String toString() {
    return "EpoxyViewHolder{" +
        "epoxyModel=" + epoxyModel +
        ", view=" + itemView +
        ", super=" + super.toString() +
        '}';
  }
}
