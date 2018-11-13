package com.airbnb.epoxy.sample.views;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.epoxy.ModelProp;
import com.airbnb.epoxy.ModelProp.Option;
import com.airbnb.epoxy.ModelView;
import com.airbnb.epoxy.TextProp;
import com.airbnb.epoxy.sample.R;
import com.airbnb.paris.annotations.Style;
import com.airbnb.paris.annotations.Styleable;

@Styleable // Dynamic styling via the Paris library
@ModelView
public class HeaderView extends LinearLayout {

  private TextView title;
  private TextView caption;

  public HeaderView(Context context) {
    super(context);
    init();
  }

  @Style(isDefault = true)
  static void headerStyle(HeaderViewStyleApplier.StyleBuilder builder) {
    builder.layoutWidth(ViewGroup.LayoutParams.MATCH_PARENT)
        .layoutHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
  }

  private void init() {
    setOrientation(VERTICAL);
    inflate(getContext(), R.layout.view_header, this);
    title = findViewById(R.id.title_text);
    caption = findViewById(R.id.caption_text);
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
