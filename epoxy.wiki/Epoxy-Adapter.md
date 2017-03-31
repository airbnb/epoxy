* [Modifying the Models List](#models-list)
* [Automatic Diffing](#diffing)
* [Hiding Models](#hiding-models)

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



With these considerations in mind, avoid calling `notifyModelsChanged()` unnecessarily and batch your changes as much as possible. For very long lists of models, or for cases with many item moves, you may prefer to use manual notifications over automatic diffing in order to prevent frame drops. That being said, diffing is fairly fast and we have used it with up to 600 models with negligible performance impact. As always, profile your code and make sure it works for your specific situation.

## Hiding Models
// hidden models breaking for pull to refresh or multiple items in a row on grid

If you want to remove a view from the Recycler View you can either remove its model from the list, or just set the model to hidden. Hiding a model is useful for cases where a view is conditionally shown and you want an easy way to toggle between showing and hiding it.

You may hide it by calling `model.hide()` and show it by calling `model.show()`, or use the conditional `model.show(boolean)`.

Hidden models are technically still in the RecyclerView, but they are changed to use an empty layout that takes up no space. This means that changing the visibility of a model _must_ be accompanied by an appropriate `notifyItemChanged` call to the adapter.

There are helper methods on the adapter, such as `EpoxyAdapter#hideModel(model)`, that will set the model's visibility and then notify the item change for you if the visibility changed.
