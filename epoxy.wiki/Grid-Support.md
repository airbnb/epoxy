EpoxyAdapter can be used with RecyclerView's `GridLayoutManager` to allow `EpoxyModels` to change their span size. `EpoxyModels` can claim various span sizes by overriding `int getSpanSize(int totalSpanCount, int position, int itemCount)` to vary their span size based on the span count of the layout manager as well as the model's position in the adapter. `EpoxyAdapter.getSpanSizeLookup()` returns a span size lookup object that delegates lookup calls to each EpoxyModel.

```java
int spanCount = 2;
GridLayoutManager layoutManager = new GridLayoutManager(getContext(), spanCount);
epoxyAdapter.setSpanCount(spanCount);
layoutManager.setSpanSizeLookup(epoxyAdapter.getSpanSizeLookup());
```