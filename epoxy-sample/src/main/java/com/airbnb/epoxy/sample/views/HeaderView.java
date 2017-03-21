package com.airbnb.epoxy.sample.views;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.epoxy.R;

import butterknife.BindView;
import butterknife.ButterKnife;

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

  public void setTitle(@StringRes int title) {
    this.title.setText(title);
  }

  public void setCaption(@StringRes int caption) {
    this.caption.setText(caption);
  }
}
