package com.airbnb.epoxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Looks up a generated {@link ControllerHelper} implementation for a given adapter.
 * If the adapter has no {@link com.airbnb.epoxy.AutoModel} models then a No-Op implementation will
 * be returned.
 */
class ControllerHelperLookup {
  private static final String GENERATED_HELPER_CLASS_SUFFIX = "_EpoxyHelper";
  private static final Map<Class<?>, Constructor<?>> BINDINGS = new LinkedHashMap<>();
  private static final NoOpControllerHelper NO_OP_CONTROLLER_HELPER = new NoOpControllerHelper();

  static ControllerHelper getHelperForController(EpoxyController controller) {
    Constructor<?> constructor = findConstructorForClass(controller.getClass());
    if (constructor == null) {
      return NO_OP_CONTROLLER_HELPER;
    }

    try {
      return (ControllerHelper) constructor.newInstance(controller);
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

  private static Constructor<?> findConstructorForClass(Class<?> controllerClass) {
    Constructor<?> bindingCtor = BINDINGS.get(controllerClass);
    if (bindingCtor != null) {
      return bindingCtor;
    }

    String clsName = controllerClass.getName();
    if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
      return null;
    }

    try {
      Class<?> bindingClass = Class.forName(clsName + GENERATED_HELPER_CLASS_SUFFIX);
      //noinspection unchecked
      bindingCtor = bindingClass.getConstructor(controllerClass);
    } catch (ClassNotFoundException e) {
      bindingCtor = findConstructorForClass(controllerClass.getSuperclass());
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Unable to find Epoxy Helper constructor for " + clsName, e);
    }
    BINDINGS.put(controllerClass, bindingCtor);
    return bindingCtor;
  }
}
