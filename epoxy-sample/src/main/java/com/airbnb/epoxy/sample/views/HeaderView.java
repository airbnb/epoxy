package com.airbnb.epoxy.sample.views;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.epoxy.ModelProp;
import com.airbnb.epoxy.ModelProp.Option;
import com.airbnb.epoxy.ModelView;
import com.airbnb.epoxy.ModelView.Size;
import com.airbnb.epoxy.TextProp;
import com.airbnb.epoxy.sample.R;

import butterknife.BindView;
import butterknife.ButterKnife;

@ModelView(autoLayout = Size.WRAP_WIDTH_WRAP_HEIGHT)
public class HeaderView extends LinearLayout {

  @BindView(R.id.title_text) TextView title;
  @BindView(R.id.caption_text) TextView caption;

  public HeaderView(Context context) {
    super(context);
    init();
  }

//  @Style(isDefault = true)
//  static void headerStyle(HeaderViewStyleApplier.StyleBuilder builder) {
//
//  }

  private void init() {
    setOrientation(VERTICAL);
    inflate(getContext(), R.layout.view_header, this);
    ButterKnife.bind(this);
  }

  @TextProp(defaultRes = R.string.app_name)
  public void setTitle(CharSequence title) {
    this.title.setText(title);
  }

  @ModelProp(options = Option.GenerateStringOverloads)
  public void setCaption(CharSequence caption) {
    this.caption.setText(caption);
  }
}
