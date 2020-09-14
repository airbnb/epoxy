[![Build Status](https://travis-ci.com/airbnb/epoxy.svg?branch=master)](https://travis-ci.com/github/airbnb/epoxy)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.airbnb.android/epoxy/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.airbnb.android/epoxy)
[![GitHub license](https://img.shields.io/github/license/airbnb/epoxy)](https://github.com/airbnb/epoxy/blob/master/LICENSE)
![GitHub contributors](https://img.shields.io/github/contributors/airbnb/epoxy)

# Epoxy

Epoxy is an Android library for building complex screens in a RecyclerView. Models are automatically generated from custom views or databinding layouts via annotation processing. These models are then used in an EpoxyController to declare what items to show in the RecyclerView.

This abstracts the boilerplate of view holders, diffing items and binding payload changes, item types, item ids, span counts, and more, in order to simplify building screens with multiple view types. Additionally, Epoxy adds support for saving view state and automatic diffing of item changes.

[We developed Epoxy at Airbnb](https://medium.com/airbnb-engineering/epoxy-airbnbs-view-architecture-on-android-c3e1af150394#.xv4ymrtmk) to simplify the process of working with RecyclerViews, and to add the missing functionality we needed. We now use Epoxy for most of the main screens in our app and it has improved our developer experience greatly.

* [Installation](#installation)
* [Basic Usage](#basic-usage)
* [Documentation](#documentation)
* [Min SDK](#min-sdk)
* [Contributing](#contributing)
* [Sample App](https://github.com/airbnb/epoxy/wiki/Sample-App)

## Installation

Gradle is the only supported build configuration, so just add the dependency to your project `build.gradle` file:

```groovy
dependencies {
  implementation "com.airbnb.android:epoxy:$epoxyVersion"
  // Add the annotation processor if you are using Epoxy's annotations (recommended)
  annotationProcessor "com.airbnb.android:epoxy-processor:$epoxyVersion"
}
```

Replace the variable `$epoxyVersion` with the latest version : [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.airbnb.android/epoxy/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.airbnb.android/epoxy)

See the [releases page](https://github.com/airbnb/epoxy/releases) for up to date release versions and details

#### Kotlin
If you are using Kotlin you should also add
```
apply plugin: 'kotlin-kapt'

kapt {
    correctErrorTypes = true
}
```

so that `AutoModel` annotations work properly. More information [here](https://github.com/airbnb/epoxy/wiki/Epoxy-Controller#usage-with-kotlin)

Also, make sure to use `kapt` instead of `annotationProcessor` in your dependencies in the `build.gradle` file.

## Library Projects
If you are using layout resources in Epoxy annotations then for library projects add [Butterknife's gradle plugin](https://github.com/JakeWharton/butterknife#library-projects) to your `buildscript`.

```yaml
buildscript {
  repositories {
    mavenCentral()
   }
  dependencies {
    classpath 'com.jakewharton:butterknife-gradle-plugin:10.1.0'
  }
}
```

and then apply it in your module:
```yaml
apply plugin: 'com.android.library'
apply plugin: 'com.jakewharton.butterknife'
```

Now make sure you use R2 instead of R inside all Epoxy annotations.
```java
@ModelView(defaultLayout = R2.layout.view_holder_header)
public class HeaderView extends LinearLayout {
   ....
}
```

This is not necessary if you don't use resources as annotation parameters, such as with [custom view models](https://github.com/airbnb/epoxy/wiki/Generating-Models-from-View-Annotations).

## Basic Usage
There are two main components of Epoxy:

1. The `EpoxyModel`s that describe how your views should be displayed in the RecyclerView.
2. The `EpoxyController` where the models are used to describe what items to show and with what data.

### Creating Models
Epoxy generates models for you based on your view or layout. Generated model classes are suffixed with an underscore (`_`) are used directly in your EpoxyController classes.

#### From Custom Views
Add the `@ModelView` annotation on a view class. Then, add a "prop" annotation on each setter method to mark it as a property for the model.

```java
@ModelView(autoLayout = Size.MATCH_WIDTH_WRAP_HEIGHT)
public class HeaderView extends LinearLayout {

  ... // Initialization omitted

  @TextProp
  public void setTitle(CharSequence text) {
    titleView.setText(text);
  }
}
```

A `HeaderViewModel_` is then generated in the same package.

[More Details](https://github.com/airbnb/epoxy/wiki/Generating-Models-from-View-Annotations)

#### From DataBinding

If you use Android DataBinding you can simply set up your xml layouts like normal:

```xml
<layout xmlns:android="http://schemas.android.com/apk/res/android">
    <data>
        <variable name="title" type="String" />
    </data>

    <TextView
        android:layout_width="120dp"
        android:layout_height="40dp"
        android:text="@{title}" />
</layout>
```

Then, create an interface or class in any package and add an `EpoxyDataBindingLayouts` annotation to declare your databinding layouts.

```java
package com.airbnb.epoxy.sample;

import com.airbnb.epoxy.EpoxyDataBindingLayouts;

@EpoxyDataBindingLayouts({R.layout.header_view, ... // other layouts })
interface EpoxyConfig {}
```

From this layout name Epoxy generates a `HeaderViewBindingModel_`.

[More Details](https://github.com/airbnb/epoxy/wiki/Data-Binding-Support)

#### From ViewHolders
If you use xml layouts without databinding you can create a model class to do the  binding.

```java
@EpoxyModelClass(layout = R.layout.header_view)
public abstract class HeaderModel extends EpoxyModelWithHolder<Holder> {
  @EpoxyAttribute String title;

  @Override
  public void bind(Holder holder) {
    holder.header.setText(title);
  }

  static class Holder extends BaseEpoxyHolder {
    @BindView(R.id.text) TextView header;
  }
}
```

A `HeaderModel_` class is generated that subclasses HeaderModel and implements the model details.

[More Details](https://github.com/airbnb/epoxy/wiki/ViewHolder-Models)

### Using your models in a controller

A controller defines what items should be shown in the RecyclerView, by adding the corresponding models in the desired order.

 The controller's `buildModels` method declares which items to show. You are responsible for calling `requestModelBuild` whenever your data changes, which triggers `buildModels` to run again. Epoxy tracks changes in the models and automatically binds and updates views.

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

#### Or with Kotlin
An extension function is generated for each model so we can write this:
```kotlin
class PhotoController : Typed2EpoxyController<List<Photo>, Boolean>() {

    override fun buildModels(photos: List<Photo>, loadingMore: Boolean) {
        header {
            id("header")
            title("My Photos")
            description("My album description!")
        }

        photos.forEach {
            photoView {
                id(it.id())
                url(it.url())
            }
        }

        if (loadingMore) loaderView { id("loader") }
    }
}
```

### Integrating with RecyclerView

Get the backing adapter off the EpoxyController to set up your RecyclerView:
```java
MyController controller = new MyController();
recyclerView.setAdapter(controller.getAdapter());

// Request a model build whenever your data changes
controller.requestModelBuild();

// Or if you are using a TypedEpoxyController
controller.setData(myData);
```

If you are using the [EpoxyRecyclerView](https://github.com/airbnb/epoxy/wiki/EpoxyRecyclerView) integration is easier.

```java
epoxyRecyclerView.setControllerAndBuildModels(new MyController());

// Request a model build on the recyclerview when data changes
epoxyRecyclerView.requestModelBuild();
```

#### Kotlin
Or use [Kotlin Extensions](https://github.com/airbnb/epoxy/wiki/EpoxyRecyclerView#kotlin-extensions) to simplify further and remove the need for a controller class.
```kotlin
epoxyRecyclerView.withModels {
        header {
            id("header")
            title("My Photos")
            description("My album description!")
        }

        photos.forEach {
            photoView {
                id(it.id())
                url(it.url())
            }
        }

        if (loadingMore) loaderView { id("loader") }
    }
}
```


### More Reading
And that's it! The controller's declarative style makes it very easy to visualize what the RecyclerView will look like, even when many different view types or items are used. Epoxy handles everything else. If a view only partially changes, such as the description, only that new value is set on the view, so the system is very efficient

Epoxy handles much more than these basics, and is highly configurable. See [the wiki](https://github.com/airbnb/epoxy/wiki) for in depth documentation.

## Documentation
See examples and browse complete documentation at the [Epoxy Wiki](https://github.com/airbnb/epoxy/wiki)

If you still have questions, feel free to create a new issue.

## Min SDK
We support a minimum SDK of 14. However, Epoxy is based on the v7 support libraries so it should work with lower versions if you care to override the min sdk level in the manifest.

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
