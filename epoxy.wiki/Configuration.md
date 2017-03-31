
Use the `@PackageEpoxyConfig` package annotation to specify configuration options for all models in a package.

```java
@PackageEpoxyConfig(
    requireAbstractModels = true,
    requireHashCode = true
)
package com.example.app;

import com.airbnb.epoxy.PackageEpoxyConfig;
```

These configuration options are used by the annotation processor to enforce model requirements or influence how generated models are created.

This is especially useful for larger projects with many models and/or contributors. In these cases having a standardized pattern for models greatly reduces mistakes in creating and using models.

If a config annotation is not found in a package then the configuration from the nearest parent package will be used. If no parent packages declare a configuration then the default values are used. See the `PackageEpoxyConfig` source for up to date documentation on supported configuration options and their defaults.