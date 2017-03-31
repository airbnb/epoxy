There are several configuration options available to control the behavior of Epoxy's annotation processor.

## Available Options

1. **Requiring abstract models** - If this is enabled then any Epoxy model classes that contain Epoxy annotations are required to be abstract, otherwise an error will be thrown at compile time. Having abstract models is useful to prevent accidental usage of the base model class instead of the generated class. This is set to false by default.
2. **Requiring Epoxy model attribute fields to implement hashCode** - If this is enabled then the annotation processor will inspect every field annotated with `@EpoxyAttribute` and ensure that the type of that field implements hashCode. If not an error will be thrown at compile time. It is necessary for attribute types to implement hashCode in order for the model's state to be correctly determined in order for diffing to work. Enabling this helps catch cases where you accidentally use a type without a hashCode implementation. It is disabled by default.
3. **Validating usage of generated Epoxy models** - If this is enabled, generated Epoxy models will have validations added to ensure that the model is used correctly with EpoxyControllers. This includes making sure the model isn't added to a controller multiple times, and that the model isn't changed after it is added. If these conditions are violated then a runtime exception is thrown. This forces models to be treated as immutable once they are added to a controller, which greatly helps to encourage proper usage of the EpoxyController and prevents unexpected view state from models changing after they are diffed. This is enabled by default, but you may like to disable it in production to prevent the overhead of the runtime validations.

## Controlling The Options
### In gradle
All of these options can be set as annotation processor options in your build.gradle file.


```groovy
project.android.buildTypes.all { buildType ->
  buildType.javaCompileOptions.annotationProcessorOptions.arguments =
      [
          // Validation is disabled in production to remove the runtime overhead
          validateEpoxyModelUsage     : String.valueOf(buildType.name == 'debug'),
          requireHashCodeInEpoxyModels: "true",
          requireAbstractEpoxyModels  : "true"
      ]
}

```

Using these options is especially useful for larger projects with many models and/or contributors. In these cases having a standardized pattern for models greatly reduces mistakes in creating and using models.

## In Java
Alternatively, use the `@PackageEpoxyConfig` package annotation to specify configuration options for models on a per package basis. Create a `package-info.java` file in the package that you want to toggle settings for. You cannot change the `validateEpoxyModelUsage` option this way.

```java
@PackageEpoxyConfig(
    requireAbstractModels = true,
    requireHashCode = true
)
package com.example.app;

import com.airbnb.epoxy.PackageEpoxyConfig;
```

If a config annotation is not found in a package then the configuration from the nearest parent package will be used. If no parent packages declare a configuration then the annotation processor options set in the build.gradle file are used. If those are not set then default values are used.