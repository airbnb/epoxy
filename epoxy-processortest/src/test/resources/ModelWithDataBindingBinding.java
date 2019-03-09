package com.airbnb.epoxy.databinding;
import com.airbnb.epoxy.R;
import com.airbnb.epoxy.BR;
import android.view.View;
public class ModelWithDataBindingBinding extends androidx.databinding.ViewDataBinding  {

  private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
  private static final android.util.SparseIntArray sViewsWithIds;
  static {
    sIncludes = null;
    sViewsWithIds = null;
  }
  // views
  public final android.widget.Button button;
  // variables
  private java.lang.String mStringValue;
  // values
  // listeners
  // Inverse Binding Event Handlers

  public ModelWithDataBindingBinding(androidx.databinding.DataBindingComponent bindingComponent, View root) {
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
      mDirtyFlags = 0x2L;
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

  public boolean setVariable(int variableId, Object variable) {
    switch(variableId) {
      case BR.stringValue :
        setStringValue((java.lang.String) variable);
        return true;
    }
    return false;
  }

  public void setStringValue(java.lang.String StringValue) {
    this.mStringValue = StringValue;
    synchronized(this) {
      mDirtyFlags |= 0x1L;
    }
    notifyPropertyChanged(BR.stringValue);
    super.requestRebind();
  }
  public java.lang.String getStringValue() {
    return mStringValue;
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

    if ((dirtyFlags & 0x3L) != 0) {
    }
    // batch finished
    if ((dirtyFlags & 0x3L) != 0) {
      // api target 1

      androidx.databinding.adapters.TextViewBindingAdapter.setText(this.button, stringValue);
    }
  }
  // Listener Stub Implementations
  // callback impls
  // dirty flag
  private  long mDirtyFlags = 0xffffffffffffffffL;

  public static ModelWithDataBindingBinding inflate(android.view.LayoutInflater inflater, android.view.ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, androidx.databinding.DataBindingUtil.getDefaultComponent());
  }
  public static ModelWithDataBindingBinding inflate(android.view.LayoutInflater inflater, android.view.ViewGroup root, boolean attachToRoot, androidx.databinding.DataBindingComponent bindingComponent) {
    return androidx.databinding.DataBindingUtil.<ModelWithDataBindingBinding>inflate(inflater, com.airbnb.epoxy.R.layout.model_with_data_binding, root, attachToRoot, bindingComponent);
  }
  public static ModelWithDataBindingBinding inflate(android.view.LayoutInflater inflater) {
    return inflate(inflater, androidx.databinding.DataBindingUtil.getDefaultComponent());
  }
  public static ModelWithDataBindingBinding inflate(android.view.LayoutInflater inflater, androidx.databinding.DataBindingComponent bindingComponent) {
    return bind(inflater.inflate(com.airbnb.epoxy.R.layout.model_with_data_binding, null, false), bindingComponent);
  }
  public static ModelWithDataBindingBinding bind(android.view.View view) {
    return bind(view, androidx.databinding.DataBindingUtil.getDefaultComponent());
  }
  public static ModelWithDataBindingBinding bind(android.view.View view, androidx.databinding.DataBindingComponent bindingComponent) {
    if (!"layout/model_with_data_binding_0".equals(view.getTag())) {
      throw new RuntimeException("view tag isn't correct on view:" + view.getTag());
    }
    return new ModelWithDataBindingBinding(bindingComponent, view);
  }
    /* flag mapping
        flag 0 (0x1L): stringValue
        flag 1 (0x2L): null
    flag mapping end*/
  //end
}
