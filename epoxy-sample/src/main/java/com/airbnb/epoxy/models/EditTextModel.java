package com.airbnb.epoxy.models;

import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;

import com.airbnb.epoxy.EpoxyAttribute;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.airbnb.epoxy.R;
import com.airbnb.epoxy.SampleAdapter.EditTextListener;
import com.airbnb.epoxy.models.EditTextModel.EditTextHolder;

import butterknife.BindView;

/**
 * Created by manas on 10/23/16.
 */

public class EditTextModel extends EpoxyModelWithHolder<EditTextHolder> {

  @EpoxyAttribute(hash = false) EditTextListener editTextListener;
  @EpoxyAttribute @StringRes int hint;

  @Override
  protected EditTextHolder createNewHolder() {
    return new EditTextHolder();
  }

  @Override
  protected int getDefaultLayout() {
    return R.layout.model_edit_text;
  }

  @Override
  public void bind(final EditTextHolder holder) {
    super.bind(holder);
    holder.textInputEditText.setHint(hint);
    holder.textInputEditText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void afterTextChanged(Editable editable) {
        // Dummy text to validate the entered text. Ideally this validation text and message
        // can be set up as fields in the model and set when initialising the model
        String enteredText = editable.toString();
        editTextListener.onTextEntered(enteredText);
        if (!enteredText.equals("epoxy") && !enteredText.isEmpty()) {
          holder.textInputEditText.setError("Enter epoxy");
        }
      }
    });
  }

  @Override
  public int getSpanSize(int totalSpanCount, int position, int itemCount) {
    return totalSpanCount;
  }

  static class EditTextHolder extends BaseEpoxyHolder {
    @BindView(R.id.input_edit_text)
    TextInputEditText textInputEditText;
  }
}
