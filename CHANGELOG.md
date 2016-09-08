# 1.2.0 (09/07/2016)

* Change signature of `EpoxyAdapter#onModelBound` to include the model position
* Fix EpoxyModel hashcode to include the layout specified by `getDefaultLayout`
* Enforce that the id of an `EpoxyModel` cannot change once it has been added to the adapter
* Add optional hash parameter to the `EpoxyAttribute` annotation to exclude a field from being included in the generated hashcode method.

# 1.1.0 (08/24/2016)

* Initial release
