# 2.12.0 (April 18, 2018)

- **Breaking** Several updates to the Paging Library integration were made (https://github.com/airbnb/epoxy/pull/421)
    - The `PagingEpoxyController` class had the methods `setNumPagesToLoad` and `setPageSizeHint` removed
    - Page hints are now taken from the `Config` object off of the PagedList. See the `setConfig` javadoc for information on how config values are used: https://github.com/airbnb/epoxy/blob/master/epoxy-paging/src/main/java/com/airbnb/epoxy/paging/PagingEpoxyController.java#L220
    - Several tweaks were made to how the page size and prefetch distance affect model rebuilding. Take some time to make sure your config values make sense and produce good results for your use case
    - A crash on empty list was fixed (https://github.com/airbnb/epoxy/issues/420)

- **New** The [Paris](https://github.com/airbnb/paris) library is now officially supported to allow dynamically styling RecyclerView items though Epoxy models. See [the wiki](https://github.com/airbnb/epoxy/wiki/Paris-Integration-(Dynamic-Styling)) for more info.


# 2.11.0 (April 7, 2018)

- **Fix** Make databinding work with Android Studio 3.1 (https://github.com/airbnb/epoxy/pull/418)
- Make `EpoxyController#isBuildingModels` public (https://github.com/airbnb/epoxy/pull/406


# 2.10.0 (February 25, 2018)

- **Improved** Allow the `Model_` class suffix for models generated via `@ModelView` to be customized (https://github.com/airbnb/epoxy/pull/402 Big thanks to geralt-encore!)

# 2.9.0 (January 29, 2018)
- **Improved** Global defaults for EpoxyController settings. Set duplicate filtering and exception handlers for all your controllers. (https://github.com/airbnb/epoxy/pull/394)
- **Improved** Add `@NonNull` annotations in EpoxyModel for better Kotlin interop

- **Fixed** Model click listeners now rebind correctly on partial model diffs (https://github.com/airbnb/epoxy/pull/393)
- **Fixed** Update Android Paging library to fix placeholder support (Thanks @wkranich! https://github.com/airbnb/epoxy/pull/360)
- **Fixed** Improve error message for inaccessible private fields (https://github.com/airbnb/epoxy/pull/388)

# 2.8.0 (December 22, 2017)

- **New** Use `@ModelProp` directly on fields to avoid creating a setter (https://github.com/airbnb/epoxy/pull/343)
- **New** Set EpoxyRecyclerView item spacing via xml attribute (https://github.com/airbnb/epoxy/pull/364)
- **New** More flexibility over setting Carousel padding values (https://github.com/airbnb/epoxy/pull/369)
- **New** Allow custom EpoxyModelGroup root view (https://github.com/airbnb/epoxy/pull/370)

- **Fixed** Public visibility settings of the Carousel snap helper settings (https://github.com/airbnb/epoxy/pull/356)
- **Fixed** Add more nullability annotations to better support Kotlin
- **Fixed** Saving view state now works better (https://github.com/airbnb/epoxy/pull/367)

# 2.7.3 (November 21, 2017)

- **Fixed** When a model changed and a partial update was bound to an existing view the wrong values could be set for prop groups (https://github.com/airbnb/epoxy/pull/347)

# 2.7.2 (October 28, 2017)

- **Fixed** Using `EpoxyDataBindingPattern` could result in the wrong package being used for the BR class in generated models.

# 2.7.1 (October 24, 2017)
Several fixes:

- https://github.com/airbnb/epoxy/pull/332
- https://github.com/airbnb/epoxy/pull/329
- https://github.com/airbnb/epoxy/pull/330
- https://github.com/airbnb/epoxy/pull/331

# 2.7.0 (October 17, 2017)

* **New** If a `@ModelView` generated model has a custom base class the generated model will now inherit constructors from the base class (https://github.com/airbnb/epoxy/pull/315)
* **New** Use the `EpoxyDataBindingPattern` annotation to specify a naming pattern for databinding layouts. This removes the need to declare every databinding layout explicitly ([Wiki](https://github.com/airbnb/epoxy/wiki/Data-Binding-Support#automatic-based-on-naming-pattern) - https://github.com/airbnb/epoxy/pull/319)
* **New** If a view with `@ModelView` implements an interface then the generated model will implement a similar interface, enabling polymorphism with models. [Wiki](https://github.com/airbnb/epoxy/wiki/Generating-Models-from-View-Annotations#view-interfaces)

* **Improvement** `PagingEpoxyController` now has getters to access the underlying data lists (Thanks to @pcqpcq - https://github.com/airbnb/epoxy/pull/317)
* **Improvement** `EpoxyModelGroup` now supports partial rebinds (https://github.com/airbnb/epoxy/pull/316)

# 2.6.0 (October 10, 2017)
* **Improvement** If a `OnModelClickListener` is used it will not be called if a view is clicked while it is being removed or otherwise has no position (https://github.com/airbnb/epoxy/issues/293 - Thanks @niccorder!)

* **New** `EpoxyRecyclerView` and `Carousel` provide out of the box integration with Epoxy along with other enhancements over regular RecyclerView (https://github.com/airbnb/epoxy/wiki/EpoxyRecyclerView)
* **New** `EpoxyPagingController` provides integration with the Android Paging architecture component as well as normal, large lists of items (https://github.com/airbnb/epoxy/wiki/Large-Data-Sets)

#### Kotlin
* **Improvement** Disable kotlin extension function generation with the annotation processor flag `disableEpoxyKotlinExtensionGeneration` (https://github.com/airbnb/epoxy/pull/309)
* **Fix** If a model has a non empty constructor the generated extension function will now use it.


# 2.5.1 (October 2, 2017)
* **Fixed** The wrong import was being generated for models using a view holder in 2.5.0 (https://github.com/airbnb/epoxy/pull/294)
* **Fixed** Fix generated code failing to compile if a subclass of View.OnClickListener is used as an attribute (https://github.com/airbnb/epoxy/pull/296)


# 2.5.0 (September 14, 2017)
* **New Feature** Epoxy now generates a Kotlin DSL to use when building models in your EpoxyController! See [the wiki](https://github.com/airbnb/epoxy/wiki/Epoxy-Controller#usage-with-kotlin) for details
* **New Feature** You can use the `autoLayout` parameter in `@ModelView` instead of needing to create a layout resource for `defaultLayout`. Epoxy will then create your view programmatically (https://github.com/airbnb/epoxy/pull/282).

**Breaking**
* The `onSwipeProgressChanged` callback in `EpoxyTouchHelper` had a `Canvas` parameter added (https://github.com/airbnb/epoxy/pull/280). You will need to update any of your usages to add this. Sorry for the inconvenience; this will hopefully help you add better swipe animations.


# 2.4.0 (September 4, 2017)
* **Improvement** If you are setting options on a @ModelProp and have no other annotation parameters you can now omit the explicit `options = ` param name (https://github.com/airbnb/epoxy/pull/268)
* **Improvement** If you are using `@TextProp` you can now specify a default string via a string resource (https://github.com/airbnb/epoxy/pull/269)

* **Fixed** EpoxyModelGroup was not binding model click listeners correctly (https://github.com/airbnb/epoxy/pull/267)
* **Fixed** A model created with @ModelView could fail to compile if it had nullable prop overloads  (https://github.com/airbnb/epoxy/pull/274)

#### Potentially Breaking Fix
A model created with @ModelView with a click listener had the wrong setter name for the model click listener overload (https://github.com/airbnb/epoxy/pull/275)

If you were setting this you will need to update the setter name. If you were setting the click listener to null you may now have to cast it.


# 2.3.0 (August 16, 2017)
* **New** An `AfterPropsSet` annotation for use in `@ModelView` classes. This allows initialization work to be done after all properties are bound from the model. (https://github.com/airbnb/epoxy/pull/242)
* **New** Annotations `TextProp` and `CallbackProp`  as convenient replacements for `ModelProp`. (https://github.com/airbnb/epoxy/pull/260)
* **New** Easy support for dragging and swiping via the `EpoxyTouchHelper` class. https://github.com/airbnb/epoxy/wiki/Touch-Support
* **Change** Added the method `getRootView` to the view holder class in `EpoxyModelGroup` and made the bind methods on `EpoxyModelGroup` non final. This allows access to the root view of the group.
* **Change** Generated models will now inherit class annotations from the base class (https://github.com/airbnb/epoxy/pull/255 Thanks geralt-encore!)

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
