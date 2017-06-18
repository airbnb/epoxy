# 2.2.0 (June 19, 2017)
* **Main Feature** Models can now be completely generated from a custom view via annotations on the view. This should completely remove the overhead of creating a model manually in many cases! For more info, see [the wiki](https://github.com/airbnb/epoxy/wiki/Generating-Models-from-View-Annotations)

* **New** Lowered the minimum SDK from 16 to 14.
* **New** Models that have a `View.OnLongClickListener` as an EpoxyAttribute will now have an overloaded setter on the generated model that allows you to set a long click listener that will return the model, view, and adapter position. This is very similar to the `View.OnClickListener` support added in 2.0.0, but for long click listeners. **Upgrade Note** If you were setting a long click listener value to null anywhere you will need to now cast that to `View.OnLongClickListener` because of the new overloaded method.
* **New** `id` overload on EpoxyModel to define a model id with multiple strings
* **New** Option in `EpoxyAttribute` to not include the attribute in the generated `toString` method (Thanks to @geralt-encore!)
* **New** @AutoModel models are now inherited from usages in super classes (Thanks to @geralt-encore!)
* **Fixed** Generated getters could recursively call themselves (Thanks to @geralt-encore!)

# 2.1.0 (May 9, 2017)

* **New**: Support for Android Data Binding! Epoxy will now generate an EpoxyModel directly from a Data Binding xml layout, and handle all data binding details automatically. Thanks to @geralt-encore for helping with this! See more details in [the wiki](https://github.com/airbnb/epoxy/wiki/Data-Binding-Support).
* **New**: Support for Litho. Epoxy will now generate an EpoxyModel for Litho Layout Specs. See more details in [the wiki](https://github.com/airbnb/epoxy/wiki/Litho-Support).
* **New**: Support for implicitly adding AutoModels to an EpoxyController, this let's you drop the extra `.addTo(this)` line. More details and instructions [here](https://github.com/airbnb/epoxy/wiki/Epoxy-Controller#implicit-adding)

# 2.0.0 (March 25, 2017)

* **New**: The `EpoxyController` class helps you manage even models better. This should be used instead of the original `EpoxyAdapter` in most places. Read more about `EpoxyController` in [the wiki](https://github.com/airbnb/epoxy/wiki/Epoxy-Controller).
* **Change**: In the new EpoxyController, the diffing algorithm uses both `equals` and `hashCode` on each model to check for changes. This is a change from the EpoxyAdapter where only `hashCode` was used. Generated models have both hashCode and equals implemented properly already, but if you have any custom hashCode implementations in your models make sure you have equals implemented as well.
* **New**: Models that have a `View.OnClickListener` as an EpoxyAttribute will now have an overloaded setter on the generated model that allows you to set a click listener that will return the model, view, and adapter position. **Upgrade Note** If you were setting a click listener value to null anywhere you will need to now cast that to `View.OnClickListener` because of the new overloaded method.
* **New**: Attach an onBind/onUnbind listener directly to a model instead of overriding the onModelBound method. Generated models will have methods created to set this listener and handle the callback for you.
* **New**: Support for creating models in Kotlin (Thanks to @geralt-encore! https://github.com/airbnb/epoxy/pull/144)
* **New**: `EpoxyModelWithView` supports creating a View programmatically instead of inflating from XML.
* **New**: `EpoxyModelGroup` supports grouping models together in arbitrary formations.
* **New**: Instead of setting attribute options like `@EpoxyAttribute(hash = false)` you should now do `@EpoxyAttribute(DoNotHash)`. You can also set other options like that.
* **New**: Annotation processor options can now be set via gradle instead of with `PackageEpoxyConfig`
* **New**: In an EpoxyController, if a model with the same id changes state Epoxy will include its previous state as a payload in the change notification. The new model will have its `bind(view, previouslyBoundModel)` method called so it can compare what changed since the previous model, and so it can update the view with only the data that changed.

# 1.7.5 (Feb 21, 2017)

* **New**: Models inherit layouts specified in superclass `@EpoxyModelClass` annotations [#119](https://github.com/airbnb/epoxy/pull/119)
* **New**: Support module configuration options [#124](https://github.com/airbnb/epoxy/pull/124)

# 1.6.2 (Feb 8, 2017)

* New: Support layout resource annotations in library projects (https://github.com/airbnb/epoxy/pull/116)

# 1.6.1 (Feb 6, 2017)

* Allow the default layout resource to be specified in the EpoxyModelClass class annotation [(#109)](https://github.com/airbnb/epoxy/pull/109) [(#111)](https://github.com/airbnb/epoxy/pull/111)
* Allow the `createNewHolder` method to be omitted and generated automatically [(#105)](https://github.com/airbnb/epoxy/pull/105)
* Generate a subclass for abstract model classes if the EpoxyModelClass annotation is present [(#105)](https://github.com/airbnb/epoxy/pull/105)
* Allow strings as model ids [(#107)](https://github.com/airbnb/epoxy/pull/107)
* Add instructions to readme for avoiding memory leaks [(#106)](https://github.com/airbnb/epoxy/pull/106)
* Add model callbacks for view attached/detached from window, and onFailedToRecycleView [(#104)](https://github.com/airbnb/epoxy/pull/104)
* Improve documentation on model unbind behavior [(#103)](https://github.com/airbnb/epoxy/pull/103)
* Fix generated methods from super classes that have var args [(#100)](https://github.com/airbnb/epoxy/pull/100)
* Remove apt dependency [(#95)](https://github.com/airbnb/epoxy/pull/95)
* Add `removeAllModels` method to EpoxyAdapter [(#94)](https://github.com/airbnb/epoxy/pull/94)
* Use actual param names when generating methods from super classes [(#85)](https://github.com/airbnb/epoxy/pull/85)

# 1.5.0 (11/21/2016)

* Fixes models being used in separate modules
* Generates a `reset()` method on each model to reset annotated fields to their defaults.
* Changes `@EpoxyAttribute(hash = false)` to still differentiate between null and non null values in the hashcode implementation
* Adds a `notifyModelChanged` method to EpoxyAdapter that allows a payload to be specified
* Generates a `toString()` method on all generated model classes that includes the values of all annotated fields.

# 1.4.0 (10/13/2016)

* Optimizations to the diffing algorithm
* Setters on generated classes are not created if an @EpoxyAttribute field is marked as `final`
* Adds @EpoxyModelClass annotation to force a model to have a generated class, even if it doesn't have any @EpoxyAttribute fields
* Fix to not generate methods for package private @EpoxyAttribute fields that are in a different package from the generated class
* Have generated classes duplicate any super methods that have the model as the return type to help with chaining

# 1.3.0 (09/15/2016)

* Add support for using the view holder pattern with models. See the readme for more information.
* Throw an exception if `EpoxyAdapter#notifyDataSetChanged()` is called when diffing is enabled. It doesn't make sense to allow this alongside diffing, and calling this is most likely to be an accidental mixup with `notifyModelsChanged()`.
* Some performance improvements with the diffing algorithm.

# 1.2.0 (09/07/2016)

* Change signature of `EpoxyAdapter#onModelBound` to include the model position
* Fix EpoxyModel hashcode to include the layout specified by `getDefaultLayout`
* Enforce that the id of an `EpoxyModel` cannot change once it has been added to the adapter
* Add optional hash parameter to the `EpoxyAttribute` annotation to exclude a field from being included in the generated hashcode method.

# 1.1.0 (08/24/2016)

* Initial release
