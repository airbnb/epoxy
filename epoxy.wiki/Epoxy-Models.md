* [Model IDs](#model-ids)
* [Binding Models](#binding-models)
* [Specifying Layouts](#specifying-layouts)
* [Using View Holders](#view-holders)
* [Generating Models via Annotations](#annotations)
* [Programmatic View Creation](*programmatic-view-creation)
* [Click Listeners](#click-listeners)
* [Grouping Models](#grouping-models)

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

## Binding Models

Epoxy uses the layout resource id provided by `EpoxyModel#getLayout()` to create a view for that model. When `RecyclerView.Adapter#onBindViewHolder(ViewHolder holder, int position)` is called, the `EpoxyAdapter` looks up the model at the given position and calls `EpoxyModel#bind(View)` with the inflated view. You may override this bind call in your model to update the view with whatever data you have set in your model.

Note, if you are [using view holders](#view-holders) then this process is slightly different. Instead, the adapter will pass the inflated view to your view holder and the view holder will be bound to the model.

When the view is recycled, `EpoxyAdapter` will call `EpoxyModel#unbind(View)`, giving you a chance to release any resources associated with the view. This is a good opportunity to clear the view of large or expensive data such as bitmaps.

Since RecyclerView reuses views when possible, a view may be bound multiple times, without `unbind` necessarily being called in between. You should make sure that your usage of `EpoxyModel#bind(View)` completely updates the view according to the data in your model.

If the recycler view provided a non empty list of payloads with `onBindViewHolder(ViewHolder holder, int position, List<Object> payloads)`, then `EpoxyModel#bind(View, List<Object>)` will be called instead so that the model can be optimized to rebind according to what changed. This can help you prevent unnecessary layout changes if only part of the view changed.

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
