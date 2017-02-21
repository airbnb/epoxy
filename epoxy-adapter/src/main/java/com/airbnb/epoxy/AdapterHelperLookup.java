package com.airbnb.epoxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Looks up a generated {@link com.airbnb.epoxy.AdapterHelper} implementation for a given adapter.
 * If the adapter has no {@link com.airbnb.epoxy.AutoModel} models then a No-Op implementation will
 * be returned.
 */
class AdapterHelperLookup {
  private static final String GENERATED_HELPER_CLASS_SUFFIX = "_EpoxyHelper";
  private static final Map<Class<?>, Constructor<?>> BINDINGS = new LinkedHashMap<>();
  private static final NoOpAdapterHelper DUMMY_ADAPTER_HELPER = new NoOpAdapterHelper();

  static AdapterHelper getHelperForAdapter(DiffAdapter adapter) {
    Constructor<?> constructor = findConstructorForClass(adapter.getClass());
    if (constructor == null) {
      return DUMMY_ADAPTER_HELPER;
    }

    try {
      return (AdapterHelper) constructor.newInstance();
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Unable to invoke " + constructor, e);
    } catch (InstantiationException e) {
      throw new RuntimeException("Unable to invoke " + constructor, e);
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause instanceof RuntimeException) {
        throw (RuntimeException) cause;
      }
      if (cause instanceof Error) {
        throw (Error) cause;
      }
      throw new RuntimeException("Unable to get Epoxy helper class.", cause);
    }
  }

  private static Constructor<?> findConstructorForClass(Class<?> adapterClass) {
    Constructor<?> bindingCtor = BINDINGS.get(adapterClass);
    if (bindingCtor != null) {
      return bindingCtor;
    }

    String clsName = adapterClass.getName();
    if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
      return null;
    }

    try {
      Class<?> bindingClass = Class.forName(clsName + GENERATED_HELPER_CLASS_SUFFIX);
      //noinspection unchecked
      bindingCtor = bindingClass.getConstructor();
    } catch (ClassNotFoundException e) {
      bindingCtor = findConstructorForClass(adapterClass.getSuperclass());
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Unable to find Epoxy Helper constructor for " + clsName, e);
    }
    BINDINGS.put(adapterClass, bindingCtor);
    return bindingCtor;
  }
}
