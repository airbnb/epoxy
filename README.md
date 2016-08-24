# Epoxy

Epoxy is an Android library for building complex screens in a RecyclerView. It abstracts the boilerplate of view holders, item types, item ids, span counts, and more, in order to simplify building screens with multiple view types. Additionally, Epoxy adds support for saving view state and automatic diffing of item changes.

We developed Epoxy at Airbnb to simplify the process of working with RecyclerViews, and to add the missing functionality we needed. We now use Epoxy for most of the main screens in our app and it has improved our developer experience greatly.

* [Installation](#installation)
* [Basic Usage](#basic-usage)
* [Epoxy Models](#epoxy-models)
* [Modifying the Models List](#models-list)
* [Automatic Diffing](#diffing)
* [Binding Models](#binding-models)
* [Model IDs](#model-ids)
* [Specifying Layouts](#specifying-layouts)
* [Hiding Models](#hiding-models)
* [Saving View State](#saved-state)
* [Grid Support](#grid-support)
* [Generating Helper Classes For Models](#annotations)
* [Sample App](/epoxy-sample)

<p align="center">
<img alt="Sample app demo gif" src="https://github.com/airbnb/epoxy/raw/master/epoxy-sample/epoxy_sample_app.gif" width="200" height="354" />
</p>

## Download

Gradle is the only supported build configuration, so just add the dependency to your project `build.gradle` file:

```groovy
  dependencies {
    compile 'com.airbnb.android:epoxy:1.1.0'
}
```

Optionally, if you want to use the [attributes for generated helper classes](#annotations) you must also provide the annotation processor as a dependency.
```groovy
buildscript {
  dependencies {
    classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
  }
}

apply plugin: 'android-apt'

dependencies {
  compile 'com.airbnb.android:epoxy:1.1.0'
  apt 'com.airbnb.android:epoxy-processor:1.1.0'
}
```

## Basic Usage

Create a class that extends `EpoxyAdapter` and add an instance of your adapter to a `RecyclerView` as you normally would.

Create `EpoxyModels` and add them to the adapter in the order you want them displayed. The base `EpoxyAdapter` will handle inflating your views and binding them to your models.

In this example our `PhotoAdapter` starts off showing just a title header and a loading indicator. It has a method to add photos, which might be called as photos are loaded from a network request.

```java
public class PhotoAdapter extends EpoxyAdapter {
    private final LoaderModel loaderModel = new LoaderModel();

    public PhotoAdapter() {
        addModels(new HeaderModel("My Photos"), loaderModel);
    }

    public void addPhotos(Collection<Photo> photos) {
        hideModel(loaderModel);
        for (Photo photo : photos) {
            insertModelBefore(new PhotoModel(photo), loaderModel);
        }
    }
}
```

## Epoxy Models

The `EpoxyAdapter` uses a list of `EpoxyModels` to know which views to display and in what order. You should subclass `EpoxyModel` to specify what layout your model uses and how to bind data to that view.

For example, the `PhotoModel` in the above example could be created like so

```java
public class PhotoModel extends EpoxyModel<PhotoView> {

  private final Photo photo;

  public PhotoModel(Photo photo) {
    this.photo = photo;
    id(photo.getId());
  }

  @LayoutRes
  public int getDefaultLayout() {
    return R.layout.view_model_photo;
  }

  @Override
  public void bind(PhotoView photoView) {
    photoView.setUrl(photo.getUrl());
  }
  
  @Override
  public void unbind(PhotoView photoView) {
    photoView.clear();
  }
}
```

In this case the PhotoModel is typed with `PhotoView`, so the `getDefaultLayout` method must return a layout resource that will inflate into a PhotoView. The file `R.layout.view_model_photo` might look like this

```
<?xml version="1.0" encoding="utf-8"?>
<PhotoView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="120dp"
    android:padding="16dp" />
```

Epoxy works well with custom views - in this pattern the model holds the data and passes it to the view, the layout file describes which view to use and how to style it, and the view itself handles displaying the data. This is a bit different from the normal ViewHolder pattern, and allows for a separation of data and view logic.

Models also allow you to control other aspects of the view, such as the span size, id, saved state, and whether the view should be shown. Those aspects of models are described in detail below.

## <a name="models-list"/>Modifying the Models List

Subclasses of EpoxyAdapter have access to the `models` field, a `List<EpoxyModel<?>>` which specifies what models to show and in what order. The list starts off empty, and subclasses should add models to this list, and otherwise modify it as they see fit, in order to build their view. 

Every time the list is modified you must notify the changes with the standard RecyclerView methods - `notifyDataSetChanged()`, `notifyItemInserted()`, etc. As always with RecyclerView, `notifyDataSetChanged()` should be avoided in favor of more specific methods like `notifyItemInserted()` when possible. 

Helper methods such as `EpoxyAdapter#addModels(EpoxyModel<?>...)` exist that will modify the list and notify the proper change for you. Alternatively, you may choose to leverage Epoxy's [automatic diffing](#diffing) to avoid the overhead of manually notifying item changes.

The example from the [Basic Usage](#basic-usage) section uses these helper methods, but could be changed to instead access the models list directly like so:
```java
public class PhotoAdapter extends EpoxyAdapter {
    private final LoaderModel loaderModel = new LoaderModel();

    public PhotoAdapter() {
      models.add(new HeaderModel("My Photos"));
      models.add(loaderModel);
      notifyItemRangeInserted(0,2);
    }

    public void addPhotos(Collection<Photo> photos) {
      for (Photo photo : photos) {
        int loaderPosition = models.size() - 1;
        models.add(loaderPosition, photo);
        notifyItemInserted(loaderPosition);
      }
    }
  }
```

Having direct access to the models list allows complete flexibility in how you arrange and rearrange your models as needed.

Once the models list is modified and the changes notified, EpoxyAdapter will reference the list in order to create and bind the appropriate view for each model.

## <a name="diffing"/>Automatic Diffing

Epoxy is especially useful for screens that have many view types backed by a complex data structure. In these cases, data may be updated via network requests, asynchronous observables, user inputs, or other sources that would require you to update your models and notify the proper changes to the adapter. 

Tracking all of these changes manually is difficult and adds significant overhead to do correctly. In these cases you can leverage Epoxy's automatic diffing to reduce the overhead, while also efficiently only updating the views that changed.

To enable diffing, call `enableDiffing()` in the constructor of your EpoxyAdapter subclass. Then simply call `notifyModelsChanged()` after modifying your models list to let the diffing algorithm figure out what changed. This will dispatch the appropriate calls to insert, remove, change, or move your models, batching as necessary.

For this to work you must leave stable ids set to true (this is [the default](#model-ids)) as well as implement `hashCode()` on your models to completely define the state of the model. This hash is used to detect when data on a model is changed.

You may mix usage of normal notify calls, such as `notifyItemInserted()`, along with `notifyModelsChanged()` when you know specifically what changed, as that will be more efficient than relying on the diffing algorithm.

A common usage pattern of this is to have a method on your adapter that updates the models according to a state object. Here is a very simple example. In practice you may have many more models, hide or show models, insert new models, involve click listeners, etc.

```java
public class MyAdapter extends EpoxyAdapter {
    private final HeaderModel headerModel = new HeaderModel();
    private final BodyModel bodyModel = new BodyModel();
    private final FooterModel footerModel = new FooterModel();

    public MyAdapter() {
        enableDiffing();
    
        addModels(
            headerModel,
            bodyModel,
            footerModel
        );
    }

    public void setData(MyDataClass data) {
        headerModel.setData(data.headerData());
        bodyModel.setData(data.bodyData());
        footerModel.setData(data.footerData());

        notifyModelsChanged();
    }
}
```

To avoid the manual overhead and boilerplate of implementing `hashCode()` on all your models you may use the [@ModelAttribute](#annotations) annotation on model fields to generate that code for you.

When using diffing there are a few performance pitfalls to be aware of.

First, diffing must process all models in your list, and so may affect performance for cases of more than hundreds of models. The diffing algorithm performs in linear time for most cases, but still must process all models in your list. Item moves are slow however, and in the worse case of shuffling all the models in the list the performance is (n^2)/2. 

Second, each diff must recompute each model's hashcode in order to determine item changes. Avoid including unnecessary computation in your hash codes as that can significantly slow down the diff.

Third, beware of changing model state unintentionally, such as with click listeners. For example, it is common to set a click listener on a model, which would then be set on a view when bound. An easy mistake here is using anonymous inner classes as click listeners, which would affect the model hashcode and require the view to be rebound when the model is updated or recreated. Instead, you can save a listener as a field to reuse with each model so that it does not change the model's hashcode. Another common mistake is modifying model state that affects the hashcode during a model's bind call.

With these considerations in mind, avoid calling `notifyModelsChanged()` unnecessarily and batch your changes as much as possible. For very long lists of models, or for cases with many item moves, you may prefer to use manual notifications over automatic diffing in order to prevent frame drops. That being said, diffing is fairly fast and we have used it with up to 600 models with negligible performance impact. As always, profile your code and make sure it works for your specific situation.

A note about the algorithm - We are using a custom diffing algorithm that we wrote in house. The Android Support Library class `DiffUtil` was released after we completed this work. We continue to use our original algorithm because in our tests it is roughly 35% faster than the DiffUtil. However, it does make some optimizations that use more memory than DiffUtil. We value the speed increase, but in the future may add the option to choose which algorithm you use.

## Binding Models

Epoxy uses the layout resource id provided by `EpoxyModel#getLayout()` to create a view for that model. When `RecyclerView.Adapter#onBindViewHolder(ViewHolder holder, int position)` is called, the `EpoxyAdapter` looks up the model at the given position and calls `EpoxyModel#bind(View)` with the inflated view. You may override this bind call in your model to update the view with whatever data you have set in your model.

Since RecyclerView reuses views when possible, a view may be bound multiple times. You should make sure that your usage of `EpoxyModel#bind(View)` completely updates the view according to the data in your model.

When the view is recycled, `EpoxyAdapter` will call `EpoxyModel#unbind(View)`, giving you a chance to release any resources associated with the view. This is a good opportunity to clear the view of large or expensive data such as bitmaps.

If the recycler view provided a non empty list of payloads with `onBindViewHolder(ViewHolder holder, int position, List<Object> payloads)`, then `EpoxyModel#bind(View, List<Object>)` will be called instead so that the model can be optimized to rebind according to what changed. This can help you prevent unnecessary layout changes if only part of the view changed.

## Model IDs

The RecyclerView concept of stable ideas is built into EpoxyModels, and the system works best when stable ids are enabled.

Every time a model is instantiated it is automatically assigned a unique id. You may override this id with the `id(long)` method, which is often useful for models that represent objects from a database which would already have an id associated with them. 

The default ids are always negative values so that they are less likely to clash with manually set ids. When using a model with a default id it is often helpful to save that model as a field in the adapter, so that the model and id are unique and constant for the lifetime of the adapter. This is common for more static views like headers, whereas dynamic content loaded from a server is likely to use a manual id.

Using stable ids are highly recommended, but not required. By default the `EpoxyAdapter` sets `setHasStableIds` to true in its constructor, but you may set it to false in your subclass's constructor if desired.

The adapter relies on stable ids for saving view state and for automatic diffing. You must leave stable ids enabled to use these features. The combination of stable ids and diffing allows for fairly good item animations with no extra work on your part.

## Specifying Layouts

The only method that an `EpoxyModel` must implement is `getDefaultLayout`. This method specifies what layout resource should be used by the adapter when creating a view holder for that model. The layout resource id also acts as the view type for the `EpoxyModel`, so that views sharing a layout can be recycled. The type of View inflated by the layout resource should be the parameterized type of the `EpoxyModel`, so that the proper View type is passed to the model's `bind` method.

If you want to dynamically change which layout is used for your model you can call `EpoxyModel#layout(layoutRes)` with the new layout id. This allows you to easily change the style of the view, such as size, padding, etc. This is useful if you want to reuse the same model, but alter the view's style based on where it is used, eg landscape vs portrait or phone vs tablet.

## Hiding Models

If you want to remove a view from the Recycler View you can either remove its model from the list, or just set the model to hidden. Hiding a model is useful for cases where a view is conditionally shown and you want an easy way to toggle between showing and hiding it.

You may hide it by calling `model.hide()` and show it by calling `model.show()`, or use the conditional `model.show(boolean)`.

Hidden models are technically still in the RecyclerView, but they are changed to use an empty layout that takes up no space. This means that changing the visibility of a model _must_ be accompanied by an appropriate `notifyItemChanged` call to the adapter.

There are helper methods on the adapter, such as `EpoxyAdapter#hideModel(model)`, that will set the model's visibility and then notify the item change for you if the visibility changed.

## Saved State

RecyclerView does not support saving the view state of its children the way a normal ViewGroup would. EpoxyAdapter adds this missing support by managing the saved state of each view on its own.

Saving view state is useful for cases where the view is modified by the user, such as checkboxes, edit texts, expansion/collapse, etc. These can be considered transient state that the model doesn't need to know about.

To enable this support you must have stable ids enabled. Then, override `EpoxyModel#shouldSaveViewState` and return true on each model whose state should be saved. When this is enabled, `EpoxyAdapter` will manually call `View#saveHierarchyState` to save the state of the view when it is unbound. That state is restored when the view is bound again. This will save the state of the view as it is scrolled off screen and then scrolled back on screen.

To save the state across separate adapter instances you must call `EpoxyAdapter#onSaveInstanceState` (eg in your activity's `onSaveInstanceState` method), and then restore it with `EpoxyAdapter#onRestoreInstanceState` once the adapter is created again.

Since a view's state is associated with its model id, the model _must_ have a constant id across adapter instances. This means you should manually set an id on models that are using saved state.

## Grid Support

EpoxyAdapter can be used with RecyclerView's `GridLayoutManager` to allow `EpoxyModels` to change their span size. `EpoxyModels` can claim various span sizes by overriding `int getSpanSize(int totalSpanCount, int position, int itemCount)` to vary their span size based on the span count of the layout manager as well as the model's position in the adapter. `EpoxyAdapter.getSpanSizeLookup()` returns a span size lookup object that delegates lookup calls to each EpoxyModel.

```
int spanCount = 2;
GridLayoutManager layoutManager = new GridLayoutManager(getContext(), spanCount);
epoxyAdapter.setSpanCount(spanCount);
layoutManager.setSpanSizeLookup(epoxyAdapter.getSpanSizeLookup());
```

## <a name="annotations"/>Generating helper classes with `@EpoxyAttribute`

You can reduce boilerplate in you model classes by using the EpoxyAttribute annotation to generate a subclass of your model with setters, getters, equals, and hashcode.

For example, you may set up a model like this:
```java
public class HeaderModel extends EpoxyModel<HeaderView> {
    @EpoxyAttribute String title;
    @EpoxyAttribute String subtitle;
    @EpoxyAttribute String description;
    
    @LayoutRes
    public int getDefaultLayout() {
        return R.layout.view_model_header;
    }

    @Override
    public void bind(HeaderView view) {
        view.setTitle(title);
        view.setSubtitle(subtitle);
        view.setDescription(description);
    }
}
```

A `HeaderModel_.java` class will be generated that subclasses HeaderModel, and you would use the generated class directly.

```java
models.add(new HeaderModel_()
    .title("My title")
    .subtitle("my subtitle")
    .description("my description"));
```

The setters return the model so that they can be used in a builder style. The generated class includes a `hashCode()` implementation for all of the annotated attributes so that the model can be used in [automatic diffing](#diffing).

The generated class will always be the name of the original class with an underscore appended at the end. If the original class is abstract then a class will not be generated for it. If a model class is subclassed from other models that also have EpoxyAttributes, the generated class will include all of the super class's attributes. The generated class will duplicate any constructors on the original model class. If the original model class has any method names that match generated setters then the generated method will call super.

This is an optional aspect of Epoxy that you may choose not to use, but it can be helpful in reducing the boilerplate in your models.

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
