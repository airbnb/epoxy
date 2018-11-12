package com.airbnb.epoxy.databinding;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.airbnb.epoxy.R;
import com.airbnb.epoxy.BR;
@SuppressWarnings("unchecked")
public class ModelWithDataBindingWithoutDonothashBinding extends androidx.databinding.ViewDataBinding  {

  @Nullable
  private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
  @Nullable
  private static final android.util.SparseIntArray sViewsWithIds;
  static {
    sIncludes = null;
    sViewsWithIds = null;
  }
  // views
  @NonNull
  public final android.widget.Button button;
  // variables
  @Nullable
  private java.lang.String mStringValue;
  @Nullable
  private android.view.View.OnClickListener mClickListener;
  // values
  // listeners
  // Inverse Binding Event Handlers

  public ModelWithDataBindingWithoutDonothashBinding(@NonNull androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
    super(bindingComponent, root, 0);
    final Object[] bindings = mapBindings(bindingComponent, root, 1, sIncludes, sViewsWithIds);
    this.button = (android.widget.Button) bindings[0];
    this.button.setTag(null);
    setRootTag(root);
    // listeners
    invalidateAll();
  }

  @Override
  public void invalidateAll() {
    synchronized(this) {
      mDirtyFlags = 0x4L;
    }
    requestRebind();
  }

  @Override
  public boolean hasPendingBindings() {
    synchronized(this) {
      if (mDirtyFlags != 0) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean setVariable(int variableId, @Nullable Object variable)  {
    boolean variableSet = true;
    if (BR.stringValue == variableId) {
      setStringValue((java.lang.String) variable);
    }
    else if (BR.clickListener == variableId) {
      setClickListener((android.view.View.OnClickListener) variable);
    }
    else {
      variableSet = false;
    }
    return variableSet;
  }

  public void setStringValue(@Nullable java.lang.String StringValue) {
    this.mStringValue = StringValue;
    synchronized(this) {
      mDirtyFlags |= 0x1L;
    }
    notifyPropertyChanged(BR.stringValue);
    super.requestRebind();
  }
  @Nullable
  public java.lang.String getStringValue() {
    return mStringValue;
  }
  public void setClickListener(@Nullable android.view.View.OnClickListener ClickListener) {
    this.mClickListener = ClickListener;
    synchronized(this) {
      mDirtyFlags |= 0x2L;
    }
    notifyPropertyChanged(BR.clickListener);
    super.requestRebind();
  }
  @Nullable
  public android.view.View.OnClickListener getClickListener() {
    return mClickListener;
  }

  @Override
  protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
    switch (localFieldId) {
    }
    return false;
  }

  @Override
  protected void executeBindings() {
    long dirtyFlags = 0;
    synchronized(this) {
      dirtyFlags = mDirtyFlags;
      mDirtyFlags = 0;
    }
    java.lang.String stringValue = mStringValue;
    android.view.View.OnClickListener clickListener = mClickListener;

    if ((dirtyFlags & 0x5L) != 0) {
    }
    if ((dirtyFlags & 0x6L) != 0) {
    }
    // batch finished
    if ((dirtyFlags & 0x6L) != 0) {
      // api target 1

      this.button.setOnClickListener(clickListener);
    }
    if ((dirtyFlags & 0x5L) != 0) {
      // api target 1

      androidx.databinding.adapters.TextViewBindingAdapter.setText(this.button, stringValue);
    }
  }
  // Listener Stub Implementations
  // callback impls
  // dirty flag
  private  long mDirtyFlags = 0xffffffffffffffffL;

  @NonNull
  public static ModelWithDataBindingWithoutDonothashBinding inflate(@NonNull android.view.LayoutInflater inflater, @Nullable android.view.ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, androidx.databinding.DataBindingUtil.getDefaultComponent());
  }
  @NonNull
  public static ModelWithDataBindingWithoutDonothashBinding inflate(@NonNull android.view.LayoutInflater inflater, @Nullable android.view.ViewGroup root, boolean attachToRoot, @Nullable androidx.databinding.DataBindingComponent bindingComponent) {
    return androidx.databinding.DataBindingUtil.<ModelWithDataBindingWithoutDonothashBinding>inflate(inflater, com.airbnb.epoxy.R.layout.model_with_data_binding_without_donothash, root, attachToRoot, bindingComponent);
  }
  @NonNull
  public static ModelWithDataBindingWithoutDonothashBinding inflate(@NonNull android.view.LayoutInflater inflater) {
    return inflate(inflater, androidx.databinding.DataBindingUtil.getDefaultComponent());
  }
  @NonNull
  public static ModelWithDataBindingWithoutDonothashBinding inflate(@NonNull android.view.LayoutInflater inflater, @Nullable androidx.databinding.DataBindingComponent bindingComponent) {
    return bind(inflater.inflate(com.airbnb.epoxy.R.layout.model_with_data_binding_without_donothash, null, false), bindingComponent);
  }
  @NonNull
  public static ModelWithDataBindingWithoutDonothashBinding bind(@NonNull android.view.View view) {
    return bind(view, androidx.databinding.DataBindingUtil.getDefaultComponent());
  }
  @NonNull
  public static ModelWithDataBindingWithoutDonothashBinding bind(@NonNull android.view.View view, @Nullable androidx.databinding.DataBindingComponent bindingComponent) {
    if (!"layout/model_with_data_binding_without_donothash_0".equals(view.getTag())) {
      throw new RuntimeException("view tag isn't correct on view:" + view.getTag());
    }
    return new ModelWithDataBindingWithoutDonothashBinding(bindingComponent, view);
  }
    /* flag mapping
        flag 0 (0x1L): stringValue
        flag 1 (0x2L): clickListener
        flag 2 (0x3L): null
    flag mapping end*/
  //end
}
