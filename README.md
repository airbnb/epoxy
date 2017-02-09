# Epoxy

Epoxy is an Android library for building complex screens in a RecyclerView. It abstracts the boilerplate of view holders, item types, item ids, span counts, and more, in order to simplify building screens with multiple view types. Additionally, Epoxy adds support for saving view state and automatic diffing of item changes.

[We developed Epoxy at Airbnb](https://medium.com/airbnb-engineering/epoxy-airbnbs-view-architecture-on-android-c3e1af150394#.xv4ymrtmk) to simplify the process of working with RecyclerViews, and to add the missing functionality we needed. We now use Epoxy for most of the main screens in our app and it has improved our developer experience greatly.

* [Installation](#installation)
* [Basic Usage](#basic-usage)
* [Epoxy Models](#epoxy-models)
* [Modifying the Models List](#models-list)
* [Automatic Diffing](#diffing)
* [Binding Models](#binding-models)
* [Avoiding Memory Leaks](#memory-leaks)
* [Using View Holders](#view-holders)
* [Model IDs](#model-ids)
* [Specifying Layouts](#specifying-layouts)
* [Hiding Models](#hiding-models)
* [Saving View State](#saved-state)
* [Grid Support](#grid-support)
* [Generating Models via Annotations](#annotations)
* [Sample App](/epoxy-sample)

<p align="center">
<img alt="Sample app demo gif" src="https://github.com/airbnb/epoxy/raw/master/epoxy-sample/epoxy_sample_app.gif" width="200" height="354" />
</p>

## Installation

Gradle is the only supported build configuration, so just add the dependency to your project `build.gradle` file:

```groovy
dependencies {
  compile 'com.airbnb.android:epoxy:1.6.2'
}
```

Optionally, if you want to use the [attributes for generated helper classes](#annotations) you must also provide the annotation processor as a dependency.
```groovy
dependencies {
  compile 'com.airbnb.android:epoxy:1.6.2'
  annotationProcessor 'com.airbnb.android:epoxy-processor:1.6.2'
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

In this case the `PhotoModel` is typed with `PhotoView`, so the `getDefaultLayout()` method must return a layout resource that will inflate into a `PhotoView`. The file `R.layout.view_model_photo` might look like this

```xml
<?xml version="1.0" encoding="utf-8"?>
<PhotoView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="120dp"
    android:padding="16dp" />
```

Epoxy works well with custom views - in this pattern the model holds the data and passes it to the view, the layout file describes which view to use and how to style it, and the view itself handles displaying the data. Alternatively, you can opt to [use the view holder pattern](#view-holders).

Models also allow you to control other aspects of the view, such as the span size, id, saved state, and whether the view should be shown. Those aspects of models are described in detail below.

## <a name="models-list"/>Modifying the Models List

Subclasses of `EpoxyAdapter` have access to the `models` field, a `List<EpoxyModel<?>>` which specifies what models to show and in what order. The list starts off empty, and subclasses should add models to this list, and otherwise modify it as they see fit, in order to build their view. 

Every time the list is modified you must notify the changes with the standard `RecyclerView` methods - `notifyDataSetChanged()`, `notifyItemInserted()`, etc. As always with RecyclerView, `notifyDataSetChanged()` should be avoided in favor of more specific methods like `notifyItemInserted()` when possible. 

Helper methods such as `EpoxyAdapter#addModels(EpoxyModel<?>...)` exist that will modify the list and notify the proper change for you. Alternatively, you may choose to leverage Epoxy's [automatic diffing](#diffing) to avoid the overhead of manually notifying item changes.

The example from the [Basic Usage](#basic-usage) section uses these helper methods, but could be changed to instead access the models list directly like so:

```java
public class PhotoAdapter extends EpoxyAdapter {
  private final LoaderModel loaderModel = new LoaderModel();

  public PhotoAdapter() {
    models.add(new HeaderModel("My Photos"));
    models.add(loaderModel);
    notifyItemRangeInserted(0, 2);
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

Once the models list is modified and the changes notified, `EpoxyAdapter` will reference the list in order to create and bind the appropriate view for each model.

## <a name="diffing"/>Automatic Diffing

Epoxy is especially useful for screens that have many view types backed by a complex data structure. In these cases, data may be updated via network requests, asynchronous observables, user inputs, or other sources that would require you to update your models and notify the proper changes to the adapter. 

Tracking all of these changes manually is difficult and adds significant overhead to do correctly. In these cases you can leverage Epoxy's automatic diffing to reduce the overhead, while also efficiently only updating the views that changed.

To enable diffing, call `enableDiffing()` in the constructor of your EpoxyAdapter subclass. Then simply call `notifyModelsChanged()` after modifying your models list to let the diffing algorithm figure out what changed. This will dispatch the appropriate calls to insert, remove, change, or move your models, batching as necessary.

For this to work you must leave stable ids set to `true` (this is [the default](#model-ids)) as well as implement `hashCode()` on your models to completely define the state of the model. This hash is used to detect when data on a model is changed.

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
      footerModel);
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

Note, if you are [using view holders](#view-holders) then this process is slightly different. Instead, the adapter will pass the inflated view to your view holder and the view holder will be bound to the model.

When the view is recycled, `EpoxyAdapter` will call `EpoxyModel#unbind(View)`, giving you a chance to release any resources associated with the view. This is a good opportunity to clear the view of large or expensive data such as bitmaps.

Since RecyclerView reuses views when possible, a view may be bound multiple times, without `unbind` necessarily being called in between. You should make sure that your usage of `EpoxyModel#bind(View)` completely updates the view according to the data in your model.

If the recycler view provided a non empty list of payloads with `onBindViewHolder(ViewHolder holder, int position, List<Object> payloads)`, then `EpoxyModel#bind(View, List<Object>)` will be called instead so that the model can be optimized to rebind according to what changed. This can help you prevent unnecessary layout changes if only part of the view changed.

## <a name="memory-leaks"/>Avoiding Memory Leaks
There are two possible memory leaks if you reuse an adapter with different RecyclerViews. A common case of this is creating and saving an adapter as a field in a Fragment's `onCreate` method, and reusing it across multiple view creation/destroy cycles if the fragment is put on the backstack or has its instance retained across rotation.

#### Child Views

Epoxy holds a reference to every bound view in order to allow state saving. To prevent leaking these, simply make sure the RecyclerView recycles all of its child views when you are done with it. One way to do this is to detach the adapter from the RecyclerView via `recyclerView.setAdapter(null)` (possibly in a fragment's `onDestroyView` method).

The downside to this approach is that the view is immediately cleared, so if you are animating your screen out it will go blank before the animation finishes. A better option that avoids this is to have your `LayoutManager` recycle its children when the RecyclerView is detached from the window. `LinearLayoutManager` and `GridLayoutManager` will do this for you if you enable `setRecycleChildrenOnDetach(true)`.

To automatically apply this you may wish to create a base adapter in your project that extends EpoxyAdapter.

```java
public class BaseAdapter extends EpoxyAdapter {

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {

            // This will force all models to be unbound and their views recycled once the RecyclerView is no longer in use. We need this so resources
            // are properly released, listeners are detached, and views can be returned to view pools (if applicable).
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                ((LinearLayoutManager) recyclerView.getLayoutManager()).setRecycleChildrenOnDetach(true);
            }
        }
    }
```

#### Parent View

Another, very similar, issue leaks a reference to the RecyclerView itself. This case is intrinsic to all RecyclerView adapters, not just Epoxy.

The case where this happens is the same as above, when an adapter is kept after the RecyclerView is destroyed. When an adapter is set on a RecyclerView the RecyclerView registers an observer to listen for adapter item changes (`adapter.registerAdapterDataObserver(...)`). This is needed for the RecyclerView to know when adapter items have changed.

This observer is only removed when the adapter is detached from the RecyclerView (eg `recyclerView.setAdapter(null)`). With the common pattern of recreating a view in a fragment it is easy to not do this.

One option to avoid this is to detach your adapter when destroying the RecyclerView. This has the downside of immediately clearing the view as mentioned above.

The other option is to clear the reference to your adapter and create a new one each time you create a new RecyclerView.

It is easy to forget to do this though, and is hard to enforce in the project. I haven't found a better automated solution to this issue. Let me know if you have ideas!

## <a name="view-holders"/>Using View Holders

The basic usage of [Epoxy Models](#epoxy-models) describes how views are bound to a model. This works well if you use custom views, but for simpler views it can be more convenient to use the traditional view holder pattern.

To get started, first create a view holder class that extends `EpoxyHolder`. 

Then, have your model extend `EpoxyModelWithHolder` instead of the normal `EpoxyModel`. Your model class should be typed with your view holder type instead of the view type.

Your model must implement `createNewHolder()` to create a new view holder. This will be called by the adapter when a new view holder is needed. The layout resource provided by your model's `getDefaultLayout()` method will be inflated and passed to your view holder when it is created.

Here is an example from the [sample app](/epoxy-sample):

```java
public class ButtonModel extends EpoxyModelWithHolder<ButtonHolder> {
  @EpoxyAttribute @StringRes int text;
  @EpoxyAttribute OnClickListener clickListener;

  @Override
  protected int getDefaultLayout() {
    return R.layout.model_button;
  }

  @Override
  public void bind(ButtonHolder holder) {
    holder.button.setText(text);
    holder.button.setOnClickListener(clickListener);
  }

  @Override
  protected ButtonHolder createNewHolder() {
    return new ButtonHolder();
  }

  static class ButtonHolder extends EpoxyHolder {
    @BindView(R.id.button) Button button;
    
    @Override
    protected void bindView(View itemView) {
      ButterKnife.bind(this, itemView);
    }
  }
}
```

Alternatively you can allow Epoxy's annotation processor to generate the `createNewHolder` method for you, reducing the boilerplate in your model.

Just leave your model class abstract, annotate it with `@EpoxyClassModel` (see [Generating Helper Classes For Models](#annotations)), and don't implement `createNewHolder`. A subclass will be generated that implements the method for you. This implementation will create a new instance of your Holder class by calling the no argument constructor, which is the same as what is implemented manually in the example above.

Another helpful pattern is to create a base Holder class that all view holders in your app can extend. Your base class can use [ButterKnife](https://github.com/JakeWharton/butterknife) to bind its view so that subclasses don't explicitly need to.

For example your base class may look like this:

```java
public abstract class BaseEpoxyHolder extends EpoxyHolder {
  @CallSuper
  @Override
  protected void bindView(View itemView) {
    ButterKnife.bind(this, itemView);
  }
}
```

Applying these two patterns helps shorten our example model to just:

```java
@EpoxyModelClass(layout = R.layout.model_button)
public abstract class ButtonModel extends EpoxyModelWithHolder<ButtonHolder> {
  @EpoxyAttribute @StringRes int text;
  @EpoxyAttribute OnClickListener clickListener;

  @Override
  public void bind(ButtonHolder holder) {
    holder.button.setText(text);
    holder.button.setOnClickListener(clickListener);
  }

  static class ButtonHolder extends BaseEpoxyHolder {
    @BindView(R.id.button) Button button;
  }
}
```

## Model IDs

The RecyclerView concept of stable ideas is built into EpoxyModels, and the system works best when stable ids are enabled.

Every time a model is instantiated it is automatically assigned a unique id. You may override this id with the `id(long)` method, which is often useful for models that represent objects from a database which would already have an id associated with them. 

The default ids are always negative values so that they are less likely to clash with manually set ids. When using a model with a default id it is often helpful to save that model as a field in the adapter, so that the model and id are unique and constant for the lifetime of the adapter. This is common for more static views like headers, whereas dynamic content loaded from a server is likely to use a manual id.

Using stable ids are highly recommended, but not required. By default the `EpoxyAdapter` sets `setHasStableIds` to true in its constructor, but you may set it to false in your subclass's constructor if desired.

The adapter relies on stable ids for saving view state and for automatic diffing. You must leave stable ids enabled to use these features. The combination of stable ids and diffing allows for fairly good item animations with no extra work on your part.

Once a model has been added to an adapter its ID can no longer be changed. Doing so will throw an error. This allows the diffing algorithm to make several optimizations to avoid checking for removals, insertions, or moves if none have been made.

## Specifying Layouts

The only method that an `EpoxyModel` must implement is `getDefaultLayout`. This method specifies what layout resource should be used by the adapter when creating a view holder for that model. The layout resource id also acts as the view type for the `EpoxyModel`, so that views sharing a layout can be recycled. The type of View inflated by the layout resource should be the parametrized type of the `EpoxyModel`, so that the proper View type is passed to the model's `bind` method.

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

```java
int spanCount = 2;
GridLayoutManager layoutManager = new GridLayoutManager(getContext(), spanCount);
epoxyAdapter.setSpanCount(spanCount);
layoutManager.setSpanSizeLookup(epoxyAdapter.getSpanSizeLookup());
```

## <a name="annotations"/>Generating Models via Annotations

You can reduce boilerplate in your model classes by using the `EpoxyModelClass` and `EpoxyAttribute` annotations to generate a subclass of your model.

For example, you may set up a model like this:

```java
@EpoxyModelClass(layout = R.layout.view_model_header)
public abstract class HeaderModel extends EpoxyModel<HeaderView> {
  @EpoxyAttribute String title;
  @EpoxyAttribute String subtitle;
  @EpoxyAttribute(hash=false) View.OnClickListener clickListener;

  @Override
  public void bind(HeaderView view) {
    view.setTitle(title);
    view.setSubtitle(subtitle);
    view.setOnClickListener(clickListener);
  }
}
```

A `HeaderModel_.java` class will be generated that subclasses HeaderModel, and you would use the generated class directly.

```java
models.add(new HeaderModel_()
    .title("My title")
    .subtitle("My subtitle")
    .clickListener(headerClickListener);
```

The generated class will always be the name of the original class with an underscore appended at the end.

A class will be generated if a model class has either a `EpoxyModelClass` annotation, and/or at least one field annotated with `EpoxyAttribute`. There are cases where you may want only one of these annotations, or both.

These annotations are optional, but are highly recommended for simplifying your models.

#### EpoxyAttribute

Annotate fields in your model with `EpoxyAttribute` to have a getter and setter generated for them. In addition, all fields annotated with `EpoxyAttribute` will be aggregated to create the following methods:

1. equals
2. hashCode
3. toString
4. reset (resets each annotated field to its default value)

This is especially useful when using [automatic diffing](#diffing) so you don't have to manually write a `hashCode` implementation.

The setters return the model so that they can be chained in a builder style.

Sometimes, you may not want an annotated field to be included in your hashCode method. A common case is a callback as an anonymous class that changes each time the model is created; in this situation its function is the same and does not represent a state change in the model. Add `hash=false` to the annotation to tell Epoxy to not use that field's value when calculating hashCode. Instead, hashCode will use a boolean value of true or false depending on whether the field is null or non null. This way the field will only change the model's state if it changes between set and unset.

If a model class is subclassed from other models that also have EpoxyAttributes, the generated class will include all of the super classes' attributes.

The generated class will duplicate any constructors on the original model class.

The generated class will also duplicate any methods on super classes that have a return type of the model class. This allows those method calls to be chained along with methods on the generated class. `super` will be called in all of these generated methods, and the return type is changed to be the generated class.

If the model class is abstract, and only has `EpoxyAttribute` annotations, a generated class will not be created for it. In this case `EpoxyModelClass` will have to be used as well.

#### EpoxyModelClass

A model class annotated with `@EpoxyModelClass` will always have a subclass generated. There are several cases where it may be useful to use this alongside, or instead of, `EpoxyAttribute`.

1. `getDefaultLayout` may be left unimplemented and the default layout resource can instead be specified as a parameter to the `EpoxyModelClass` annotation. The generated model will include a `getDefaultLayout` implementation that returns that value.
  * **Note**: This works with library projects, but you'll need to use final layout values in the annotation. I recommend using Butterknife's gradle plugin to generate an R2 class to do this easily.

2. If you are using `EpoxyModelWithHolder` (see [Using View Holders](#view-holders)) you can leave the `createNewHolder` method unimplemented and the generated class will contain a default implementation that creates a new holder by calling a no argument constructor of the holder class.

3. Having your model class be abstract signals to other developers that the class should not be instantiated directly, and that the generated model should be used instead. This may prevent accidentally instantiating the base class instead of the generated class. For larger projects this can be a good pattern to establish for all models.

4. If a class does not have any `@EpoxyAttribute` annotations itself, but one of its super classes does, it would not normally have a class generated for it. Using `@EpoxyModelClass` on the subclass is the only way to generate a model in that case.


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
