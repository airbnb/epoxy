package com.airbnb.epoxy;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View.OnClickListener;

import com.airbnb.paris.styles.Style;

import java.util.List;

public interface ModelProperties {

  @NonNull
  String getId();

  boolean has(@NonNull String propertyName);

  boolean getBoolean(@NonNull String propertyName);

  double getDouble(@NonNull String propertyName);

  @NonNull
  Drawable getDrawable(@NonNull String propertyName);

  int getInt(@NonNull String propertyName);

  @NonNull
  OnClickListener getOnClickListener(@NonNull String propertyName);

  @NonNull
  String getString(@NonNull String propertyName);

  @NonNull
  List<String> getStringList(@NonNull String propertyName);

  /**
   * @return Null to apply the default style.
   */
  @Nullable
  Style getStyle();
}
