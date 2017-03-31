
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