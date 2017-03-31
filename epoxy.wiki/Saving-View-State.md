
RecyclerView does not support saving the view state of its children the way a normal ViewGroup would. EpoxyAdapter adds this missing support by managing the saved state of each view on its own.

Saving view state is useful for cases where the view is modified by the user, such as checkboxes, edit texts, expansion/collapse, etc. These can be considered transient state that the model doesn't need to know about.

To enable this support you must have stable ids enabled. Then, override `EpoxyModel#shouldSaveViewState` and return true on each model whose state should be saved. When this is enabled, `EpoxyAdapter` will manually call `View#saveHierarchyState` to save the state of the view when it is unbound. That state is restored when the view is bound again. This will save the state of the view as it is scrolled off screen and then scrolled back on screen.

To save the state across separate adapter instances you must call `EpoxyAdapter#onSaveInstanceState` (eg in your activity's `onSaveInstanceState` method), and then restore it with `EpoxyAdapter#onRestoreInstanceState` once the adapter is created again.

Since a view's state is associated with its model id, the model _must_ have a constant id across adapter instances. This means you should manually set an id on models that are using saved state.

*Note*: Saved state does not work properly if you set the same adapter on multiple RecyclerViews. In that case, any model ids that exist in multiple RecyclerView would cause state for those models to be shared across RecyclerView.