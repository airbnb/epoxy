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

Another option is to clear the reference to your adapter and create a new one each time you create a new RecyclerView.

A final option is to create a RecyclerView subclass that removes its adapter when it is detached from the window.

```java
TODO

```