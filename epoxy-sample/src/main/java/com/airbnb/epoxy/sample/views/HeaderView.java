package com.airbnb.epoxy.sample.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.epoxy.ModelProp;
import com.airbnb.epoxy.ModelProp.Option;
import com.airbnb.epoxy.ModelView;
import com.airbnb.epoxy.R;

import butterknife.BindView;
import butterknife.ButterKnife;

@ModelView
public class HeaderView extends LinearLayout {

  @BindView(R.id.title_text) TextView title;
  @BindView(R.id.caption_text) TextView caption;

  public HeaderView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init() {
    setOrientation(VERTICAL);
    inflate(getContext(), R.layout.view_header, this);
    ButterKnife.bind(this);
  }

  @ModelProp(options = Option.GenerateStringOverloads)
  public void setTitle(CharSequence title) {
    this.title.setText(title);
  }

  @ModelProp(options = Option.GenerateStringOverloads)
  public void setCaption(CharSequence caption) {
    this.caption.setText(caption);
  }
}
