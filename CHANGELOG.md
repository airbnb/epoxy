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
