# Epoxy

Epoxy is an Android library for building complex screens in a RecyclerView. Models are automatically generated from custom views, databinding layouts, or Litho components via annotation processing. These models are then used in an EpoxyController to declare what items to show in the RecyclerView.

This abstracts the boilerplate of view holders, diffing items and binding payload changes, item types, item ids, span counts, and more, in order to simplify building screens with multiple view types. Additionally, Epoxy adds support for saving view state and automatic diffing of item changes.

[We developed Epoxy at Airbnb](https://medium.com/airbnb-engineering/epoxy-airbnbs-view-architecture-on-android-c3e1af150394#.xv4ymrtmk) to simplify the process of working with RecyclerViews, and to add the missing functionality we needed. We now use Epoxy for most of the main screens in our app and it has improved our developer experience greatly.

* [Installation](#installation)
* [Basic Usage](#basic-usage)
* [Documentation](#documentation)
* [Min SDK](#min-sdk)
* [Contributing](#contributing)
* [Sample App](/epoxy-sample)

## Installation

Gradle is the only supported build configuration, so just add the dependency to your project `build.gradle` file:

```groovy
dependencies {
  compile 'com.airbnb.android:epoxy:2.2.0'
  // Add the annotation processor if you are using Epoxy's annotations (recommended)
  annotationProcessor 'com.airbnb.android:epoxy-processor:2.2.0' 
}
```

#### Kotlin
If you are using Kotlin you should also add
```
apply plugin: 'kotlin-kapt'

kapt {
    correctErrorTypes = true
}
```

so that `AutoModel` annotations work properly. More information [here](https://github.com/airbnb/epoxy/wiki/Epoxy-Controller#usage-with-kotlin)

## Basic Usage
There are two main components of Epoxy:

1. The `EpoxyModel`s that describe how your views should be displayed in the RecyclerView.
2. The `EpoxyController` where the models are used to describe what items to show and with what data.

### Creating Models
There are a few ways to create models, depending on whether you prefer to use custom views, databinding, or other approaches.

#### From Custom Views
You can easily generate an EpoxyModel from your custom views by using the `@ViewModel` annotation on the class. Then, add a `ModelProp` annotation on each setter method to mark it as a property for the model.

```java
@ModelView(defaultLayout = R.layout.view_holder_header)
public class HeaderView extends LinearLayout {

  ... // Initialization omitted

  @ModelProp(options = Option.GenerateStringOverloads)
  public void setTitle(CharSequence text) {
    titleView.setText(text);
  }

  @ModelProp(options = Option.GenerateStringOverloads)
  public void setDescription(CharSequence text) {
    captionView.setText(text);
  }
}
```

The layout file (`R.layout.view_holder_header` in this case) simply describes the layout params and styling for how the view should be inflated.
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.airbnb.epoxy.sample.views.HeaderView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:minHeight="120dp" />
```

#### From DataBinding

If you use Android DataBinding you can simply set up your xml layouts like normal:

```xml
<layout xmlns:android="http://schemas.android.com/apk/res/android">

    <data>
        <variable
            name="url"
            type="String" />

    </data>

    <Button
        android:layout_width="120dp"
        android:layout_height="40dp"
        android:imageUrl="@{url}" />
</layout>
```

Then, create a `package-info.java` class in the package you want the models generated and add an `EpoxyDataBindingLayouts` annotation to declare all of the databinding layouts that should be used to create models.

```java
@EpoxyDataBindingLayouts({R.layout.photo, ... // other layouts })
package com.airbnb.epoxy.sample;

import com.airbnb.epoxy.EpoxyDataBindingLayouts;
import com.airbnb.epoxy.R;
```

Epoxy generates a model that includes all the variables for that layout.

#### Other ways
You can also create EpoxyModel's from Litho components, viewholders, or completely manually. See the wiki sidebar for more information on these approaches in depth.

### Using your models in a controller

A controller defines what items should be shown in the RecyclerView, by adding the corresponding models in the desired order.

 The controller's `buildModels` method declares the current view, and is called whenever the data backing the view changes. Epoxy tracks changes in the models and automatically binds and updates views.

As an example, our `PhotoController` shows a header, a list of photos, and a loader (if more photos are being loaded). The controller's `setData(photos, loadingMore)` method is called whenever photos are loaded, which triggers a call to `buildModels` so models representing the state of the new data can be built.

```java
public class PhotoController extends Typed2EpoxyController<List<Photo>, Boolean> {
    @AutoModel HeaderModel_ headerModel;
    @AutoModel LoaderModel_ loaderModel;

    @Override
    protected void buildModels(List<Photo> photos, Boolean loadingMore) {
      headerModel
          .title("My Photos")
          .description("My album description!")
          .addTo(this);

      for (Photo photo : photos) {
        new PhotoModel()
           .id(photo.id())
           .url(photo.url())
           .addTo(this);
      }

      loaderModel
          .addIf(loadingMore, this);
    }
  }
```

And that's it! The controller's declarative style makes it very easy to visualize what the RecyclerView will look like, even when many different view types or items are used. Epoxy handles everything else. If a view only partially changes, such as the description, only that new value is set on the view, so the system is very efficient

Epoxy handles much more than these basics, and is highly configurable. See the wiki for in depth documentation.

## Documentation
See examples and browse complete documentation at the [Epoxy Wiki](https://github.com/airbnb/epoxy/wiki)

If you still have questions, feel free to create a new issue.

## Min SDK
We support a minimum SDK of 14. However, Epoxy is based on the v7 support libraries so it should work with lower versions if you care to override the min sdk level in the manifest.

If you are using the [optional Litho integration](https://github.com/airbnb/epoxy/wiki/Litho-Support) then the min SDK is 15  due to Litho's SDK requirement.

## Contributing
Pull requests are welcome! We'd love help improving this library. Feel free to browse through open issues to look for things that need work. If you have a feature request or bug, please open a new issue so we can track it.

## License

```
Copyright 2016 Airbnb, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
